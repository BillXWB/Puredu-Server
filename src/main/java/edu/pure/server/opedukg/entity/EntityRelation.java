package edu.pure.server.opedukg.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityRelation {
    private final KnowledgeBaseEntity predicate;
    private final KnowledgeBaseEntity entity;
}
