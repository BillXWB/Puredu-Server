package edu.pure.server.opedukg.service;

import com.fasterxml.jackson.annotation.JsonAlias;
import edu.pure.server.opedukg.entity.EntityProperty;
import edu.pure.server.opedukg.entity.KnowledgeBaseEntity;
import edu.pure.server.opedukg.entity.OpedukgAnswer;
import edu.pure.server.opedukg.payload.OpedukgResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class QuestionService {
    private static final String URL = "/api/typeOpen/open/inputQuestion";

    private final OpedukgClientLoggedIn client;

    public List<OpedukgAnswer> askQuestion(final String course, final String question) {
        final OpedukgResponse<List<Data>> response =
                this.client.post(QuestionService.URL, Response.class,
                                 Map.of("course", course,
                                        "inputQuestion", question));
        return response.getData().stream()
                       .map(a -> new OpedukgAnswer(
                               new KnowledgeBaseEntity(a.getSubject(), a.getSubjectUri()),
                               new EntityProperty(a.getPredicate(), a.getValue()),
                               a.getScore())
                       )
                       .sorted()
                       .collect(Collectors.toList());
    }

    private static class Response extends OpedukgResponse<List<Data>> {}

    @Getter
    private static class Data {
        private String all;

        @SuppressWarnings("SpellCheckingInspection")
        @JsonAlias("fsanswer")
        private String fsAnswer;

        private String subject;
        private String message;

        @SuppressWarnings("SpellCheckingInspection")
        @JsonAlias("tamplateContent")
        private String templateContent;

        private int fs;
        private String filterStr;
        private String subjectUri;
        private String predicate;
        private double score;

        @SuppressWarnings("SpellCheckingInspection")
        @JsonAlias("answerflag")
        private boolean answerFlag;

        private String attention;

        @SuppressWarnings("SpellCheckingInspection")
        @JsonAlias("fsscore")
        private String fsScore;

        private String value;
    }
}
