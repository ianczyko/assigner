package com.anczykowski.assigner.projects;

import com.anczykowski.assigner.auth.authutils.AuthUtils;
import com.anczykowski.assigner.projects.dto.ProjectDto;
import com.anczykowski.assigner.projects.models.Project;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/courses/{courseName}/editions/{edition}/projects")
public class ProjectsController {

    ModelMapper modelMapper;

    ProjectsService projectsService;

    AuthUtils authUtils;

    @PostMapping
    public ProjectDto newProject(
            @PathVariable String courseName,
            @PathVariable String edition,
            @Valid @RequestBody final ProjectDto projectDto
    ) {
        var project = modelMapper.map(projectDto, Project.class);
        var createdProject = projectsService.create(courseName, edition, project);
        return modelMapper.map(createdProject, ProjectDto.class);
    }

}
