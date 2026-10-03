package nichy.studentapp.service;

import nichy.studentapp.dto.StudentRequest;
import nichy.studentapp.dto.StudentResponse;
import nichy.studentapp.repository.StudentRepository;
import nichy.studentapp.entity.Student;
import org.springframework.stereotype.Service;
import java.util.Collection;

import java.util.List;

@Service
public interface StudentService {
    StudentResponse createStudent(StudentRequest request);
    List<StudentResponse> getAllStudents();
    StudentResponse getStudentById(Long id);
    StudentResponse updateStudent(Long id, StudentRequest request);
    void deleteStudent(Long id);
}
