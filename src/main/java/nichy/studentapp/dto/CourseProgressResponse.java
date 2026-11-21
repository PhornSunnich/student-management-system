package nichy.studentapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseProgressResponse {
    private Long studentId;
    private Long courseId;
    private String courseTitle;
    private long completedLessonsCount;
    private long totalLessonsCount;
    private Double progressPercentage;
    private List<LessonItemProgress> lessons;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonItemProgress {
        private Long lessonId;
        private String lessonTitle;
        private boolean completed;
        private LocalDateTime completedAt;
    }
}