package nichy.studentapp.service;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.CourseRequest;
import nichy.studentapp.dto.CourseResponse;
import nichy.studentapp.dto.EnrollmentRequest;
import nichy.studentapp.dto.EnrollmentResponse;
import nichy.studentapp.entity.Course;
import nichy.studentapp.entity.Enrollment;
import nichy.studentapp.entity.Student;
import nichy.studentapp.entity.User;
import nichy.studentapp.repository.CourseRepository;
import nichy.studentapp.repository.EnrollmentRepository;
import nichy.studentapp.repository.StudentRepository;
import nichy.studentapp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// for Business logic like create course and check enrollment
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    // create new course
    public CourseResponse createCourse(CourseRequest request){
        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found!"));
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setInstructor(instructor);

        Course savedCourse = courseRepository.save(course);
        return mapToCourseResponse(savedCourse);
    }

    // get all course
    public List<CourseResponse> getAllCourses(){
        return courseRepository.findAll().stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    // 🆕 get course by id
    public CourseResponse getCourseById(Long id){
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + id));
        return mapToCourseResponse(course);
    }

    // 🆕 update course
    public CourseResponse updateCourse(Long id, CourseRequest request){
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + id));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());

        // បើមានការប្ដូរ Instructor ថ្មី
        if (request.getInstructorId() != null) {
            User instructor = userRepository.findById(request.getInstructorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found!"));
            course.setInstructor(instructor);
        }

        Course updatedCourse = courseRepository.save(course);
        return mapToCourseResponse(updatedCourse);
    }

    // 🆕 delete course
    public void deleteCourse(Long id){
        if (!courseRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    // សម្រាប់ Student មើលតែរបស់ខ្លួនឯង
    public List<EnrollmentResponse> getEnrollmentsByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    // សម្រាប់ Admin មើលការចុះឈ្មោះទាំងអស់
    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    // student enroll
    public EnrollmentResponse enrollStudent(EnrollmentRequest request){
        // check student enrolled in course
        enrollmentRepository.findByStudentIdAndCourseId(request.getStudentId(), request.getCourseId())
                .ifPresent(e->{
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student is already enrolled in this course.");
                });
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setProgress(0.0);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return mapToEnrollmentResponse(savedEnrollment);
    }

    // helper Mapping Methods
    private CourseResponse mapToCourseResponse(Course course){
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .instructorId(course.getInstructor() != null ? course.getInstructor().getId() : null)
                .instructorEmail(course.getInstructor() != null ? course.getInstructor().getEmail() : null)
                .build();
    }

    private EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment){
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent() != null ? enrollment.getStudent().getId() : null)
                .studentName(enrollment.getStudent() != null ? enrollment.getStudent().getName() : null)
                .courseId(enrollment.getCourse() != null ? enrollment.getCourse().getId() : null)
                .courseTitle(enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : null)
                .progress(enrollment.getProgress())
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }
}