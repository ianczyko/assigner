package com.anczykowski.assigner.projects;

import com.anczykowski.assigner.courses.services.CourseEditionGroupsService;
import com.anczykowski.assigner.error.NotFoundException;
import com.anczykowski.assigner.projects.models.Project;
import com.anczykowski.assigner.projects.models.ProjectForumComment;
import com.anczykowski.assigner.users.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectsService {

    final ProjectsRepository projectsRepository;

    final CourseEditionGroupsService courseEditionGroupsService;

    final UsersRepository usersRepository; // TODO: maybe use service here?

    final ProjectForumCommentsRepository projectForumCommentsRepository;

    @Transactional
    public Project create(String courseName, String edition, String groupName, Project project) {
        var courseEditionGroup = courseEditionGroupsService.get(courseName, edition, groupName);
        project.setCourseEditionGroup(courseEditionGroup);
        return projectsRepository.save(project);
    }

    public Project get(Integer projectId) {
        return projectsRepository.get(projectId);
    }

    public List<Project> getProjects(String courseName, String edition, String groupName) {
        var courseEditionGroup = courseEditionGroupsService.get(courseName, edition, groupName);
        return projectsRepository.getAll(courseEditionGroup);
    }

    @Transactional
    public ProjectForumComment addProjectForumComment(
            Integer projectId,
            Integer usosId,
            ProjectForumComment projectForumComment
    ) {
        var project = projectsRepository.get(projectId);
        var user = usersRepository.getByUsosId(usosId)
                .orElseThrow(() -> new NotFoundException("user with usosId %d not found".formatted(usosId)));
        projectForumComment.setProject(project);
        projectForumComment.setAuthor(user);
        return projectForumCommentsRepository.save(projectForumComment);
    }

    public List<ProjectForumComment> getProjectForumComments(Integer projectId) {
        var project = projectsRepository.get(projectId);
        return project.getComments();
    }

    @Transactional
    public void remove(Integer projectId) {
        projectsRepository.remove(projectId);
    }

    @Transactional
    public Project changeLimit(Integer projectId, Integer newLimit) {
        var project = projectsRepository.get(projectId);
        project.setTeamLimit(newLimit);
        return projectsRepository.save(project);
    }
}
