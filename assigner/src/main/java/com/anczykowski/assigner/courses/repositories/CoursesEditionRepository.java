package com.anczykowski.assigner.courses.repositories;

import com.anczykowski.assigner.courses.models.CourseEdition;

import java.util.List;

public interface CoursesEditionRepository {
    CourseEdition save(CourseEdition course);

    List<CourseEdition> getAll(String courseName);

    CourseEdition get(String courseName, String edition);

    boolean checkIfUserHasAccessToCourseEdition(String courseName, String edition, String usosId);
}
