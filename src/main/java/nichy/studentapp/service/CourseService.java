package nichy.studentapp.service;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.CourseRequest;
import nichy.studentapp.dto.CourseResponse;
import nichy.studentapp.dto.EnrollmentRequest;
import nichy.studentapp.dto.EnrollmentResponse;
import nichy.studentapp.dto.PagedResponse;
import nichy.studentapp.entity.Category;
import nichy.studentapp.entity.Course;
import nichy.studentapp.entity.Enrollment;
import nichy.studentapp.entity.Review;
import nichy.studentapp.entity.Student;
import nichy.studentapp.entity.User;
import nichy.studentapp.repository.CategoryRepository;
import nichy.studentapp.repository.CourseRepository;
import nichy.studentapp.repository.EnrollmentRepository;
import nichy.studentapp.repository.LessonRepository;
import nichy.studentapp.repository.ReviewRepository;
import nichy.studentapp.repository.StudentRepository;
import nichy.studentapp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final LessonRepository lessonRepository;

    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found!"));

        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setInstructor(instructor);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found!"));
            course.setCategory(category);
        }

        Course savedCourse = courseRepository.save(course);
        return mapToCourseResponse(savedCourse);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + id));
        return mapToCourseResponse(course);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + categoryId);
        }
        return courseRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + id));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());

        if (request.getInstructorId() != null) {
            User instructor = userRepository.findById(request.getInstructorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found!"));
            course.setInstructor(instructor);
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found!"));
            course.setCategory(category);
        }

        Course updatedCourse = courseRepository.save(course);
        return mapToCourseResponse(updatedCourse);
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EnrollmentResponse enrollStudent(EnrollmentRequest request) {
        enrollmentRepository.findByStudentIdAndCourseId(request.getStudentId(), request.getCourseId())
                .ifPresent(e -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student is already enrolled in this course.");
                });

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setProgress(0.0);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return mapToEnrollmentResponse(savedEnrollment);
    }

    @Transactional(readOnly = true)
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

    private CourseResponse mapToCourseResponse(Course course) {
        List<Review> reviews = reviewRepository.findByCourseIdOrderByCreatedAtDesc(course.getId());
        int reviewCount = reviews.size();
        double averageRating = reviews.isEmpty()
                ? 0.0
                : Math.round(reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0) * 10.0) / 10.0;

        int lessonCount = (int) lessonRepository.countByCourseId(course.getId());

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .instructorId(course.getInstructor() != null ? course.getInstructor().getId() : null)
                .instructorEmail(course.getInstructor() != null ? course.getInstructor().getEmail() : null)
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                .averageRating(averageRating)
                .reviewCount(reviewCount)
                .lessonCount(lessonCount)
                .build();
    }

    private EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment) {
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