package edu.pure.server.opedukg.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecognitionResult {
    private final KnowledgeBaseEntity entity;
    private final Index index;

    @Getter
    @AllArgsConstructor
    public static class Index {
        private final int begin;
        private final int end;
    }
}
