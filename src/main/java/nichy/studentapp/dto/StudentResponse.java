package nichy.studentapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentResponse {
    private Long id;
    private String name;
    private String email;
    private String course;
    private Integer age;
}
