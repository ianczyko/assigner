package com.anczykowski.assigner.solver.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AssignOptimizationResult {

    double objective;

    List<PersonDateAssignment> personDateAssignments = new ArrayList<>();

    public void addPersonDateAssignment(PersonDateAssignment personDateAssignment){
        personDateAssignments.add(personDateAssignment);
    }}
