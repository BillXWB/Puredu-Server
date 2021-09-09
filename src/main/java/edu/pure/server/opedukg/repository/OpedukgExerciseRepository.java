package edu.pure.server.opedukg.repository;

import edu.pure.server.opedukg.model.OpedukgExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpedukgExerciseRepository extends JpaRepository<OpedukgExercise, Integer> {
}
