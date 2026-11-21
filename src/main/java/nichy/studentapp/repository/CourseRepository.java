package nichy.studentapp.repository;

import nichy.studentapp.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByCategoryId(Long categoryId);

    @Query("""
            SELECT c FROM Course c
            WHERE (:categoryId IS NULL OR c.category.id = :categoryId)
              AND (
                    :search IS NULL OR TRIM(:search) = ''
                    OR LOWER(c.title) LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                    OR LOWER(c.description) LIKE LOWER(CONCAT('%', TRIM(:search), '%'))
                  )
            """)
    Page<Course> searchAndFilterCourses(
            @Param("search") String search,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}