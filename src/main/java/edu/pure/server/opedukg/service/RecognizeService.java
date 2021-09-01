package edu.pure.server.opedukg.service;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import edu.pure.server.opedukg.entity.KnowledgeBaseEntity;
import edu.pure.server.opedukg.entity.RecognitionResult;
import edu.pure.server.opedukg.payload.OpedukgResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class RecognizeService {
    private static final String URL = "/api/typeOpen/open/linkInstance";

    private final OpedukgClientLoggedIn client;

    public List<RecognitionResult> recognize(final String course, final String text) {
        final OpedukgResponse<Data> response = this.client.post(RecognizeService.URL,
                                                                Response.class,
                                                                Map.of("course", course,
                                                                       "context", text));
        return response.getData().getResults().stream()
                       .map(r -> new RecognitionResult(
                               new KnowledgeBaseEntity(r.getEntity(), r.getEntityUrl()),
                               new RecognitionResult.Index(r.getStartIndex(), r.getEndIndex()))
                       ).collect(Collectors.toList());
    }

    private static class Response extends OpedukgResponse<Data> {}

    @Getter
    private static class Data {
        private List<Result> results;

        @Getter
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        private static class Result {
            private String entityType;
            private String entityUrl;
            private int startIndex;
            private int endIndex;
            private String entity;
        }
    }
}
