package nichy.studentapp.controller;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.*;
import nichy.studentapp.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN', 'INSTRUCTOR', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<CourseResponse>> getCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                courseService.getCoursesPaged(search, categoryId, page, size, "id", "asc")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(courseService.getCoursesByCategoryId(categoryId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN', 'INSTRUCTOR', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long id, @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'ROLE_STUDENT', 'ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<EnrollmentResponse> enrollCourse(
            Principal principal,
            @RequestBody EnrollmentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.enrollStudent(request));
    }

    @GetMapping("/enrollments/all")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {
        return ResponseEntity.ok(courseService.getAllEnrollments());
    }

    @GetMapping("/{studentId}/enrollments")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'ROLE_STUDENT', 'ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
            Principal principal,
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(courseService.getEnrollmentsByStudentId(studentId));
    }
}