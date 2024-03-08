package com.anczykowski.assigner.projects.persistent;

import com.anczykowski.assigner.projects.ProjectsRepository;
import com.anczykowski.assigner.projects.models.Project;
import com.anczykowski.assigner.projects.models.projections.ProjectFlat;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return modelMapper.map(repositoryImpl.getReferenceById(projectId), Project.class);
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