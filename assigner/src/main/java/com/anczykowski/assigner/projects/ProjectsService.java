package com.anczykowski.assigner.projects;

import com.anczykowski.assigner.courses.services.CourseEditionsService;
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

    final CourseEditionsService courseEditionsService;

    final UsersRepository usersRepository;

    final ProjectForumCommentsRepository projectForumCommentsRepository;

    @Transactional
    public Project create(String courseName, String edition, Project project) {
        var courseEdition = courseEditionsService.get(courseName, edition);
        project.setCourseEdition(courseEdition);
        return projectsRepository.save(project);
    }

    public Project get(Integer projectId) {
        return projectsRepository.get(projectId);
    }

    public List<Project> getProjects(String courseName, String edition) {
        var courseEdition = courseEditionsService.get(courseName, edition);
        return projectsRepository.getAll(courseEdition);
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
}
