package nichy.studentapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponse {
    private Long studentId;
    private Long userId;
    private String email;
    private String name;
    private Integer age;
    private String major;
    private String phone;
    private boolean passwordUpdated;
}