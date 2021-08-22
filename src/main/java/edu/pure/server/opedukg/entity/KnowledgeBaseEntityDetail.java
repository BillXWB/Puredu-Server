package edu.pure.server.opedukg.entity;

import lombok.Getter;

import java.util.List;

@Getter
public class KnowledgeBaseEntityDetail extends KnowledgeBaseEntity {
    private final List<EntityPropertyDetail> properties;
    private final List<EntityRelation> objects;
    private final List<EntityRelation> subjects;

    public KnowledgeBaseEntityDetail(final String name, final String uri,
                                     final List<EntityPropertyDetail> properties,
                                     final List<EntityRelation> objects,
                                     final List<EntityRelation> subjects) {
        super(name, uri);
        this.properties = properties;
        this.objects = objects;
        this.subjects = subjects;
    }
}
