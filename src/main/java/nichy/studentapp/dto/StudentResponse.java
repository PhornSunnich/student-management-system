package nichy.studentapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentResponse {
    private Long id;
    private String name;
    private Integer age;
    private String email;
    private String major;
    private String role;
}