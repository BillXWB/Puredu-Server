package edu.pure.server.opedukg.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchResult {
    private final KnowledgeBaseEntity entity;
    private final String category;
}
