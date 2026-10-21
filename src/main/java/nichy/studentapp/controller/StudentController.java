package nichy.studentapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.StudentRequest;
import nichy.studentapp.dto.StudentResponse;
import nichy.studentapp.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*") // 👈 អនុញ្ញាតឱ្យ Frontend ហៅ API បាន
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request){
        return new ResponseEntity<>(studentService.createStudent(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents(){
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id){
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request){
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    // 1. GET /api/students/me ៖ សម្រាប់មើល Profile ខ្លួនឯង
    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'ROLE_STUDENT')")
    public ResponseEntity<StudentResponse> getCurrentStudentProfile(Principal principal) {
        // principal.getName() គឺទាញយក Email ពី JWT Token ដែលកំពុង Login ស្រាប់
        String email = principal.getName();
        StudentResponse response = studentService.getStudentProfileByEmail(email);
        return ResponseEntity.ok(response);
    }

    // 2. POST /api/students/me ៖ សម្រាប់បង្កើត ឬកែសម្រួល Profile ខ្លួនឯង
    @PostMapping("/me")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'ROLE_STUDENT')")
    public ResponseEntity<StudentResponse> createOrUpdateMyProfile(
            Principal principal,
            @Valid @RequestBody StudentRequest request) {

        String email = principal.getName();
        StudentResponse response = studentService.saveOrUpdateProfile(email, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id){
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }




}
