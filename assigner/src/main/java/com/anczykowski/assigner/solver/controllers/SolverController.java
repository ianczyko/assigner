package com.anczykowski.assigner.solver.controllers;

import com.anczykowski.assigner.solver.dto.AssignOptimizationDto;
import com.anczykowski.assigner.solver.services.SolverService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/courses/{courseName}/editions/{edition}/groups/{groupName}/team-project-assignment")
public class SolverController {

    SolverService solverService;

    ModelMapper modelMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public AssignOptimizationDto solve(
            @PathVariable String courseName,
            @PathVariable String edition,
            @PathVariable String groupName
    ) {
        var assignOptimizationResult = solverService.assignProjects(courseName, edition, groupName);
        return modelMapper.map(assignOptimizationResult, AssignOptimizationDto.class);
    }
}
