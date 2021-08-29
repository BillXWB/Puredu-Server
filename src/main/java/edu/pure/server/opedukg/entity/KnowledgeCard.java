package edu.pure.server.opedukg.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class KnowledgeCard {
    private final KnowledgeBaseEntity entity;
    private final String type;
    private final List<EntityProperty> properties;
}
