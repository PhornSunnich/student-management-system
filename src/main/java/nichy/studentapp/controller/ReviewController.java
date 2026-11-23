package nichy.studentapp.controller;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.CourseReviewsSummaryResponse;
import nichy.studentapp.dto.ReviewRequest;
import nichy.studentapp.dto.ReviewResponse;
import nichy.studentapp.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // POST /api/reviews (បង្កើត ឬ Update Review របស់សិស្ស)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('STUDENT', 'ROLE_STUDENT', 'ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<ReviewResponse> submitReview(
            Principal principal,
            @RequestBody ReviewRequest request
    ) {
        ReviewResponse response = reviewService.createOrUpdateReview(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET /api/reviews/course/{courseId} (មើល Reviews និង Average Rating របស់ Course)
    @GetMapping("/course/{courseId}")
    public ResponseEntity<CourseReviewsSummaryResponse> getCourseReviews(@PathVariable Long courseId) {
        return ResponseEntity.ok(reviewService.getReviewsByCourseId(courseId));
    }

    // DELETE /api/reviews/{reviewId}
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'ROLE_STUDENT', 'ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<String> deleteReview(
            Principal principal,
            @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("Review deleted successfully with id: " + reviewId);
    }
}