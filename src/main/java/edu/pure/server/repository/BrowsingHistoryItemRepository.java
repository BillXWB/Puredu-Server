package edu.pure.server.repository;

import edu.pure.server.model.BrowsingHistoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrowsingHistoryItemRepository extends JpaRepository<BrowsingHistoryItem, Long> {
}
