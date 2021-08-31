package edu.pure.server.repository;

import edu.pure.server.model.ErrorBookItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorBookRepository extends JpaRepository<ErrorBookItem, Long> {
    boolean existsByUserIdAndExerciseId(long userId, int exerciseId);

    void deleteByUserId(long userId);

    void deleteByUserIdAndExerciseId(long userId, int exerciseId);

    Page<ErrorBookItem> findAllByUserIdOrderByCreatedAtDesc(long userId, Pageable pageable);
}
