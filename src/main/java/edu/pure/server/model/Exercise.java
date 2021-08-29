package edu.pure.server.model;

import edu.pure.server.opedukg.entity.OpedukgExercise;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "exercises")
public class Exercise extends OpedukgExercise {
    private Exercise(final OpedukgExercise exercise) {
        super(exercise);
    }

    @Contract("_ -> new")
    public static @NotNull Exercise fromOpedukg(final OpedukgExercise exercise) {
        return new Exercise(exercise);
    }
}
