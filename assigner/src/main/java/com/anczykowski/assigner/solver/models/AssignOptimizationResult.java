package com.anczykowski.assigner.solver.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AssignOptimizationResult {

    double objective;

    List<PersonProjectAssignment> personProjectAssignments = new ArrayList<>();

    public void addPersonProjectAssignment(PersonProjectAssignment personProjectAssignment){
        personProjectAssignments.add(personProjectAssignment);
    }}
