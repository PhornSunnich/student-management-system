package nichy.studentapp.controller;

import nichy.studentapp.entity.Student;
import nichy.studentapp.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    private final StudentService studentService;
    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }
    // get all student
    @GetMapping
    public List<Student> getAllStudent(){
        return studentService.getAllStudent();
    }
    // get name by id
    @GetMapping("/{id}")
    public Student getNameById(@PathVariable Long id){
        return studentService.getNameById(id);
    }
    // create or save student
    @PostMapping
    public Student createStudent(@RequestBody Student student){
        return studentService.saveStudent(student);
    }
    // delete student
    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id){
        studentService.deleteStudent(id);
    }

}
