package edu.pure.server.controller;

import edu.pure.server.exception.ResourceNotFoundException;
import edu.pure.server.model.*;
import edu.pure.server.opedukg.entity.KnowledgeBaseEntityDetail;
import edu.pure.server.opedukg.entity.OpedukgExercise;
import edu.pure.server.opedukg.service.EntityService;
import edu.pure.server.opedukg.service.ExerciseService;
import edu.pure.server.opedukg.service.SearchService;
import edu.pure.server.payload.ApiResponse;
import edu.pure.server.payload.BrowsingHistoryResponse;
import edu.pure.server.payload.FavoriteResponse;
import edu.pure.server.repository.*;
import edu.pure.server.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@RolesAllowed("USER")
public class UserController {
    private final SearchService searchService;
    private final EntityService entityService;
    private final ExerciseService exerciseService;

    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final ErrorBookRepository errorBookRepository;
    private final BrowsingHistoryItemRepository browsingHistoryItemRepository;
    private final FavoriteItemRepository favoriteItemRepository;

    @Value("${user-controller.exercise-entity-names}")
    private List<String> exerciseEntityNames;

    @GetMapping("/user/me")
    public ResponseEntity<Map<String, Object>>
    getCurrentUser(@AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        final User user = this.userRepository.getById(currentUser.getId());
        return ResponseEntity.ok(Map.of("id", user.getId(),
                                        "name", user.getName(),
                                        "username", user.getUsername(),
                                        "email", user.getEmail(),
                                        "createdAt", user.getCreatedAt(),
                                        "updatedAt", user.getUpdatedAt()));
    }

    @GetMapping("/user/errorBook")
    public ResponseEntity<Page<OpedukgExercise>>
    getErrorBook(final Pageable pageable,
                 @AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        final Page<OpedukgExercise> exercises = this.errorBookRepository
                .findAllByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable)
                .map(ErrorBookItem::getExercise);
        return ResponseEntity.ok(exercises);
    }

    @PostMapping("/user/errorBook")
    public ResponseEntity<ApiResponse<?>>
    addExercise(@RequestParam final int exerciseId,
                @AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        if (!this.errorBookRepository.existsByUserIdAndExerciseId(currentUser.getId(),
                                                                  exerciseId)) {
            final User user = this.userRepository.getById(currentUser.getId());
            final Exercise exercise = this.exerciseRepository
                    .findById(exerciseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", exerciseId));
            this.errorBookRepository.save(new ErrorBookItem(user, exercise));
        }
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Transactional
    @DeleteMapping("/user/errorBook")
    public ResponseEntity<ApiResponse<?>>
    deleteExercise(@RequestParam final int exerciseId,
                   @AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        if (!this.errorBookRepository.existsByUserIdAndExerciseId(currentUser.getId(),
                                                                  exerciseId)) {
            throw new ResourceNotFoundException("Exercise in error book", "id", exerciseId);
        }
        this.errorBookRepository.deleteByUserIdAndExerciseId(currentUser.getId(), exerciseId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Transactional
    @PutMapping("/user/errorBook")
    public ResponseEntity<ApiResponse<?>>
    updateExercise(@RequestBody final @NotNull List<Integer> exerciseIds,
                   @AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        this.errorBookRepository.deleteByUserId(currentUser.getId());
        exerciseIds.forEach(exerciseId -> {
            if (!this.exerciseRepository.existsById(exerciseId)) {
                throw new ResourceNotFoundException("Exercise", "id", exerciseId);
            }
        });
        final User user = this.userRepository.getById(currentUser.getId());
        final List<ErrorBookItem> errorBook =
                exerciseIds.stream()
                           .map(this.exerciseRepository::findById)
                           .map(Optional::get)
                           .map(exercise -> new ErrorBookItem(user, exercise))
                           .collect(Collectors.toList());
        this.errorBookRepository.saveAll(errorBook);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Transactional(readOnly = true)
    @GetMapping("/user/browsingHistory")
    public ResponseEntity<List<BrowsingHistoryResponse>>
    getBrowsingHistory(@AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        final List<BrowsingHistoryResponse> entities =
                this.browsingHistoryItemRepository.findAllByUserId(currentUser.getId()).stream()
                                                  .map(r -> new BrowsingHistoryResponse(
                                                          this.entityService.getEntity(
                                                                  r.getCourse().toOpedukg(),
                                                                  r.getName()),
                                                          r.getCourse())
                                                  )
                                                  .collect(Collectors.toList());
        return ResponseEntity.ok(entities);
    }

    @Transactional
    @PutMapping("/user/browsingHistory")
    public ResponseEntity<ApiResponse<?>>
    updateBrowsingHistory(@RequestBody final @SuppressWarnings("unused") @NotNull
                                  List<BrowsingHistoryItem> browsingHistory) {
        // 前后端分别独立记录，后端在 `OpedukgController::getEntityByName` 已经记录
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Transactional(readOnly = true)
    @GetMapping("/user/favorites")
    public ResponseEntity<List<FavoriteResponse>>
    getFavorites(@AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        final List<FavoriteResponse> entities =
                this.favoriteItemRepository.findAllByUserId(currentUser.getId()).stream()
                                           .map(r -> {
                                               final KnowledgeBaseEntityDetail entity =
                                                       this.entityService.getEntity(
                                                               r.getCourse().toOpedukg(),
                                                               r.getName());
                                               return new FavoriteResponse(entity.toSuper(),
                                                                           r.getCourse(),
                                                                           entity.getCategory());
                                           })
                                           .collect(Collectors.toList());
        return ResponseEntity.ok(entities);
    }

    @Transactional
    @PutMapping("/user/favorites")
    public ResponseEntity<ApiResponse<?>>
    updateFavorites(@RequestBody final @NotNull List<FavoriteItem> favorites,
                    @AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        this.favoriteItemRepository.deleteAllByUserId(currentUser.getId());
        final User user = this.userRepository.getById(currentUser.getId());
        favorites.forEach(f -> f.setUser(user));
        this.favoriteItemRepository.saveAll(favorites);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Transactional(readOnly = true)
    @GetMapping("/user/exercises")
    public ResponseEntity<?>
    getExercises(final int size,
                 @AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        // TODO: 对试题搜索结果进行缓存，以优化性能。
        //  由于错题本的题目必须存在于数据库中，所以此处返回的错题也需要加入数据库。
        final List<BrowsingHistoryItem> browsingHistory =
                this.browsingHistoryItemRepository.findAllByUserId(currentUser.getId());
        final var exercises = new HashSet<OpedukgExercise>();
        final BiFunction<String, Integer, Stream<OpedukgExercise>> exerciseSupplier =
                (entityName, limit) -> this.exerciseService.getExercise(entityName)
                                                           .stream()
                                                           .filter(Predicate.not(
                                                                   exercises::contains))
                                                           .limit(limit);
        if (!browsingHistory.isEmpty()) {
            final var weights = new ArrayList<Integer>(browsingHistory.size());
            int sum = 0;
            for (final var h : browsingHistory) {
                sum += h.getCount();
                weights.add(sum);
            }
            final var random = new Random();
            for (int tried = 0; tried < 3 * size && exercises.size() < size * 0.7; ++tried) {
                int index = Collections.binarySearch(weights, random.nextInt(sum));
                if (index < 0) {
                    index = -1 - index;
                }
                exerciseSupplier.apply(browsingHistory.get(index).getName(),
                                       Math.min(5, Math.max(1, size / 4)))
                                .forEach(exercises::add);
            }
        }
        final var random = new Random();
        if (exercises.size() < size) {
            this.errorBookRepository.findAllByUserId(currentUser.getId()).stream()
                                    .map(ErrorBookItem::getExercise)
                                    .map(OpedukgExercise.class::cast)
                                    .filter(Predicate.not(exercises::contains))
                                    .collect(Collectors.collectingAndThen(
                                            Collectors.toList(),
                                            list -> {
                                                Collections.shuffle(list);
                                                return list;
                                            }
                                    )).stream()
                                    .takeWhile(e -> random.nextDouble() < 0.2)
                                    .forEach(exercises::add);
        }
        for (int tried = 0; tried < 3 * size && exercises.size() < size; ++tried) {
            final String entityName =
                    this.exerciseEntityNames.get(random.nextInt(this.exerciseEntityNames.size()));
            exerciseSupplier.apply(entityName, Math.max(1, size / 3))
                            .forEach(exercises::add);
        }
        return ResponseEntity.ok(exercises.stream()
                                          .limit(size)
                                          .collect(Collectors.collectingAndThen(
                                                  Collectors.toList(),
                                                  list -> {
                                                      Collections.shuffle(list);
                                                      return list;
                                                  }
                                          ))
        );
    }

    @GetMapping("/users/{userId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<User> getOne(@PathVariable final long userId) {
        final User user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<User>> getAll() {
        final List<User> users = this.userRepository.findAll();
        return ResponseEntity.ok(users);
    }
}
