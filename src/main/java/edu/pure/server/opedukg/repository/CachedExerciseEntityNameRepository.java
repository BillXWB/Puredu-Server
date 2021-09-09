package edu.pure.server.opedukg.repository;

import edu.pure.server.opedukg.model.CachedExerciseEntityName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CachedExerciseEntityNameRepository
        extends JpaRepository<CachedExerciseEntityName, String> {
}
