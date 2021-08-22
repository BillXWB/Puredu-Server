package edu.pure.server.opedukg.entity;

import lombok.Getter;

@Getter
public class EntityPropertyDetail extends EntityProperty {
    private final String uri;

    public EntityPropertyDetail(final String key, final String value, final String uri) {
        super(key, value);
        this.uri = uri;
    }
}
