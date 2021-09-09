package edu.pure.server.opedukg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "cached_exercise_entity_names")
public class CachedExerciseEntityName {
    @JsonIgnore
    @OneToMany(mappedBy = "entityName")
    private final List<OpedukgExercise> exercises = List.of();

    @JsonProperty("entityName")
    @Id
    private String value;

    public CachedExerciseEntityName(final String entityName) {
        this.value = entityName;
    }
}
