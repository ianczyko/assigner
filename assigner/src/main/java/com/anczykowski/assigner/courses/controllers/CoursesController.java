package com.anczykowski.assigner.courses.controllers;

import com.anczykowski.assigner.auth.authutils.AuthUtils;
import com.anczykowski.assigner.courses.dto.CourseDto;
import com.anczykowski.assigner.courses.dto.CourseEditionDto;
import com.anczykowski.assigner.courses.dto.CourseEditionGroupDto;
import com.anczykowski.assigner.courses.services.CourseEditionGroupsService;
import com.anczykowski.assigner.courses.services.CourseEditionService;
import com.anczykowski.assigner.courses.services.CoursesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@AllArgsConstructor
public class CoursesController {

    ModelMapper modelMapper;

    CoursesService coursesService;

    CourseEditionGroupsService courseEditionGroupsService;

    CourseEditionService courseEditionsService;

    AuthUtils authUtils;

    @PostMapping
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public CourseDto newCourse(@RequestParam String name) {
        return modelMapper.map(coursesService.create(name), CourseDto.class);
    }

    @GetMapping
    public List<CourseDto> getCourses() {
        return coursesService.getAll()
                .stream()
                .map(c -> modelMapper.map(c, CourseDto.class))
                .toList();
    }

    @GetMapping("/filtered")
    public List<CourseDto> getCoursesFiltered(HttpServletRequest request) {
        var usosId = authUtils.getUsosId(request);
        return coursesService.getAllFiltered(usosId)
                .stream()
                .map(c -> modelMapper.map(c, CourseDto.class))
                .toList();
    }

    @PostMapping("/{courseName}/editions")
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public CourseEditionDto newCourseEdition(
            @PathVariable String courseName,
            @RequestParam String edition
    ) {
        return modelMapper.map(
                courseEditionsService.create(courseName, edition),
                CourseEditionDto.class
        );
    }

    @PostMapping("/{courseName}/editions/{edition}/groups")
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public CourseEditionGroupDto newCourseEditionGroup(
            @PathVariable String courseName,
            @PathVariable String edition,
            @RequestParam String group
    ) {
        return modelMapper.map(
                courseEditionGroupsService.create(courseName, edition, group),
                CourseEditionGroupDto.class
        );
    }

    @GetMapping("/{courseName}/editions/{edition}/groups")
    public List<CourseEditionGroupDto> getCourseEditionGroups(
            @PathVariable String courseName,
            @PathVariable String edition
    ) {
        return courseEditionGroupsService.getAll(courseName, edition)
                .stream()
                .map(c -> modelMapper.map(c, CourseEditionGroupDto.class))
                .toList();
    }

    @PostMapping("/{courseName}/editions/{edition}/groups/user-reassignment")
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public ResponseEntity<Void> manualUserReassign(
            @PathVariable String courseName,
            @PathVariable String edition,
            @RequestParam(name = "group-from") String groupFrom,
            @RequestParam(name = "group-to") String groupTo,
            @RequestParam Integer usosId
    ) {
        courseEditionGroupsService.reassignUser(usosId, groupFrom, groupTo, courseName, edition);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{courseName}/editions/{edition}/groups/user-assignment")
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public ResponseEntity<Void> manualUserAssign(
            @PathVariable String courseName,
            @PathVariable String edition,
            @RequestParam String group,
            @RequestParam Integer usosId
    ) {
        courseEditionGroupsService.assignUser(usosId, courseName, edition, group);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{courseName}/editions/{edition}/groups/initial")
    public ResponseEntity<Void> pickInitialGroup(
            @PathVariable String courseName,
            @PathVariable String edition,
            @RequestParam String group,
            HttpServletRequest request
    ) {
        var usosId = authUtils.getUsosId(request);
        courseEditionGroupsService.assignUser(usosId, courseName, edition, group);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{courseName}/editions/{edition}/groups/{groupName}")
    @PreAuthorize("@authUtils.hasAccessToCourseEditionGroup(#courseName, #edition, #groupName, #request)")
    public CourseEditionGroupDto getCourseEditionGroup(
            HttpServletRequest request,
            @PathVariable String courseName,
            @PathVariable String edition,
            @PathVariable String groupName
    ) {
        return modelMapper.map(courseEditionGroupsService.get(courseName, edition, groupName), CourseEditionGroupDto.class);
    }

}
