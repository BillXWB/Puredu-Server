package edu.pure.server.repository;

import edu.pure.server.model.Course;
import edu.pure.server.model.CourseName;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByName(CourseName courseName);

    boolean existsByName(CourseName courseName);
}

@AllArgsConstructor
@Component
class CourseLoader {
    private final CourseRepository repository;

    @PostConstruct
    private void load() {
        Arrays.stream(CourseName.values())
              .filter(Predicate.not(this.repository::existsByName))
              .map(Course::new)
              .forEach(this.repository::save);
    }
}
