package nichy.studentapp.dto;

import lombok.Data;

@Data
public class LessonRequest {
    private String title;
    private String videoUrl;
    private String content;
    private Long courseId;
}
