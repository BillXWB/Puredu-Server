package edu.pure.server.assembler;

import edu.pure.server.controller.CourseController;
import edu.pure.server.model.Course;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CourseAssembler implements RepresentationModelAssembler<Course, EntityModel<Course>> {
    @Override
    public @NotNull EntityModel<Course> toModel(final @NotNull Course course) {
        return EntityModel.of(
                course,
                linkTo(methodOn(CourseController.class).getOne(course.getId())).withSelfRel(),
                linkTo(methodOn(CourseController.class).getAll()).withRel("all")
        );
    }
}
