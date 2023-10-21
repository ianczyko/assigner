package com.anczykowski.assigner.courses.controllers;

import com.anczykowski.assigner.auth.authutils.AuthUtils;
import com.anczykowski.assigner.courses.dto.CourseDto;
import com.anczykowski.assigner.courses.dto.CourseEditionDto;
import com.anczykowski.assigner.courses.dto.CourseEditionShortDto;
import com.anczykowski.assigner.courses.services.CourseEditionsService;
import com.anczykowski.assigner.courses.services.CoursesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.apache.commons.io.input.BOMInputStream;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/courses")
@AllArgsConstructor
public class CoursesController {

    ModelMapper modelMapper;

    CoursesService coursesService;

    CourseEditionsService courseEditionsService;

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

    @PostMapping("/{courseName}/editions")
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public CourseEditionShortDto newCourseEdition(
            @PathVariable String courseName,
            @RequestParam String edition,
            @RequestParam(value = "file") MultipartFile file,
            HttpServletRequest request
    ) {
        var usosId = authUtils.getUsosId(request);
        try (var fileStream = file.getInputStream()) {
            var inputCsvBufferedReader = new BufferedReader(
                    new InputStreamReader(
                            new BOMInputStream(fileStream), StandardCharsets.UTF_8
                    )
            );
            return modelMapper.map(
                    courseEditionsService.create(courseName, edition, usosId, inputCsvBufferedReader),
                    CourseEditionShortDto.class
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{courseName}/editions")
    public List<CourseEditionDto> getCourseEditions(@PathVariable String courseName) {
        return courseEditionsService.getAll(courseName)
                .stream()
                .map(c -> modelMapper.map(c, CourseEditionDto.class))
                .toList();
    }

    @GetMapping("/{courseName}/editions/{edition}")
    @PreAuthorize("@authUtils.hasAccessToCourseEdition(#courseName, #edition, #request)")
    public CourseEditionDto getCourseEdition(
            HttpServletRequest request,
            @PathVariable String courseName,
            @PathVariable String edition
    ) {
        return modelMapper.map(courseEditionsService.get(courseName, edition), CourseEditionDto.class);
    }

}
