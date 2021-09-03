package edu.pure.server.repository;

import edu.pure.server.model.EntityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRecordRepository extends JpaRepository<EntityRecord, Long> {
}
