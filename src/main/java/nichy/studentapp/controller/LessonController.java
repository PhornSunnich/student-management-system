package nichy.studentapp.controller;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.LessonRequest;
import nichy.studentapp.dto.LessonResponse;
import nichy.studentapp.entity.Enrollment;
import nichy.studentapp.entity.Lesson;
import nichy.studentapp.service.LessonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<Lesson> createLesson(@RequestBody LessonRequest request){
        Lesson createdLesson = lessonService.createLesson(request);
        return new ResponseEntity<>(createdLesson, HttpStatus.CREATED);
    }

    // 1. Get All Lessons
    @GetMapping
    public ResponseEntity<List<LessonResponse>> getAllLessons() {
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    // 2. Get Lesson by ID
    @GetMapping("/{id}")
    public ResponseEntity<LessonResponse> getLessonById(@PathVariable Long id) {
        LessonResponse lesson = lessonService.getLessonById(id);
        return ResponseEntity.ok(lesson);
    }

    // 3. Update ID
    @PutMapping("/{id}")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable Long id,
            @RequestBody LessonRequest request) {
        LessonResponse updatedLesson = lessonService.updateLesson(id, request);
        return ResponseEntity.ok(updatedLesson);
    }

    // 4. Delete ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok("Lesson deleted successfully with id: " + id);
    }

    // 5. Get lesson by Course ID
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LessonResponse>> getLessonsByCourse(@PathVariable Long courseId){
        List<LessonResponse> lessons = lessonService.getLessonByCourseId(courseId);
        return ResponseEntity.ok(lessons);
    }

    // check student progress
    @PutMapping("/progress")
    public ResponseEntity<Enrollment> updateProgress(
            @RequestParam Long studentId,
            @RequestParam Long courseId,
            @RequestParam Double progress
    ){
        Enrollment updateEnrollment = lessonService.updateProgress(studentId, courseId, progress);
        return ResponseEntity.ok(updateEnrollment);
    }
}
