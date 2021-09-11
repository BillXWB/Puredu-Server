package edu.pure.server.service;

import edu.pure.server.opedukg.model.OpedukgExercise;
import edu.pure.server.repository.ErrorBookRepository;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class ErrorBookService {
    private final ErrorBookRepository errorBookRepository;

    @Transactional(readOnly = true)
    public OpedukgExercise markExerciseInErrorBook(final @NotNull OpedukgExercise exercise,
                                                   final long userId) {
        return new OpedukgExercise(exercise) {
            public final boolean marked =
                    ErrorBookService.this.errorBookRepository
                            .existsByUserIdAndExerciseId(userId, exercise.getId());
        };
    }
}
