package com.anczykowski.assigner.projects.persistent;

import com.anczykowski.assigner.courses.models.CourseEditionGroup;
import com.anczykowski.assigner.projects.ProjectsRepository;
import com.anczykowski.assigner.projects.models.Project;
import com.anczykowski.assigner.projects.models.ProjectForumComment;
import com.anczykowski.assigner.projects.models.projections.ProjectFlat;
import com.anczykowski.assigner.teams.models.Team;
import com.anczykowski.assigner.users.models.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ProjectsRepositoryPersistent implements ProjectsRepository {

    ProjectsRepositoryPersistentImpl repositoryImpl;

    ModelMapper modelMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Project save(Project project) {
        var projectPersistent = modelMapper.map(project, ProjectPersistent.class);
        var projectPersistentSaved = repositoryImpl.save(projectPersistent);
        return modelMapper.map(projectPersistentSaved, Project.class);
    }

    @Override
    public Project get(Integer projectId) {
        var projectPersistent = repositoryImpl.getReferenceById(projectId);
        var courseEditionGroup = projectPersistent.getCourseEditionGroup();
        return Project.builder()
                .id(projectPersistent.getId())
                .name(projectPersistent.getName())
                .description(projectPersistent.getDescription())
                .teamLimit(projectPersistent.getTeamLimit())
                .projectManager(projectPersistent.getProjectManager())
                .courseEditionGroup(CourseEditionGroup.builder()
                        .id(courseEditionGroup.getId())
                        .groupName(courseEditionGroup.getGroupName())
                        .build())
                .assignedTeams(projectPersistent
                        .getAssignedTeams()
                        .stream()
                        .map(at -> Team.builder()
                                .id(at.getId())
                                .name(at.getName())
                                .accessToken(at.getAccessToken())
                                .isAssignmentFinal(at.getIsAssignmentFinal())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public Project getFull(Integer projectId) {
        return modelMapper.map(repositoryImpl.getReferenceById(projectId), Project.class);
    }

    @Override
    public List<ProjectForumComment> getComments(Integer projectId) {
        return repositoryImpl.getReferenceById(projectId)
                .getComments()
                .stream()
                .map(c -> {
                    var author = c.getAuthor();
                    return ProjectForumComment.builder()
                            .id(c.getId())
                            .content(c.getContent())
                            .createdDate(c.getCreatedDate())
                            .author(User.builder()
                                    .id(author.getId())
                                    .name(author.getName())
                                    .usosId(author.getUsosId())
                                    .secondName(author.getSecondName())
                                    .surname(author.getSurname())
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> getAll(Integer courseEditionGroupId) {
        return repositoryImpl.findByCourseEditionGroup_IdOrderById(courseEditionGroupId)
                .stream()
                .map(c -> Project.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .description(c.getDescription())
                        .teamLimit(c.getTeamLimit())
                        .projectManager(c.getProjectManager())
                        .build())
                .toList();
    }

    @Override
    public List<ProjectFlat> getAllFlat(Integer courseEditionGroupId) {
        return repositoryImpl.findFlatByCourseEditionGroup_IdOrderById(courseEditionGroupId);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void remove(Integer projectId) {
        repositoryImpl.deleteById(projectId);
    }
}

@Component
interface ProjectsRepositoryPersistentImpl extends JpaRepository<ProjectPersistent, Integer> {
    List<ProjectFlat> findFlatByCourseEditionGroup_IdOrderById(Integer courseEditionGroupId);

    List<ProjectPersistent> findByCourseEditionGroup_IdOrderById(Integer courseEditionGroupId);
}