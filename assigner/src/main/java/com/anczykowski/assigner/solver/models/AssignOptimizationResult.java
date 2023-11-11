package com.anczykowski.assigner.solver.models;

import com.anczykowski.assigner.teams.models.Team;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AssignOptimizationResult {

    double objective;

    List<Team> teams;

}