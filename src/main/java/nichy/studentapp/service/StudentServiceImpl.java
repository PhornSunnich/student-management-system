package nichy.studentapp.service;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.StudentRequest;
import nichy.studentapp.dto.StudentResponse;
import nichy.studentapp.dto.UpdateProfileRequest;
import nichy.studentapp.entity.Student;
import nichy.studentapp.entity.User;
import nichy.studentapp.repository.StudentRepository;
import nichy.studentapp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public StudentResponse getStudentProfileByEmail(String email) {
        Student student = studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student profile not found for email: " + email));

        return mapToResponse(student);
    }

    @Override
    @Transactional
    public StudentResponse saveOrUpdateProfile(String email, StudentRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with email: " + email));

        Student student = studentRepository.findByUserEmail(email)
                .orElse(new Student());

        student.setName(request.getName());
        student.setAge(request.getAge());
        student.setMajor(request.getMajor());
        student.setUser(user);

        Student savedStudent = studentRepository.save(student);
        return mapToResponse(savedStudent);
    }

    @Override
    @Transactional
    public StudentResponse updateMyProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with email: " + email));

        Student student = studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student profile not found for email: " + email));

        if (request.getName() != null && !request.getName().isBlank()) {
            student.setName(request.getName().trim());
        }
        if (request.getAge() != null) {
            if (request.getAge() < 1 || request.getAge() > 120) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid age");
            }
            student.setAge(request.getAge());
        }
        if (request.getMajor() != null) {
            student.setMajor(request.getMajor().trim());
        }

        // ប្ដូរ Password ថ្មី ប្រសិនបើមានបញ្ជូន newPassword មក
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Current password is required to set a new password");
            }

            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Current password is incorrect");
            }

            if (request.getNewPassword().length() < 6) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "New password must be at least 6 characters");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
        }

        Student updatedStudent = studentRepository.save(student);
        return mapToResponse(updatedStudent);
    }

    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encode BCrypt ឱ្យត្រឹមត្រូវ
        user.setRole("STUDENT");

        Student student = Student.builder()
                .name(request.getName())
                .age(request.getAge())
                .major(request.getMajor())
                .user(user)
                .build();

        Student savedStudent = studentRepository.save(student);
        return mapToResponse(savedStudent);
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found with id: " + id));
        return mapToResponse(student);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found with id: " + id));

        student.setName(request.getName());
        student.setAge(request.getAge());
        student.setMajor(request.getMajor());

        Student updatedStudent = studentRepository.save(student);
        return mapToResponse(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    public StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .name(student.getName())
                .age(student.getAge())
                .major(student.getMajor())
                .email(student.getUser() != null ? student.getUser().getEmail() : null)
                .role(student.getUser() != null ? student.getUser().getRole() : null)
                .build();
    }
}