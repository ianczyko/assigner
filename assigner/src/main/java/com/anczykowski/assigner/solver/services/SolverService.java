package com.anczykowski.assigner.solver.services;

import org.springframework.stereotype.Service;

import com.anczykowski.assigner.solver.models.AssignOptimizationResult;
import com.anczykowski.assigner.solver.models.TeamProjectAssignment;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SolverService {
    static final double EPSILON = 1e-5;

    public AssignOptimizationResult assignProjects() {
        try (IloCplex cplex = new IloCplex()) {
            cplex.setOut(null);

            //// parameters
            // This will not be hard-coded in the future but obtained from a database
            int T = 4;
            var P = 2;

            int[][] teams_project_preference = {
                {5, 1},
                {3, 4},
                {2, 5},
                {3, 2}
            };

            int[] project_team_limits = {2, 2};

            //// variables

            IloIntVar[][] teams_project_assignment = new IloIntVar[T][];
            for (int i = 0; i < T; ++i) {
                teams_project_assignment[i] = cplex.boolVarArray(P);
            }

            //// constraints
            // one_project_per_team
            for (int i = 0; i < T; ++i) {
                cplex.addEq(cplex.sum(teams_project_assignment[i]), 1);
            }

            // project_team_limits_constraint
            for (int j = 0; j < P; ++j) {
                IloLinearNumExpr column = cplex.linearNumExpr();
                for (int i = 0; i < T; ++i) {
                    column.addTerm(1, teams_project_assignment[i][j]);
                }
                cplex.addLe(column, project_team_limits[j]);
            }

            //// objective
            IloLinearNumExpr satisfaction = cplex.linearNumExpr();
            for (int i = 0; i < T; ++i) {
                for (int j = 0; j < P; ++j) {
                    satisfaction.addTerm(teams_project_assignment[i][j], teams_project_preference[i][j]);
                }
            }

            cplex.addMaximize(satisfaction);

            if (cplex.solve()) {
                var assignOptimizationResult = new AssignOptimizationResult();
                assignOptimizationResult.setObjective(cplex.getObjValue());
                for (int i = 0; i < T; ++i) {
                    for (int j = 0; j < P; ++j) {
                        var floatAssignment = cplex.getValue(teams_project_assignment[i][j]);
                        var isAssigned = Math.abs(floatAssignment - 1.0) < EPSILON;
                        if (isAssigned) {
                            assignOptimizationResult.addTeamProjectAssignment(
                                TeamProjectAssignment
                                    .builder()
                                    .teamId(i)
                                    .projectId(j)
                                    .build()
                            );
                        }
                    }
                }
                return assignOptimizationResult;
            }
        } catch (IloException e) {
            // TODO: better error handling
            e.printStackTrace();
        }
        return null;
    }

}
