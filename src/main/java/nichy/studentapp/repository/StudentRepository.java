package nichy.studentapp.repository;

import nichy.studentapp.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // ទទួលបាន Methods ស្រាប់ៗដូចជា findAll(), findById(), save(), deleteById() ...
}
