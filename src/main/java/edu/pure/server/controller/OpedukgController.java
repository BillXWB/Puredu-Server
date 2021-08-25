package edu.pure.server.controller;

import edu.pure.server.exception.UnimplementedException;
import edu.pure.server.model.CourseName;
import edu.pure.server.opedukg.entity.*;
import edu.pure.server.opedukg.payload.LoginRequest;
import edu.pure.server.opedukg.service.*;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<OpedukgExercise>>
    getExercises(@RequestParam final String entityName) {
        final List<OpedukgExercise> exercises = this.exerciseService.getExercise(entityName);
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
