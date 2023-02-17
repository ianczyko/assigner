package com.anczykowski.assigner.solver.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.anczykowski.assigner.solver.models.AssignOptimizationResult;

public record AssignOptimizationResponse(
    double objective,
    List<PersonDateAssignmentResponse> personDateAssignments
) {

    public static AssignOptimizationResponse of(AssignOptimizationResult assignOptimizationResult) {
        return new AssignOptimizationResponse(
            assignOptimizationResult.getObjective(),
            assignOptimizationResult.getPersonDateAssignments().stream().map(PersonDateAssignmentResponse::of).collect(
                Collectors.toList())
        );
    }
}
