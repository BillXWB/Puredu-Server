package edu.pure.server.assembler;

import edu.pure.server.controller.UserController;
import edu.pure.server.model.User;
import edu.pure.server.security.UserPrincipal;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {
    @Override
    public @NotNull EntityModel<User> toModel(@NotNull final User user) {
        return EntityModel.of(
                user,
                linkTo(methodOn(UserController.class)
                               .getOne(user.getId(), UserPrincipal.from(user))).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("all")
        );
    }
}
