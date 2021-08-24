package edu.pure.server.repository;

import edu.pure.server.model.Role;
import edu.pure.server.model.RoleName;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);

    boolean existsByName(RoleName roleName);
}

@AllArgsConstructor
@Component
class RoleLoader {
    private final RoleRepository repository;

    @PostConstruct
    private void load() {
        Arrays.stream(RoleName.values())
              .filter(Predicate.not(this.repository::existsByName))
              .map(Role::new)
              .forEach(this.repository::save);
    }
}
