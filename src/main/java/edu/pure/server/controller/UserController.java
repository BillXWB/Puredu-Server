package edu.pure.server.controller;

import edu.pure.server.exception.ResourceNotFoundException;
import edu.pure.server.model.ErrorBookItem;
import edu.pure.server.model.Exercise;
import edu.pure.server.model.User;
import edu.pure.server.opedukg.entity.OpedukgExercise;
import edu.pure.server.repository.ErrorBookRepository;
import edu.pure.server.repository.ExerciseRepository;
import edu.pure.server.repository.UserRepository;
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

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@RolesAllowed("USER")
public class UserController {
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final ErrorBookRepository errorBookRepository;

    @GetMapping("/user/me")
    public ResponseEntity<Map<String, Object>>
    getCurrentUser(@AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        final User user = this.userRepository.findById(currentUser.getId()).get();
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
    public ResponseEntity<?>
    addExercise(@RequestParam final @NotNull List<Integer> exerciseIds,
                @AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        exerciseIds.forEach(exerciseId -> {
            if (!this.errorBookRepository.existsByUserIdAndExerciseId(currentUser.getId(),
                                                                      exerciseId)) {
                @SuppressWarnings("OptionalGetWithoutIsPresent")
                final User user = this.userRepository.findById(currentUser.getId()).get();
                final Exercise exercise = this.exerciseRepository
                        .findById(exerciseId)
                        .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id",
                                                                         exerciseId));
                this.errorBookRepository.save(new ErrorBookItem(user, exercise));
            }
        });
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @DeleteMapping("/user/errorBook")
    public ResponseEntity<?>
    deleteExercise(@RequestParam final @NotNull List<Integer> exerciseIds,
                   @AuthenticationPrincipal final @NotNull UserPrincipal currentUser) {
        exerciseIds.forEach(exerciseId -> {
            if (!this.errorBookRepository.existsByUserIdAndExerciseId(currentUser.getId(),
                                                                      exerciseId)) {
                throw new ResourceNotFoundException("Exercise in error book", "id", exerciseId);
            }
            this.errorBookRepository.deleteByUserIdAndExerciseId(currentUser.getId(), exerciseId);
        });
        return ResponseEntity.noContent().build();
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
