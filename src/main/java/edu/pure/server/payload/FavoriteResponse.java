package edu.pure.server.payload;

import edu.pure.server.model.CourseName;
import edu.pure.server.opedukg.entity.KnowledgeBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteResponse {
    private final KnowledgeBaseEntity entity;
    private final CourseName course;
    private final String category;
}
