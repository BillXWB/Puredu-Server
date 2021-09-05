package edu.pure.server.opedukg.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
public class KnowledgeBaseEntity {
    private final String name;
    private final String uri;
}
