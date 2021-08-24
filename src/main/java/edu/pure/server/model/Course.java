package edu.pure.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, length = 20)
    @NaturalId
    private CourseName name;

    public Course(final CourseName name) {this.name = name;}
}
