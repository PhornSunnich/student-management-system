package nichy.studentapp.repository;

import nichy.studentapp.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    // find all subject that created by 1 instructor
    List<Course> findByInstructorId(Long instructorId);
}
