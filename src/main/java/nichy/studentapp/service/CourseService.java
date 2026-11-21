package nichy.studentapp.service;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.*;
import nichy.studentapp.entity.Category;
import nichy.studentapp.entity.Course;
import nichy.studentapp.entity.Enrollment;
import nichy.studentapp.entity.Student;
import nichy.studentapp.entity.User;
import nichy.studentapp.repository.CategoryRepository;
import nichy.studentapp.repository.CourseRepository;
import nichy.studentapp.repository.EnrollmentRepository;
import nichy.studentapp.repository.StudentRepository;
import nichy.studentapp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CategoryRepository categoryRepository; // បន្ថែម CategoryRepository

    // create new course
    public CourseResponse createCourse(CourseRequest request){
        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found!"));

        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setInstructor(instructor);

        // ពិនិត្យ និងភ្ជាប់ Category បើមានការបញ្ជូន categoryId មក
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found!"));
            course.setCategory(category);
        }

        Course savedCourse = courseRepository.save(course);
        return mapToCourseResponse(savedCourse);
    }

    // get all course
    public List<CourseResponse> getAllCourses(){
        return courseRepository.findAll().stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    // get course by id
    public CourseResponse getCourseById(Long id){
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + id));
        return mapToCourseResponse(course);
    }

    // Filter មើល Courses តាម Category ID
    public List<CourseResponse> getCoursesByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + categoryId);
        }
        return courseRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    // update course
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

        // បើមានការប្ដូរ ឬបន្ថែម Category ថ្មី
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found!"));
            course.setCategory(category);
        }

        Course updatedCourse = courseRepository.save(course);
        return mapToCourseResponse(updatedCourse);
    }

    // delete course
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

    public PagedResponse<CourseResponse> getCoursesPaged(String search, Long categoryId, Pageable pageable) {
        Page<Course> coursePage = courseRepository.searchAndFilterCourses(search, categoryId, pageable);

        List<CourseResponse> items = coursePage.getContent().stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());

        return PagedResponse.<CourseResponse>builder()
                .content(items)
                .pageNumber(coursePage.getNumber())
                .pageSize(coursePage.getSize())
                .totalElements(coursePage.getTotalElements())
                .totalPages(coursePage.getTotalPages())
                .first(coursePage.isFirst())
                .last(coursePage.isLast())
                .build();
    }

    // helper Mapping Methods
    private CourseResponse mapToCourseResponse(Course course){
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .instructorId(course.getInstructor() != null ? course.getInstructor().getId() : null)
                .instructorEmail(course.getInstructor() != null ? course.getInstructor().getEmail() : null)
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
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