package edu.pure.server.controller;

import edu.pure.server.assembler.RoleAssembler;
import edu.pure.server.exception.ResourceNotFoundException;
import edu.pure.server.model.Role;
import edu.pure.server.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@AllArgsConstructor
@RestController
@RequestMapping("/api/roles")
@RolesAllowed("ADMIN")
public class RoleController {
    private final RoleRepository roleRepository;
    private final RoleAssembler roleAssembler;

    @GetMapping("/{roleId}")
    public EntityModel<Role> getOne(@PathVariable final long roleId) {
        final Role role = this.roleRepository
                .findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        return this.roleAssembler.toModel(role);
    }

    @GetMapping("")
    public CollectionModel<EntityModel<Role>> getAll() {
        final List<EntityModel<Role>> roles = this.roleRepository
                .findAll().stream()
                .map(this.roleAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(roles,
                                  linkTo(methodOn(RoleController.class).getAll()).withSelfRel());
    }
}
