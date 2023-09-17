package com.anczykowski.assigner.solver.dto;

import com.anczykowski.assigner.solver.models.AssignOptimizationResult;

import java.util.List;
import java.util.stream.Collectors;

public record AssignOptimizationResponse(
    double objective,
    List<TeamProjectAssignmentResponse> teamProjectAssignments
) {

    public static AssignOptimizationResponse of(AssignOptimizationResult assignOptimizationResult) {
        return new AssignOptimizationResponse(
            assignOptimizationResult.getObjective(),
            assignOptimizationResult.getTeamProjectAssignments().stream().map(TeamProjectAssignmentResponse::of).collect(
                Collectors.toList())
        );
    }
}
