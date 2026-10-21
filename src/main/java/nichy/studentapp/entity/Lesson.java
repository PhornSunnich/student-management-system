package nichy.studentapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lessons")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    private String videoUrl;
    private String content;

    // relationship Many Lessons -> 1 Course
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
