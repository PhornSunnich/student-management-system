package nichy.studentapp.repository;

import nichy.studentapp.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // find enrollment of 1 student
    List<Enrollment> findByStudentId(Long student_id);
    // check student enrolled on course
    Optional<Enrollment> findByStudentIdAndCourseId(Long student_id, Long course_id);
}
