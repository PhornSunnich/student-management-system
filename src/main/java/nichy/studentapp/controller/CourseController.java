package nichy.studentapp.controller;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.*;
import nichy.studentapp.service.CourseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "title");

    // create new course
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CourseRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request));
    }

    // 1. Get Courses with Search, Category Filter & Pagination
    // ឧទាហរណ៍៖ GET /api/courses?page=0&size=10&search=Java&categoryId=1&sortBy=id&direction=desc
    @GetMapping
    public ResponseEntity<PagedResponse<CourseResponse>> getCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "id";

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(safeSortBy).descending()
                : Sort.by(safeSortBy).ascending();

        Pageable pageable = PageRequest.of(safePage, safeSize, sort);
        return ResponseEntity.ok(courseService.getCoursesPaged(search, categoryId, pageable));
    }

    // 2. Get Course by ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // 3. Filter Courses by Category ID
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(courseService.getCoursesByCategoryId(categoryId));
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

    // 6. Enrollment
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