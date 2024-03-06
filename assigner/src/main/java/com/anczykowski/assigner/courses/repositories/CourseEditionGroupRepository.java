package com.anczykowski.assigner.courses.repositories;

import com.anczykowski.assigner.courses.models.CourseEditionGroup;

import java.util.List;
import java.util.Optional;

public interface CourseEditionGroupRepository {
    CourseEditionGroup save(CourseEditionGroup course);

    List<CourseEditionGroup> getAll(String courseName, String edition);

    Optional<CourseEditionGroup> get(
            String courseName,
            String edition,
            String groupName
    );

    Optional<Integer> getId(
            String courseName,
            String edition,
            String groupName
    );

    boolean checkIfUserHasAccessToCourseEditionGroup(String courseName, String edition, String groupName, Integer usosId);
}
