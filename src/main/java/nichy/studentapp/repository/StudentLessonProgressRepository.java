package nichy.studentapp.repository;

import nichy.studentapp.entity.StudentLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentLessonProgressRepository extends JpaRepository<StudentLessonProgress, Long> {

    Optional<StudentLessonProgress> findByStudentIdAndLessonId(Long studentId, Long lessonId);

    List<StudentLessonProgress> findByStudentIdAndLesson_Course_Id(Long studentId, Long courseId);

    @Query("""
            SELECT COUNT(p)
            FROM StudentLessonProgress p
            WHERE p.student.id = :studentId
              AND p.lesson.course.id = :courseId
              AND p.completed = true
            """)
    long countCompletedLessons(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}