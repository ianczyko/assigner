package com.anczykowski.assigner.projects;

import com.anczykowski.assigner.courses.models.CourseEditionGroup;
import com.anczykowski.assigner.projects.models.Project;

import java.util.List;

public interface ProjectsRepository {
    Project save(Project project);

    Project get(Integer projectId);

    List<Project> getAll(CourseEditionGroup courseEditionGroup);

    void remove(Integer projectId);
}
