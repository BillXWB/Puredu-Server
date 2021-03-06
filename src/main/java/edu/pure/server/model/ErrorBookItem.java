package edu.pure.server.model;

import edu.pure.server.model.audit.DateAudit;
import edu.pure.server.opedukg.model.OpedukgExercise;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_error_book")
public class ErrorBookItem extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private OpedukgExercise exercise;

    public ErrorBookItem(final User user, final OpedukgExercise exercise) {
        this.user = user;
        this.exercise = exercise;
    }
}
