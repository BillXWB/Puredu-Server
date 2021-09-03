package edu.pure.server.opedukg.service;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import edu.pure.server.opedukg.entity.EntityPropertyDetail;
import edu.pure.server.opedukg.entity.EntityRelation;
import edu.pure.server.opedukg.entity.KnowledgeBaseEntity;
import edu.pure.server.opedukg.entity.KnowledgeBaseEntityDetail;
import edu.pure.server.opedukg.payload.OpedukgResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class EntityService {
    private static final String URL = "/api/typeOpen/open/infoByInstanceName";

    private final OpedukgClientLoggedIn client;

    public KnowledgeBaseEntityDetail getEntity(final String course, final String entityName) {
        final OpedukgResponse<Data> response = this.client.get(EntityService.URL,
                                                               Response.class,
                                                               Map.of("course", course,
                                                                      "name", entityName));
        final List<EntityPropertyDetail> properties
                = response.getData().getProperty().stream()
                          .map(p -> new EntityPropertyDetail(p.getPredicateLabel(),
                                                             p.getObject(),
                                                             p.getPredicate())
                          ).collect(Collectors.toList());
        final Map<Data.Content.RelationType, List<EntityRelation>> entities
                = response.getData().getContent().stream()
                          .collect(Collectors.groupingBy(Data.Content::getType))
                          .entrySet().stream() // Map<_, List<Data.Content>>
                          .collect(Collectors.toMap(Map.Entry::getKey,
                                                    entry -> entry.getValue().stream()
                                                                  .map(c -> new EntityRelation(
                                                                          new KnowledgeBaseEntity(
                                                                                  c.getPredicateLabel(),
                                                                                  c.getPredicate()),
                                                                          new KnowledgeBaseEntity(
                                                                                  c.getEntityLabel(),
                                                                                  c.getEntity()))
                                                                  ).collect(Collectors.toList())));
        return new KnowledgeBaseEntityDetail(
                response.getData().getLabel(),
                response.getData().getUri(),
                properties,
                entities.getOrDefault(Data.Content.RelationType.OBJECT, List.of()),
                entities.getOrDefault(Data.Content.RelationType.SUBJECT, List.of())
        );
    }

    private static class Response extends OpedukgResponse<Data> {}

    @Getter
    private static class Data {
        private String label;
        private String uri;
        private List<Property> property;
        private List<Content> content;

        @Getter
        private static class Property {
            private String predicate;
            private String predicateLabel;
            private String object;
        }

        @Getter
        @JsonIgnoreProperties(value = {"entity", "type"}, allowGetters = true)
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        private static class Content {
            private String predicate;
            private String predicateLabel;
            private String entity;

            @JsonAlias({"object_label", "subject_label"})
            private String entityLabel;

            private RelationType type;

            @JsonProperty
            private void setObject(final String object) {
                this.entity = object;
                this.type = RelationType.OBJECT;
            }

            @JsonProperty
            private void setSubject(final String subject) {
                this.entity = subject;
                this.type = RelationType.SUBJECT;
            }

            private enum RelationType {OBJECT, SUBJECT}
        }
    }
}
