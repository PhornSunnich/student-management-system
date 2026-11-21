package nichy.studentapp.repository;

import nichy.studentapp.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    Optional<Review> findByStudentIdAndCourseId(Long studentId, Long courseId);
}