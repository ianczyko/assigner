package com.anczykowski.assigner.projects;

import com.anczykowski.assigner.projects.models.Project;

public interface ProjectsRepository {
    Project save(Project project);

    Project get(Integer projectId);
}
