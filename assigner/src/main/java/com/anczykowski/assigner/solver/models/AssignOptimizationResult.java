package com.anczykowski.assigner.solver.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AssignOptimizationResult {

    double objective;

    List<TeamProjectAssignment> teamProjectAssignments = new ArrayList<>();

    public void addTeamProjectAssignment(TeamProjectAssignment teamProjectAssignment){
        teamProjectAssignments.add(teamProjectAssignment);
    }}
