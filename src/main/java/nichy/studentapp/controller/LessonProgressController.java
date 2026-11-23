package nichy.studentapp.controller;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.CourseProgressResponse;
import nichy.studentapp.dto.LessonProgressResponse;
import nichy.studentapp.service.LessonProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/lesson-progress")
@RequiredArgsConstructor
public class LessonProgressController {

    private final LessonProgressService lessonProgressService;

    // ឧទាហរណ៍៖ PUT /api/lesson-progress/complete?studentId=1&lessonId=5&completed=true
    @PutMapping("/complete")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'ROLE_STUDENT', 'ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<LessonProgressResponse> markLessonCompleted(
            Principal principal,
            @RequestParam Long studentId,
            @RequestParam Long lessonId,
            @RequestParam(defaultValue = "true") boolean completed
    ) {
        return ResponseEntity.ok(
                lessonProgressService.toggleLessonCompletion(studentId, lessonId, completed)
        );
    }

    @GetMapping("/course/{courseId}/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'ROLE_STUDENT', 'ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<CourseProgressResponse> getCourseProgress(
            Principal principal,
            @PathVariable Long courseId,
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(
                lessonProgressService.getCourseProgressForStudent(studentId, courseId)
        );
    }
}