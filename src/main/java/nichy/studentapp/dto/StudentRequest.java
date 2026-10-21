package nichy.studentapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StudentRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email; // 👈 សម្រាប់បង្កើត User Login

    @NotBlank(message = "Password is required")
    private String password; // 👈 សម្រាប់បង្កើត User Login

    private Integer age;
    private String major;
}