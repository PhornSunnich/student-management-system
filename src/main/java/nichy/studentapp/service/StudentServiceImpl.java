package nichy.studentapp.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.StudentRequest;
import nichy.studentapp.dto.StudentResponse;
import nichy.studentapp.entity.Student;
import nichy.studentapp.entity.User;
import nichy.studentapp.repository.StudentRepository;
import nichy.studentapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Override
    public StudentResponse getStudentProfileByEmail(String email) {
        Student student = studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for email: " + email));

        return mapToResponse(student);
    }

    @Override
    public StudentResponse saveOrUpdateProfile(String email, StudentRequest request) {
        // 1. ស្វែងរក User តាម Email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // 2. ឆែកមើលថាតើ Student Profile មានរួចហើយ ឬនៅ?
        Student student = studentRepository.findByUserEmail(email)
                .orElse(new Student()); // បើអត់ទាន់មាន គឺបង្កើត Student ថ្មី

        // 3. កំណត់តម្លៃ Name, Age, Major និង Link ទៅ User
        student.setName(request.getName());
        student.setAge(request.getAge());
        student.setMajor(request.getMajor());
        student.setUser(user);

        Student savedStudent = studentRepository.save(student);
        return mapToResponse(savedStudent);
    }


    @Override
    public StudentResponse createStudent(StudentRequest request){
        // check email
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        // create User object for login
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole("STUDENT");

        // create Student object and connect to user
        Student student = Student.builder()
                .name(request.getName())
                .age(request.getAge())
                .major(request.getMajor())
                .user(user)
                .build();

        // save to database (Cascade and save User auto)
        Student savedStudent = studentRepository.save(student);
        return mapToResponse(savedStudent);
    }

    @Override
    public List<StudentResponse> getAllStudents(){
        return studentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        return mapToResponse(student);
    }


    @Override
    public StudentResponse updateStudent(Long id, StudentRequest request){
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        student.setName(request.getName());
        student.setAge(request.getAge());
        student.setMajor(request.getMajor());

        Student updatedStudent = studentRepository.save(student);
        return mapToResponse(updatedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    public StudentResponse mapToResponse(Student student){
        return StudentResponse.builder()
                .id(student.getId())
                .name(student.getName())
                .age(student.getAge())
                .major(student.getMajor())
                .email(student.getUser() != null? student.getUser().getEmail() : null)
                .role(student.getUser() != null ? student.getUser().getRole() : null)
                .build();
    }
}
