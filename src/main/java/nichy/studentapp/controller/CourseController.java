package nichy.studentapp.controller;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.CourseRequest;
import nichy.studentapp.dto.CourseResponse;
import nichy.studentapp.dto.EnrollmentRequest;
import nichy.studentapp.dto.EnrollmentResponse;
import nichy.studentapp.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    // create new course
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CourseRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request));
    }

    // see all course
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses(){
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // 3. Get Course by ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // 4. Update Course
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN', 'INSTRUCTOR', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long id, @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    // 5. Delete Course
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    // enrollment
    @PostMapping("/enroll")
    public ResponseEntity<EnrollmentResponse> enrollCourse(@RequestBody EnrollmentRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.enrollStudent(request));
    }

    @GetMapping("/enrollments/all")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {
        return ResponseEntity.ok(courseService.getAllEnrollments());
    }

    @GetMapping("/{studentId}/enrollments")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'ROLE_STUDENT', 'ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(@PathVariable Long studentId) {
        return ResponseEntity.ok(courseService.getEnrollmentsByStudentId(studentId));
    }
}