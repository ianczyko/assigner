package com.anczykowski.assigner.solver.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anczykowski.assigner.solver.dto.AssignOptimizationResponse;
import com.anczykowski.assigner.solver.services.SolverService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class SolverController {

    SolverService solverService;

    @GetMapping("/solve")
    public AssignOptimizationResponse solve() {
        var assignOptimizationResult = solverService.assignDates();
        return AssignOptimizationResponse.of(assignOptimizationResult);
    }
}
