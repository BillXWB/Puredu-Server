package edu.pure.server.repository;

import edu.pure.server.model.FavoriteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Long> {
    List<FavoriteItem> findAllByUserId(final long userId);

    void deleteAllByUserId(final long userId);
}
