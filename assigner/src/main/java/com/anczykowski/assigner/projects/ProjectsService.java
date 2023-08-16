package com.anczykowski.assigner.projects;

import com.anczykowski.assigner.courses.services.CourseEditionsService;
import com.anczykowski.assigner.projects.models.Project;
import com.anczykowski.assigner.users.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectsService {

    final ProjectsRepository projectsRepository;

    final CourseEditionsService courseEditionsService;

    final UsersRepository usersRepository;
    
    @Transactional
    public Project create(String courseName, String edition, Project project) {
        var courseEdition = courseEditionsService.get(courseName, edition);
        project.setCourseEdition(courseEdition);
        return projectsRepository.save(project);
    }
    
    public Project get(Integer projectId) {
        return projectsRepository.get(projectId);
    }
    
    
}
