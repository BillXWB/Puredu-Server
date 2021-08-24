package edu.pure.server.controller;

import edu.pure.server.assembler.CourseAssembler;
import edu.pure.server.exception.ResourceNotFoundException;
import edu.pure.server.model.Course;
import edu.pure.server.repository.CourseRepository;
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
@RequestMapping("/api/courses")
@RolesAllowed("ADMIN")
public class CourseController {
    private final CourseRepository courseRepository;
    private final CourseAssembler courseAssembler;

    @GetMapping("/{courseId}")
    public EntityModel<Course> getOne(@PathVariable final long courseId) {
        final Course course = this.courseRepository
                .findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
        return this.courseAssembler.toModel(course);
    }

    @GetMapping("")
    public CollectionModel<EntityModel<Course>> getAll() {
        final List<EntityModel<Course>> courses = this.courseRepository
                .findAll().stream()
                .map(this.courseAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(courses,
                                  linkTo(methodOn(CourseController.class).getAll()).withSelfRel());
    }
}
