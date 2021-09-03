package edu.pure.server.model;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "entity_records")
public class EntityRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String entityName;
    private CourseName courseName;
}
