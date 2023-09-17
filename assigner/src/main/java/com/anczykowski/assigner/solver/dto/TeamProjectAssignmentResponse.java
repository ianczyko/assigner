package com.anczykowski.assigner.solver.dto;

import com.anczykowski.assigner.solver.models.TeamProjectAssignment;

public record TeamProjectAssignmentResponse(
    int teamId,
    int projectId
) {

    public static TeamProjectAssignmentResponse of(TeamProjectAssignment teamProjectAssignment) {
        return new TeamProjectAssignmentResponse(
            teamProjectAssignment.getTeamId(),
            teamProjectAssignment.getProjectId()
        );
    }
}