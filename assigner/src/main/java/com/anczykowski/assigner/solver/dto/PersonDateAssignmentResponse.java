package com.anczykowski.assigner.solver.dto;

import com.anczykowski.assigner.solver.models.PersonDateAssignment;

public record PersonDateAssignmentResponse(
    int personId,
    int dateId
) {

    public static PersonDateAssignmentResponse of(PersonDateAssignment personDateAssignment) {
        return new PersonDateAssignmentResponse(
            personDateAssignment.getPersonId(),
            personDateAssignment.getDateId()
        );
    }
}