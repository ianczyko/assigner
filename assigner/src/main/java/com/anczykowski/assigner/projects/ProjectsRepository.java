package com.anczykowski.assigner.projects;

import com.anczykowski.assigner.courses.models.CourseEdition;
import com.anczykowski.assigner.projects.models.Project;

import java.util.List;

public interface ProjectsRepository {
    Project save(Project project);

    Project get(Integer projectId);

    List<Project> getAll(CourseEdition courseEdition);
}
