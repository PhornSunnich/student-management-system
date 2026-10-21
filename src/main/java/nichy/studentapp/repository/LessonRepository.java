package nichy.studentapp.repository;

import nichy.studentapp.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    // find all lessons in 1 course
    List<Lesson> findByCourseId(Long course_id);
}
