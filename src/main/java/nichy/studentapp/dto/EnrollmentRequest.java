package nichy.studentapp.dto;

import lombok.Data;

@Data
public class EnrollmentRequest {
    private Long studentId;
    private Long courseId;
}
