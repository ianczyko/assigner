package com.anczykowski.assigner.projects.persistent;

import com.anczykowski.assigner.courses.models.CourseEditionGroup;
import com.anczykowski.assigner.projects.ProjectsRepository;
import com.anczykowski.assigner.projects.models.Project;
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
    public List<Project> getAll(CourseEditionGroup courseEditionGroup) {
        return repositoryImpl.findByCourseEditionGroup_Id(courseEditionGroup.getId())
                .stream()
                .map(c -> modelMapper.map(c, Project.class))
                .toList();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void remove(Integer projectId) {
        repositoryImpl.deleteById(projectId);
    }
}

@Component
interface ProjectsRepositoryPersistentImpl extends JpaRepository<ProjectPersistent, Integer> {
    List<ProjectPersistent> findByCourseEditionGroup_Id(Integer courseEditionGroupId);
}