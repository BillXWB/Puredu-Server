package edu.pure.server.repository;

import edu.pure.server.model.BrowsingHistoryItem;
import edu.pure.server.model.CourseName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrowsingHistoryItemRepository extends JpaRepository<BrowsingHistoryItem, Long> {
    Optional<BrowsingHistoryItem> findByUserIdAndNameAndCourse(final long userId,
                                                               final String name,
                                                               final CourseName course);

    List<BrowsingHistoryItem> findAllByUserId(final long userId);
}
