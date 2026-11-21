package nichy.studentapp.service;

import nichy.studentapp.dto.StudentRequest;
import nichy.studentapp.dto.StudentResponse;
import nichy.studentapp.dto.UpdateProfileRequest;

import java.util.List;

public interface StudentService {
    StudentResponse createStudent(StudentRequest request);
    List<StudentResponse> getAllStudents();
    StudentResponse getStudentById(Long id);
    StudentResponse getStudentProfileByEmail(String email);
    StudentResponse saveOrUpdateProfile(String email, StudentRequest request);
    StudentResponse updateMyProfile(String email, UpdateProfileRequest request);
    StudentResponse updateStudent(Long id, StudentRequest request);
    void deleteStudent(Long id);
}