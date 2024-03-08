package com.anczykowski.assigner.projects;

import com.anczykowski.assigner.projects.models.Project;
import com.anczykowski.assigner.projects.models.ProjectForumComment;
import com.anczykowski.assigner.projects.models.projections.ProjectFlat;

import java.util.List;

public interface ProjectsRepository {
    Project save(Project project);

    Project get(Integer projectId);

    List<Project> getAll(Integer courseEditionGroupId);
    List<ProjectFlat> getAllFlat(Integer courseEditionGroupId);

    void remove(Integer projectId);

    List<ProjectForumComment> getComments(Integer projectId);
}
