package com.anczykowski.assigner.courses.repositories;

import com.anczykowski.assigner.courses.models.Course;

import java.util.List;

public interface CoursesRepository {
    Course save(Course course);

    List<Course> get();

    Course getByName(String courseName);
}
