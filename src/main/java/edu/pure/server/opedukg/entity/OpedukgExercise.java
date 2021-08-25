package edu.pure.server.opedukg.entity;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class OpedukgExercise {
    private final int id;
    private final String question;
    private final List<String> options;
    private final int answer;

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
}
