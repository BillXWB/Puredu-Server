package edu.pure.server.model;

import edu.pure.server.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.function.Predicate;

public enum RoleName {
    USER
}

@AllArgsConstructor
@Component
class RoleLoader {
    private RoleRepository repository;

    @PostConstruct
    private void load() {
        Arrays.stream(RoleName.values())
              .filter(Predicate.not(this.repository::existsByName))
              .map(Role::new)
              .forEach(this.repository::save);
    }
}
