package nichy.studentapp.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private Integer age;
    private String major;
    private String phone;

    // Optional: ប្រើតែពេលចង់ប្ដូរ Password ថ្មី
    private String currentPassword;
    private String newPassword;
}