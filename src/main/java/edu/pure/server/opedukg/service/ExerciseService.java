package edu.pure.server.opedukg.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.pure.server.opedukg.entity.OpedukgExercise;
import edu.pure.server.opedukg.payload.OpedukgResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ExerciseService {
    private static final String URL = "/api/typeOpen/open/questionListByUriName";

    private final OpedukgClientLoggedIn client;

    public List<OpedukgExercise> getExercise(final String entityName) {
        final OpedukgResponse<List<Data>> response = this.client.get(ExerciseService.URL,
                                                                     Response.class,
                                                                     Map.of("uriName", entityName));
        return response.getData().stream()
                       .map(d -> new OpedukgExercise(d.getId(), d.getQBody(), d.getQAnswer()))
                       .collect(Collectors.toList());
    }

    private static class Response extends OpedukgResponse<List<Data>> {}

    @Getter
    private static class Data {
        @JsonProperty("qAnswer")
        private String qAnswer;

        private int id;

        @JsonProperty("qBody")
        private String qBody;
    }
}
