package nichy.studentapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // ឧ. Programming, Database, Design, Business

    private String description;

    @JsonIgnore // ការពារ Infinite Recursion ពេលแปลงជា JSON
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Course> courses;
}
