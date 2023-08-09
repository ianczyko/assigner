package com.anczykowski.assigner.courses.repositories;

import com.anczykowski.assigner.courses.models.Course;

import java.util.List;
import java.util.Optional;

public interface CoursesRepository {
    Course save(Course course);

    List<Course> get();

    Optional<Course> getByName(String courseName);
}
