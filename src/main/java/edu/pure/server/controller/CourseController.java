package edu.pure.server.controller;

import edu.pure.server.exception.ResourceNotFoundException;
import edu.pure.server.model.Course;
import edu.pure.server.repository.CourseRepository;
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
@RequestMapping("/api/courses")
@RolesAllowed("ADMIN")
public class CourseController {
    private final CourseRepository courseRepository;

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getOne(@PathVariable final long courseId) {
        final Course course = this.courseRepository
                .findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
        return ResponseEntity.ok(course);
    }

    @GetMapping("")
    public ResponseEntity<List<Course>> getAll() {
        final List<Course> courses = this.courseRepository.findAll();
        return ResponseEntity.ok(courses);
    }
}
