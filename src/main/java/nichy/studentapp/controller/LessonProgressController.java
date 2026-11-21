package nichy.studentapp.controller;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.CourseProgressResponse;
import nichy.studentapp.dto.LessonProgressResponse;
import nichy.studentapp.service.LessonProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lesson-progress")
@RequiredArgsConstructor
public class LessonProgressController {

    private final LessonProgressService lessonProgressService;

    // ឧទាហរណ៍៖ PUT /api/lesson-progress/complete?studentId=1&lessonId=5&completed=true
    @PutMapping("/complete")
    public ResponseEntity<LessonProgressResponse> markLessonCompleted(
            @RequestParam Long studentId,
            @RequestParam Long lessonId,
            @RequestParam(defaultValue = "true") boolean completed
    ) {
        return ResponseEntity.ok(
                lessonProgressService.toggleLessonCompletion(studentId, lessonId, completed)
        );
    }

    @GetMapping("/course/{courseId}/student/{studentId}")
    public ResponseEntity<CourseProgressResponse> getCourseProgress(
            @PathVariable Long courseId,
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(
                lessonProgressService.getCourseProgressForStudent(studentId, courseId)
        );
    }
}