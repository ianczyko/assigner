package com.anczykowski.assigner.solver.dto;

import com.anczykowski.assigner.solver.models.AssignOptimizationResult;

import java.util.List;
import java.util.stream.Collectors;

public record AssignOptimizationResponse(
    double objective,
    List<PersonProjectAssignmentResponse> personProjectAssignments
) {

    public static AssignOptimizationResponse of(AssignOptimizationResult assignOptimizationResult) {
        return new AssignOptimizationResponse(
            assignOptimizationResult.getObjective(),
            assignOptimizationResult.getPersonProjectAssignments().stream().map(PersonProjectAssignmentResponse::of).collect(
                Collectors.toList())
        );
    }
}
