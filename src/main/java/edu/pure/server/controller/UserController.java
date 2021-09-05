package edu.pure.server.controller;

import edu.pure.server.exception.ResourceNotFoundException;
import edu.pure.server.model.*;
import edu.pure.server.opedukg.entity.KnowledgeBaseEntityDetail;
import edu.pure.server.opedukg.entity.OpedukgExercise;
import edu.pure.server.opedukg.entity.SearchResult;
import edu.pure.server.opedukg.service.EntityService;
import edu.pure.server.payload.ApiResponse;
import edu.pure.server.repository.*;
import edu.pure.server.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@RolesAllowed("USER")
public class UserController {
    private final EntityService entityService;

    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final ErrorBookRepository errorBookRepository;
    private final BrowsingHistoryItemRepository browsingHistoryItemRepository;
    private final FavoriteItemRepository favoriteItemRepository;

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
    public ResponseEntity<List<KnowledgeBaseEntityDetail>>
    getBrowsingHistory(@AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        final User user = this.userRepository.getById(currentUser.getId());
        final List<KnowledgeBaseEntityDetail> entities =
                user.getBrowsingHistory().stream()
                    .map(r -> this.entityService.getEntity(r.getCourse().toOpedukg(), r.getName()))
                    .collect(Collectors.toList());
        return ResponseEntity.ok(entities);
    }

    @Transactional
    @PutMapping("/user/browsingHistory")
    public ResponseEntity<ApiResponse<?>>
    updateBrowsingHistory(@RequestBody final @NotNull List<BrowsingHistoryItem> browsingHistory,
                          @AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        final User user = this.userRepository.getById(currentUser.getId());
        this.browsingHistoryItemRepository.deleteAll(user.getBrowsingHistory());
        user.setBrowsingHistory(browsingHistory);
        this.userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Transactional(readOnly = true)
    @GetMapping("/user/favorites")
    public ResponseEntity<List<SearchResult>>
    getFavorites(@AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        final User user = this.userRepository.getById(currentUser.getId());
        final List<SearchResult> entities =
                user.getFavorites().stream()
                    .map(r -> new SearchResult(
                            this.entityService.getEntity(r.getCourse().toOpedukg(), r.getName())
                                              .toSuper(),
                            this.entityService.getCategory(r.getCourse().toOpedukg(), r.getName()))
                    )
                    .collect(Collectors.toList());
        return ResponseEntity.ok(entities);
    }

    @Transactional
    @PutMapping("/user/favorites")
    public ResponseEntity<ApiResponse<?>>
    updateFavorites(@RequestBody final @NotNull List<FavoriteItem> favorites,
                    @AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        final User user = this.userRepository.getById(currentUser.getId());
        this.favoriteItemRepository.deleteAll(user.getFavorites());
        user.setFavorites(favorites);
        this.userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success());
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
