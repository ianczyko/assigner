package com.anczykowski.assigner.solver.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamProjectAssignment {
    int teamId;

    int projectId;
}
