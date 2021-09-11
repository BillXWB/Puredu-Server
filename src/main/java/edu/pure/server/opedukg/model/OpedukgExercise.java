package edu.pure.server.opedukg.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "option", columnDefinition = "TEXT")
    private List<String> options;

    private int answer;

    @JsonUnwrapped
    @ManyToOne
    @JoinColumn(name = "entity_name")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CachedExerciseEntityName entityName;

    public OpedukgExercise(final int id,
                           final @NotNull CachedExerciseEntityName entityName,
                           final @NotNull String question,
                           final @NotNull String answer) {
        // TODO 非选择题
        this.id = id;
        this.entityName = entityName;
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
        this.entityName = other.getEntityName();
        this.question = other.getQuestion();
        this.options = other.getOptions();
        this.answer = other.getAnswer();
    }
}
