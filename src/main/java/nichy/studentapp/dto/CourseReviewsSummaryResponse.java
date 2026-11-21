package nichy.studentapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseReviewsSummaryResponse {
    private Long courseId;
    private String courseTitle;
    private Double averageRating;
    private int totalReviews;
    private List<ReviewResponse> reviews;
}