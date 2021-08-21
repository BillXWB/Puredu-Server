package edu.pure.server.assembler;

import edu.pure.server.controller.RoleController;
import edu.pure.server.model.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RoleAssembler implements RepresentationModelAssembler<Role, EntityModel<Role>> {
    @Override
    public @NotNull EntityModel<Role> toModel(@NotNull final Role role) {
        return EntityModel.of(
                role,
                linkTo(methodOn(RoleController.class).getOne(role.getId())).withSelfRel(),
                linkTo(methodOn(RoleController.class).getAll()).withRel("all")
        );
    }
}
