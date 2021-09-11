package edu.pure.server.opedukg.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.pure.server.opedukg.model.CachedExerciseEntityName;
import edu.pure.server.opedukg.model.OpedukgExercise;
import edu.pure.server.opedukg.payload.OpedukgResponse;
import edu.pure.server.opedukg.repository.CachedExerciseEntityNameRepository;
import edu.pure.server.opedukg.repository.OpedukgExerciseRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ExerciseService {
    private static final String URL = "/api/typeOpen/open/questionListByUriName";

    private final OpedukgClientLoggedIn client;
    private final OpedukgExerciseRepository opedukgExerciseRepository;
    private final CachedExerciseEntityNameRepository cachedExerciseEntityNameRepository;

    @Transactional
    public List<OpedukgExercise> getExercise(final String entityName) {
        return this.cachedExerciseEntityNameRepository
                .findById(entityName)
                .map(CachedExerciseEntityName::getExercises)
                .orElseGet(() -> {
                    final var entityNameCached = new CachedExerciseEntityName(entityName);
                    this.cachedExerciseEntityNameRepository.save(entityNameCached);
                    final List<OpedukgExercise> exercises =
                            this.getExerciseFromOpedukg(entityNameCached).stream()
                                .map(e -> this.opedukgExerciseRepository
                                        .findById(e.getId())
                                        .map(e_ -> {
                                            e_.getEntityNames().add(entityNameCached);
                                            return e_;
                                        })
                                        .orElse(e)
                                )
                                .collect(Collectors.toList());
                    return this.opedukgExerciseRepository.saveAll(exercises);
                });
    }

    private List<OpedukgExercise>
    getExerciseFromOpedukg(final @NotNull CachedExerciseEntityName entityName) {
        final OpedukgResponse<List<Data>> response =
                this.client.get(ExerciseService.URL, Response.class,
                                Map.of("uriName", entityName.getValue()));
        return response.getData().stream() // 过滤非单选题
                       .filter(d -> d.getQAnswer().length() == 1)
                       .filter(d -> d.getQAnswer().matches("[A-Z]"))
                       .map(d -> new OpedukgExercise(d.getId(), entityName,
                                                     d.getQBody(), d.getQAnswer()))
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
