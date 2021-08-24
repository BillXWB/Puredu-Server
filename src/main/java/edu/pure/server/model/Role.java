package edu.pure.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, length = 20)
    @NaturalId
    private RoleName name;

    public Role(final RoleName name) {
        this.name = name;
    }
}
