package edu.pure.server.controller;

import edu.pure.server.exception.UnimplementedException;
import edu.pure.server.model.CourseName;
import edu.pure.server.model.Exercise;
import edu.pure.server.model.User;
import edu.pure.server.opedukg.entity.*;
import edu.pure.server.opedukg.payload.LoginRequest;
import edu.pure.server.opedukg.service.*;
import edu.pure.server.repository.ErrorBookRepository;
import edu.pure.server.repository.ExerciseRepository;
import edu.pure.server.repository.UserRepository;
import edu.pure.server.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/opedukg")
@RolesAllowed("USER")
public class OpedukgController {
    private final LoginService loginService;
    private final SearchService searchService;
    private final EntityDetailService detailService;
    private final RecognizeService recognizeService;
    private final ExerciseService exerciseService;

    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final ErrorBookRepository errorBookRepository;

    @PostMapping("/login")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Map<String, String>>
    login(@RequestBody final @Valid @NotNull LoginRequest request) {
        final String id = this.loginService.login(request.getPhoneNumber(), request.getPassword());
        return ResponseEntity.ok(Map.of("id", id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchResult>> search(@RequestParam final @NotNull CourseName course,
                                                     @RequestParam final @NotNull String keyword) {
        final List<SearchResult> results = this.searchService.search(course.toOpedukg(), keyword);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/detail")
    public ResponseEntity<KnowledgeBaseEntityDetail>
    getDetail(@RequestParam final @NotNull CourseName course,
              @RequestParam final @NotNull String entityName) {
        final KnowledgeBaseEntityDetail detail = this.detailService.getDetail(course.toOpedukg(),
                                                                              entityName);
        return ResponseEntity.ok(detail);
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

    @GetMapping("/entity")
    public ResponseEntity<Object> getEntity(@RequestParam final CourseName course,
                                            @RequestParam final String uri) {
        throw new UnimplementedException(); // TODO 感觉应该整合进 detail？
    }
}
