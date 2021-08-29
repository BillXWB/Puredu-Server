package edu.pure.server.opedukg.service;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import edu.pure.server.opedukg.entity.EntityProperty;
import edu.pure.server.opedukg.entity.KnowledgeBaseEntity;
import edu.pure.server.opedukg.entity.KnowledgeCard;
import edu.pure.server.opedukg.payload.OpedukgResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class KnowledgeCardService {
    private static final String URL = "/api/typeOpen/open/getKnowledgeCard";

    private final OpedukgClient client;

    public KnowledgeCard getKnowledgeCard(final String course, final String uri) {
        final OpedukgResponse<Data> response =
                this.client.post(KnowledgeCardService.URL, Response.class,
                                 Map.of("course", course,
                                        "uri", uri));
        return new KnowledgeCard(new KnowledgeBaseEntity(response.getData().getEntityName(), uri),
                                 response.getData().getEntityType(),
                                 response.getData().getEntityFeatures().stream()
                                         .map(f -> new EntityProperty(f.getFeatureKey(),
                                                                      f.getFeatureValue()))
                                         .collect(Collectors.toList()));
    }

    private static class Response extends OpedukgResponse<Data> {}

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    private static class Data {
        private String entityType;
        private String entityName;
        private List<Feature> entityFeatures;

        @Getter
        private static class Feature {
            private String featureKey;
            private String featureValue;
        }
    }
}
