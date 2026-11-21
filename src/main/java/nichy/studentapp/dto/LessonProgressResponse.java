package nichy.studentapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgressResponse {
    private Long progressId;
    private Long studentId;
    private Long courseId;
    private Long lessonId;
    private String lessonTitle;
    private boolean completed;
    private LocalDateTime completedAt;
    private long completedLessonsCount;
    private long totalLessonsCount;
    private Double courseProgressPercentage;
}