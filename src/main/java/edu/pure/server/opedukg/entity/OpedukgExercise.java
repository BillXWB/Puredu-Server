package edu.pure.server.opedukg.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OpedukgExercise {
    private final int id;
    private final String question;
    private final String answer;
}
