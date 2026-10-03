package nichy.studentapp.service;

import nichy.studentapp.repository.StudentRepository;
import nichy.studentapp.entity.Student;
import org.springframework.stereotype.Service;
import java.util.Collection;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    public StudentService(StudentRepository studentRepository){
        this.studentRepository = studentRepository;
    }

    // get all student
    public List<Student> getAllStudent(){
        return studentRepository.findAll();
    }

    // get name by id
    public Student getNameById(Long id){
        return studentRepository.findById(id).orElse(null);
    }

    // create or save student
    public Student saveStudent(Student student){
        return studentRepository.save(student);
    }

    // delete student
    public void deleteStudent(Long id){
        studentRepository.deleteById(id);
    }
}
