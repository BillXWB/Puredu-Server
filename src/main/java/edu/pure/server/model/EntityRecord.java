package edu.pure.server.model;

import edu.pure.server.model.audit.DateAudit;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@SuppressWarnings("LombokEqualsAndHashCodeInspection")
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@MappedSuperclass
public class EntityRecord extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private CourseName course;
}
