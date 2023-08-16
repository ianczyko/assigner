package com.anczykowski.assigner.projects.persistent;

import com.anczykowski.assigner.courses.models.CourseEdition;
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
    public List<Project> getAll(CourseEdition courseEdition) {
        return repositoryImpl.findByCourseEdition_Id(courseEdition.getId())
                .stream()
                .map(c -> modelMapper.map(c, Project.class))
                .toList();
    }
}

@Component
interface ProjectsRepositoryPersistentImpl extends JpaRepository<ProjectPersistent, Integer> {
    List<ProjectPersistent> findByCourseEdition_Id(Integer courseEditionId);
}