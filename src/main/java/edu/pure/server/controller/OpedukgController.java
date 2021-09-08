package edu.pure.server.controller;

import edu.pure.server.exception.UnimplementedException;
import edu.pure.server.model.BrowsingHistoryItem;
import edu.pure.server.model.CourseName;
import edu.pure.server.model.Exercise;
import edu.pure.server.model.User;
import edu.pure.server.opedukg.entity.*;
import edu.pure.server.opedukg.service.*;
import edu.pure.server.repository.BrowsingHistoryItemRepository;
import edu.pure.server.repository.ErrorBookRepository;
import edu.pure.server.repository.ExerciseRepository;
import edu.pure.server.repository.UserRepository;
import edu.pure.server.security.UserPrincipal;
import edu.pure.server.util.PageFactory;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.util.*;
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
    private final QuestionService questionService;
    private final KnowledgeCardService knowledgeCardService;

    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final ErrorBookRepository errorBookRepository;
    private final BrowsingHistoryItemRepository browsingHistoryItemRepository;

    @Value("#{${opedukg-controller.keywords}}")
    private Map<CourseName, Set<String>> keywords;

    @GetMapping("/search")
    public ResponseEntity<Page<SearchResult>> search(@RequestParam final @NotNull CourseName course,
                                                     @RequestParam final @NotNull String keyword,
                                                     final @NotNull Pageable pageable) {
        final List<SearchResult> results = this.searchService.search(course.toOpedukg(), keyword);
        // TODO 怎么才能不这么脏？QwQ
        pageable.getSort().stream().findFirst().ifPresent(order -> {
            if (order.getProperty().equals("default") && order.isDescending()) {
                Collections.reverse(results);
            }
        });
        final Page<SearchResult> page = PageFactory.fromList(results, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping(value = "/entity", params = {"uri", "!name"})
    public ResponseEntity<KnowledgeBaseEntityDetail>
    getEntityByUri(@RequestParam final @NotNull CourseName course,
                   @RequestParam final String uri,
                   @AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        final KnowledgeCard knowledgeCard =
                this.knowledgeCardService.getKnowledgeCard(course.toOpedukg(), uri);
        return this.getEntityByName(course, knowledgeCard.getEntity().getName(), currentUser);
    }

    @GetMapping(value = "/entity", params = {"name", "!uri"})
    public ResponseEntity<KnowledgeBaseEntityDetail>
    getEntityByName(@RequestParam final @NotNull CourseName course,
                    @RequestParam final @NotNull String name,
                    @AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        final KnowledgeBaseEntityDetail entity = this.entityService.getEntity(course.toOpedukg(),
                                                                              name);
        final BrowsingHistoryItem browsingHistoryItem = this.browsingHistoryItemRepository
                .findByUserIdAndNameAndCourse(currentUser.getId(), name, course)
                .orElse(new BrowsingHistoryItem(name, course,
                                                this.userRepository.getById(currentUser.getId())));
        browsingHistoryItem.incCount(1);
        this.browsingHistoryItemRepository.save(browsingHistoryItem);
        return ResponseEntity.ok(entity);
    }

    @GetMapping("/entities")
    @PermitAll
    public ResponseEntity<List<SearchResult>>
    getEntities(@RequestParam final @NotNull CourseName course,
                @RequestParam final int size) {
        final List<String> list = new ArrayList<>(this.keywords.get(course));
        Collections.shuffle(list);
        List<SearchResult> results = list.stream()
                                         .limit(Math.max(size / 2, 1))
                                         .flatMap(k -> this.searchService.search(course.toOpedukg(),
                                                                                 k)
                                                                         .stream())
                                         .collect(Collectors.toList());
        Collections.shuffle(results);
        results = results.stream()
                         .distinct()
                         .limit(size)
                         .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/question")
    public ResponseEntity<List<OpedukgAnswer>>
    question(@RequestParam final @NotNull CourseName course,
             @RequestParam final String question) {
        final List<OpedukgAnswer> answers = this.questionService.askQuestion(course.toOpedukg(),
                                                                             question);
        return ResponseEntity.ok(answers);
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
        final User user = this.userRepository.getById(currentUser.getId());
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
