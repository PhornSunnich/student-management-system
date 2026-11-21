package nichy.studentapp.service;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.CourseReviewsSummaryResponse;
import nichy.studentapp.dto.ReviewRequest;
import nichy.studentapp.dto.ReviewResponse;
import nichy.studentapp.entity.Course;
import nichy.studentapp.entity.Review;
import nichy.studentapp.entity.Student;
import nichy.studentapp.repository.CourseRepository;
import nichy.studentapp.repository.EnrollmentRepository;
import nichy.studentapp.repository.ReviewRepository;
import nichy.studentapp.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public ReviewResponse createOrUpdateReview(ReviewRequest request) {
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
        }

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with id: " + request.getStudentId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + request.getCourseId()));

        // ត្រូវតែជាសិស្សដែលបាន Enroll ក្នុង Course នេះជាមុនសិន
        boolean isEnrolled = enrollmentRepository
                .findByStudentIdAndCourseId(request.getStudentId(), request.getCourseId())
                .isPresent();

        if (!isEnrolled) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only enrolled students can review this course");
        }

        Review review = reviewRepository
                .findByStudentIdAndCourseId(request.getStudentId(), request.getCourseId())
                .orElseGet(() -> Review.builder()
                        .student(student)
                        .course(course)
                        .build());

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);
        return mapToReviewResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public CourseReviewsSummaryResponse getReviewsByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + courseId));

        List<Review> reviewEntities = reviewRepository.findByCourseIdOrderByCreatedAtDesc(courseId);

        List<ReviewResponse> reviewResponses = reviewEntities.stream()
                .map(this::mapToReviewResponse)
                .toList();

        double avg = reviewEntities.isEmpty()
                ? 0.0
                : reviewEntities.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        double roundedAvg = Math.round(avg * 10.0) / 10.0; // ឧទាហរណ៍៖ 4.3

        return CourseReviewsSummaryResponse.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .averageRating(roundedAvg)
                .totalReviews(reviewResponses.size())
                .reviews(reviewResponses)
                .build();
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found with id: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .studentId(review.getStudent() != null ? review.getStudent().getId() : null)
                .studentName(review.getStudent() != null ? review.getStudent().getName() : null)
                .courseId(review.getCourse() != null ? review.getCourse().getId() : null)
                .courseTitle(review.getCourse() != null ? review.getCourse().getTitle() : null)
                .createdAt(review.getCreatedAt())
                .build();
    }
}