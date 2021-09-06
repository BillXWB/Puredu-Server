package edu.pure.server.opedukg.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("LombokEqualsAndHashCodeInspection")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@MappedSuperclass
public class OpedukgExercise {
    @JsonIgnore
    @Id
    private int id;

    private String question;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> options;

    private int answer;

    public OpedukgExercise(final int id,
                           final @NotNull String question,
                           final @NotNull String answer) {
        final Logger logger = LoggerFactory.getLogger(OpedukgExercise.class);
        // TODO 非选择题
        this.id = id;
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
        this.question = other.getQuestion();
        this.options = other.getOptions();
        this.answer = other.getAnswer();
    }
}
