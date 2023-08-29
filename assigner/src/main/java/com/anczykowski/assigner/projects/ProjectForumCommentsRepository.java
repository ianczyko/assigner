package com.anczykowski.assigner.projects;

import com.anczykowski.assigner.projects.models.ProjectForumComment;

public interface ProjectForumCommentsRepository {
    ProjectForumComment save(ProjectForumComment projectForumComment);

    ProjectForumComment get(Integer projectForumCommentId);
}
