package nichy.studentapp.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long studentId;
    private Long courseId;
    private Integer rating;  // 1..5
    private String comment;
}