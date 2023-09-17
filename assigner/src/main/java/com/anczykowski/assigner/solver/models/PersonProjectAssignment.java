package com.anczykowski.assigner.solver.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonProjectAssignment {
    int personId;

    int projectId;
}
