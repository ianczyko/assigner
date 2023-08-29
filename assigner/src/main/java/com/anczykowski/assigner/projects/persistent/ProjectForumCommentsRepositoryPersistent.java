package com.anczykowski.assigner.projects.persistent;

import com.anczykowski.assigner.projects.ProjectForumCommentsRepository;
import com.anczykowski.assigner.projects.models.ProjectForumComment;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@AllArgsConstructor
public class ProjectForumCommentsRepositoryPersistent implements ProjectForumCommentsRepository {

    ProjectForumCommentsRepositoryPersistentImpl repositoryImpl;

    ModelMapper modelMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public ProjectForumComment save(ProjectForumComment projectForumComment) {
        var projectPersistent = modelMapper.map(projectForumComment, ProjectForumCommentPersistent.class);
        var projectPersistentSaved = repositoryImpl.save(projectPersistent);
        return modelMapper.map(projectPersistentSaved, ProjectForumComment.class);
    }

    @Override
    public ProjectForumComment get(Integer projectForumCommentId) {
        return modelMapper.map(repositoryImpl.getReferenceById(projectForumCommentId), ProjectForumComment.class);
    }
}

@Component
interface ProjectForumCommentsRepositoryPersistentImpl extends JpaRepository<ProjectForumCommentPersistent, Integer> {

}