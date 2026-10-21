package nichy.studentapp.service;

import nichy.studentapp.dto.StudentRequest;
import nichy.studentapp.dto.StudentResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StudentService {
    StudentResponse createStudent(StudentRequest request);
    List<StudentResponse> getAllStudents();
    StudentResponse getStudentById(Long id);
    StudentResponse getStudentProfileByEmail(String email);
    StudentResponse saveOrUpdateProfile(String email, StudentRequest request);
    StudentResponse updateStudent(Long id, StudentRequest request);
    void deleteStudent(Long id);
}
