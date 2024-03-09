package com.anczykowski.assigner.projects;

import com.anczykowski.assigner.auth.authutils.AuthUtils;
import com.anczykowski.assigner.projects.dto.ProjectDto;
import com.anczykowski.assigner.projects.dto.ProjectFlatDto;
import com.anczykowski.assigner.projects.dto.ProjectForumCommentDto;
import com.anczykowski.assigner.projects.models.Project;
import com.anczykowski.assigner.projects.models.ProjectForumComment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/courses/{courseName}/editions/{edition}/groups/{groupName}/projects")
public class ProjectsController {

    ModelMapper modelMapper;

    ProjectsService projectsService;

    AuthUtils authUtils;

    @PostMapping
    @PreAuthorize("hasAuthority('COORDINATOR') or hasAuthority('TEACHER')")
    public ProjectDto newProject(
            @PathVariable String courseName,
            @PathVariable String edition,
            @PathVariable String groupName,
            @Valid @RequestBody final ProjectDto projectDto
    ) {
        var project = modelMapper.map(projectDto, Project.class);
        var createdProject = projectsService.create(courseName, edition, groupName, project);
        return modelMapper.map(createdProject, ProjectDto.class);
    }

    @GetMapping
    public List<ProjectFlatDto> getProjects(
            @PathVariable String courseName,
            @PathVariable String edition,
            @PathVariable String groupName
    ) {
        return projectsService.getProjectsFlat(courseName, edition, groupName)
                .stream()
                .map(c -> modelMapper.map(c, ProjectFlatDto.class))
                .toList();
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProject(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @SuppressWarnings("unused") @PathVariable String groupName,
            @PathVariable Integer projectId
    ) {
        return modelMapper.map(projectsService.get(projectId), ProjectDto.class);
    }

    @PutMapping("/{projectId}/limit")
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public ProjectDto changeProjectLimit(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @SuppressWarnings("unused") @PathVariable String groupName,
            @PathVariable Integer projectId,
            @RequestParam("new_limit") Integer newLimit
    ) {
        return modelMapper.map(projectsService.changeLimit(projectId, newLimit), ProjectDto.class);
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public ResponseEntity<Void> deleteProject(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @SuppressWarnings("unused") @PathVariable String groupName,
            @PathVariable Integer projectId
    ) {
        projectsService.remove(projectId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{projectId}/forum-comments")
    @PreAuthorize("@authUtils.hasAccessToCourseEditionGroup(#courseName, #edition, #groupName, #request)")
    public ProjectForumCommentDto addProjectForumComment(
            @PathVariable Integer projectId,
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @SuppressWarnings("unused") @PathVariable String groupName,
            HttpServletRequest request,
            @Valid @RequestBody final ProjectForumCommentDto projectForumCommentDto
    ) {
        var usosId = authUtils.getUsosId(request);
        var projectForumComment = modelMapper.map(projectForumCommentDto, ProjectForumComment.class);
        var projectForumCommentCreated = projectsService.addProjectForumComment(projectId, usosId, projectForumComment);
        return modelMapper.map(projectForumCommentCreated, ProjectForumCommentDto.class);
    }

    @GetMapping("/{projectId}/forum-comments")
    @PreAuthorize("@authUtils.hasAccessToCourseEditionGroup(#courseName, #edition, #groupName, #request)")
    public List<ProjectForumCommentDto> getProjectForumComments(
            @PathVariable Integer projectId,
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @SuppressWarnings("unused") @PathVariable String groupName,
            HttpServletRequest request
    ) {
        return projectsService.getProjectForumComments(projectId)
                .stream()
                .map(c -> modelMapper.map(c, ProjectForumCommentDto.class))
                .toList();
    }

    @PutMapping("/{projectId}/description")
    @PreAuthorize("hasAuthority('COORDINATOR') or hasAuthority('TEACHER')")
    public ProjectDto updateDescription(
            @PathVariable Integer projectId,
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @SuppressWarnings("unused") @PathVariable String groupName,
            @RequestParam("new-description") String newDescription
    ) {
        var project = projectsService.updateDescription(projectId, newDescription);
        return modelMapper.map(project, ProjectDto.class);
    }

}
