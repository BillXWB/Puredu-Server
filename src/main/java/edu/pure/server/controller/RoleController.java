package edu.pure.server.controller;

import edu.pure.server.exception.ResourceNotFoundException;
import edu.pure.server.model.Role;
import edu.pure.server.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/roles")
@RolesAllowed("ADMIN")
public class RoleController {
    private final RoleRepository roleRepository;

    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getOne(@PathVariable final long roleId) {
        final Role role = this.roleRepository
                .findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        return ResponseEntity.ok(role);
    }

    @GetMapping("")
    public ResponseEntity<List<Role>> getAll() {
        final List<Role> roles = this.roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }
}
