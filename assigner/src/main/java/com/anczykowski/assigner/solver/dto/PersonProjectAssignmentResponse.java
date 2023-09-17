package com.anczykowski.assigner.solver.dto;

import com.anczykowski.assigner.solver.models.PersonProjectAssignment;

public record PersonProjectAssignmentResponse(
    int personId,
    int projectId
) {

    public static PersonProjectAssignmentResponse of(PersonProjectAssignment personProjectAssignment) {
        return new PersonProjectAssignmentResponse(
            personProjectAssignment.getPersonId(),
            personProjectAssignment.getProjectId()
        );
    }
}