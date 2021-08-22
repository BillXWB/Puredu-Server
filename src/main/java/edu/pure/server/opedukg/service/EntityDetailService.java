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
public class EntityDetailService {
    private static final String URL = "/api/typeOpen/open/infoByInstanceName";

    private final OpedukgClient client;

    public KnowledgeBaseEntityDetail getDetail(final String course, final String entityName) {
        final OpedukgResponse<Data> response = this.client.get(EntityDetailService.URL,
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
        return new KnowledgeBaseEntityDetail(response.getData().getLabel(), "", properties,
                                             entities.get(Data.Content.RelationType.OBJECT),
                                             entities.get(Data.Content.RelationType.SUBJECT));
    }

    private static class Response extends OpedukgResponse<Data> {}

    @Getter
    private static class Data {
        private String label;
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