package edu.pure.server.opedukg.entity;

import lombok.Getter;

import java.util.List;

@Getter
public class KnowledgeBaseEntityDetail extends KnowledgeBaseEntity {
    private final String category;
    private final List<EntityPropertyDetail> properties;
    private final List<EntityRelation> objects;
    private final List<EntityRelation> subjects;

    public KnowledgeBaseEntityDetail(final String name, final String uri, final String category,
                                     final List<EntityPropertyDetail> properties,
                                     final List<EntityRelation> objects,
                                     final List<EntityRelation> subjects) {
        super(name, uri);
        this.category = category;
        this.properties = properties;
        this.objects = objects;
        this.subjects = subjects;
    }

    public KnowledgeBaseEntity toSuper() {
        return new KnowledgeBaseEntity(this.getName(), this.getUri());
    }
}
