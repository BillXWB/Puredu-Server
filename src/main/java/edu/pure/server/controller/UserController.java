package edu.pure.server.controller;

import edu.pure.server.assembler.UserAssembler;
import edu.pure.server.exception.ResourceNotFoundException;
import edu.pure.server.model.User;
import edu.pure.server.repository.UserRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserRepository userRepository;
    private UserAssembler userAssembler;

    @GetMapping("/{userId}")
    public EntityModel<User> getOne(@PathVariable final Long userId) {
        final User user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return this.userAssembler.toModel(user);
    }

    @GetMapping("/")
    public CollectionModel<EntityModel<User>> getAll() {
        final List<EntityModel<User>> users = this.userRepository
                .findAll().stream()
                .map(this.userAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(users,
                                  linkTo(methodOn(UserController.class).getAll()).withSelfRel());
    }
}
