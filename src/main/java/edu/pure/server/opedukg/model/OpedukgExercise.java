package edu.pure.server.opedukg.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("LombokEqualsAndHashCodeInspection")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "opedukg_exercises")
public class OpedukgExercise {
    @Id
    private int id;

    @Column(columnDefinition = "TEXT")
    private String question;

    @SuppressWarnings("JpaDataSourceORMInspection")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "option", columnDefinition = "TEXT")
    private List<String> options;

    private int answer;

    @JsonUnwrapped
    @ManyToMany
    @JoinTable(
            name = "opedukg_exercise_entity_names",
            joinColumns = @JoinColumn(name = "exercise_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_name")
    )
    private Set<CachedExerciseEntityName> entityNames;

    public OpedukgExercise(final int id,
                           final @NotNull CachedExerciseEntityName entityName,
                           final @NotNull String question,
                           final @NotNull String answer) {
        this.id = id;
        this.entityNames = Set.of(entityName);
        final List<String> segments = Arrays.stream(question.split("[A-Z][．.]")) // 可能是全角句点...
                                            .map(String::strip)
                                            .filter(Predicate.not(String::isEmpty))
                                            .collect(Collectors.toList());
        this.question = segments.get(0);
        this.options = segments.subList(1, segments.size());
        this.answer = answer.toCharArray()[0] - 'A';
    }

    protected OpedukgExercise(final @NotNull OpedukgExercise other) {
        this.id = other.getId();
        this.entityNames = new HashSet<>(other.getEntityNames());
        this.question = other.getQuestion();
        this.options = other.getOptions();
        this.answer = other.getAnswer();
    }
}
