package com.anczykowski.assigner.courses.repositories;

import com.anczykowski.assigner.courses.models.CourseEdition;

import java.util.List;
import java.util.Optional;

public interface CoursesEditionRepository {
    CourseEdition save(CourseEdition course);

    List<CourseEdition> getAll(String courseName);

    Optional<CourseEdition> get(String courseName, String edition);

    boolean checkIfUserHasAccessToCourseEdition(String courseName, String edition, Integer usosId);
}
