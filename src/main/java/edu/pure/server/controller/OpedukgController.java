package edu.pure.server.controller;

import edu.pure.server.exception.UnimplementedException;
import edu.pure.server.model.CourseName;
import edu.pure.server.model.Exercise;
import edu.pure.server.model.User;
import edu.pure.server.opedukg.entity.*;
import edu.pure.server.opedukg.service.*;
import edu.pure.server.repository.ErrorBookRepository;
import edu.pure.server.repository.ExerciseRepository;
import edu.pure.server.repository.UserRepository;
import edu.pure.server.security.UserPrincipal;
import edu.pure.server.util.PageFactory;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/opedukg")
@RolesAllowed("USER")
public class OpedukgController {
    private final SearchService searchService;
    private final EntityService entityService;
    private final RecognizeService recognizeService;
    private final ExerciseService exerciseService;
    private final KnowledgeCardService knowledgeCardService;

    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final ErrorBookRepository errorBookRepository;

    @GetMapping("/search")
    public ResponseEntity<Page<SearchResult>> search(@RequestParam final @NotNull CourseName course,
                                                     @RequestParam final @NotNull String keyword,
                                                     final Pageable pageable) {
        final List<SearchResult> results = this.searchService.search(course.toOpedukg(), keyword);
        final Page<SearchResult> page = PageFactory.fromList(results, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping(value = "/entity", params = {"uri", "!name"})
    public ResponseEntity<KnowledgeBaseEntityDetail>
    getEntityByUri(@RequestParam final @NotNull CourseName course, @RequestParam final String uri) {
        final KnowledgeCard knowledgeCard =
                this.knowledgeCardService.getKnowledgeCard(course.toOpedukg(), uri);
        return this.getEntityByName(course, knowledgeCard.getEntity().getName());
    }

    @GetMapping(value = "/entity", params = {"name", "!uri"})
    public ResponseEntity<KnowledgeBaseEntityDetail>
    getEntityByName(@RequestParam final @NotNull CourseName course,
                    @RequestParam final @NotNull String name) {
        final KnowledgeBaseEntityDetail entity = this.entityService.getEntity(course.toOpedukg(),
                                                                              name);
        return ResponseEntity.ok(entity);
    }

    @GetMapping("/question")
    public ResponseEntity<OpedukgAnswer> question(@RequestParam final CourseName course,
                                                  @RequestParam final String question) {
        throw new UnimplementedException(); // TODO
    }

    @GetMapping("/recognize")
    public ResponseEntity<List<RecognitionResult>>
    recognize(@RequestParam final @NotNull CourseName course,
              @RequestParam final @NotNull String text) {
        final List<RecognitionResult> results = this.recognizeService.recognize(course.toOpedukg(),
                                                                                text);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/exercises")
    public ResponseEntity<List<? extends OpedukgExercise>>
    getExercises(@RequestParam final String entityName,
                 @AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        List<? extends OpedukgExercise> exercises = this.exerciseService.getExercise(entityName);
        this.exerciseRepository.saveAll(exercises.stream()
                                                 .map(Exercise::fromOpedukg)
                                                 .collect(Collectors.toList()));
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        final User user = this.userRepository.findById(currentUser.getId()).get();
        exercises = exercises.stream()
                             .map(exercise -> new OpedukgExercise(exercise) {
                                 public final boolean marked =
                                         OpedukgController.this.errorBookRepository
                                                 .existsByUserIdAndExerciseId(user.getId(),
                                                                              exercise.getId());
                             })
                             .collect(Collectors.toList());
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/relate")
    public ResponseEntity<List<RelatedEntity>>
    getRelatedEntities(@RequestParam final CourseName course, @RequestParam final String keyword) {
        throw new UnimplementedException(); // TODO
    }
}
