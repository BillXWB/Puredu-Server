package edu.pure.server.controller;

import edu.pure.server.exception.UnimplementedException;
import edu.pure.server.model.CourseName;
import edu.pure.server.opedukg.entity.*;
import edu.pure.server.opedukg.payload.LoginRequest;
import edu.pure.server.opedukg.service.*;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@AllArgsConstructor
@RestController
@RequestMapping("/api/opedukg")
public class OpedukgController {
    private LoginService loginService;
    private SearchService searchService;
    private EntityDetailService detailService;
    private RecognizeService recognizeService;
    private ExerciseService exerciseService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>>
    login(@Valid @RequestBody @NotNull final LoginRequest request) {
        final String id = this.loginService.login(request.getPhoneNumber(), request.getPassword());
        return ResponseEntity.ok(Map.of("id", id));
    }

    @GetMapping("/search")
    public CollectionModel<SearchResult> search(@RequestParam @NotNull final CourseName course,
                                                @RequestParam @NotNull final String keyword) {
        return CollectionModel.of(
                this.searchService.search(course.toOpedukg(), keyword),
                linkTo(methodOn(OpedukgController.class).search(course, keyword)).withSelfRel()
        );
    }

    @GetMapping("/detail")
    public EntityModel<KnowledgeBaseEntityDetail>
    getDetail(@RequestParam @NotNull final CourseName course,
              @RequestParam @NotNull final String entityName) {
        return EntityModel.of(
                this.detailService.getDetail(course.toOpedukg(), entityName),
                linkTo(methodOn(OpedukgController.class)
                               .getDetail(course, entityName)).withSelfRel()
        );
    }

    @GetMapping("/question")
    public EntityModel<OpedukgAnswer> question(@RequestParam final CourseName course,
                                               @RequestParam final String question) {
        throw new UnimplementedException(); // TODO
    }

    @GetMapping("/recognize")
    public CollectionModel<RecognitionResult>
    recognize(@RequestParam @NotNull final CourseName course,
              @RequestParam @NotNull final String text) {
        return CollectionModel.of(
                this.recognizeService.recognize(course.toOpedukg(), text),
                linkTo(methodOn(OpedukgController.class).recognize(course, text)).withSelfRel()
        );
    }

    @GetMapping("/exercises")
    public CollectionModel<OpedukgExercise> getExercises(@RequestParam final String entityName) {
        return CollectionModel.of(
                this.exerciseService.getExercise(entityName),
                linkTo(methodOn(OpedukgController.class).getExercises(entityName)).withSelfRel()
        );
    }

    @GetMapping("/relate")
    public CollectionModel<RelatedEntity> getRelatedEntities(@RequestParam final CourseName course,
                                                             @RequestParam final String keyword) {
        throw new UnimplementedException(); // TODO
    }

    @GetMapping("/entity")
    public Object getEntity(@RequestParam final CourseName course,
                            @RequestParam final String uri) {
        throw new UnimplementedException(); // TODO 感觉应该整合进 detail？
    }
}