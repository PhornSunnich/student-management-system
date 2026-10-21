package nichy.studentapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relationship Many Enrollments -> 1 Student(User)
    @ManyToOne
    @JoinColumn(name= "student_id", nullable = false)
    private Student student;

    // relationship Many Enrollments -> 1 Course
    @ManyToOne
    @JoinColumn(name="course_id", nullable= false)
    private Course course;

    private LocalDateTime enrolledAt;
    private Double progress; // example: 50.0%
}
