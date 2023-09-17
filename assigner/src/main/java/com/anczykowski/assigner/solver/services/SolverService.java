package com.anczykowski.assigner.solver.services;

import org.springframework.stereotype.Service;

import com.anczykowski.assigner.solver.models.AssignOptimizationResult;
import com.anczykowski.assigner.solver.models.PersonProjectAssignment;
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
            int S = 4;
            var P = 2;

            int[][] students_project_preference = {
                {5, 1},
                {3, 4},
                {2, 5},
                {3, 2}
            };

            int[] project_student_limits = {2, 2};

            //// variables

            IloIntVar[][] students_project_assignment = new IloIntVar[S][];
            for (int i = 0; i < S; ++i) {
                students_project_assignment[i] = cplex.boolVarArray(P);
            }

            //// constraints
            // one_project_per_student
            for (int i = 0; i < S; ++i) {
                cplex.addEq(cplex.sum(students_project_assignment[i]), 1);
            }

            // project_student_limits_constraint
            for (int j = 0; j < P; ++j) {
                IloLinearNumExpr column = cplex.linearNumExpr();
                for (int i = 0; i < S; ++i) {
                    column.addTerm(1, students_project_assignment[i][j]);
                }
                cplex.addLe(column, project_student_limits[j]);
            }

            //// objective
            IloLinearNumExpr satisfaction = cplex.linearNumExpr();
            for (int i = 0; i < S; ++i) {
                for (int j = 0; j < P; ++j) {
                    satisfaction.addTerm(students_project_assignment[i][j], students_project_preference[i][j]);
                }
            }

            cplex.addMaximize(satisfaction);

            if (cplex.solve()) {
                var assignOptimizationResult = new AssignOptimizationResult();
                assignOptimizationResult.setObjective(cplex.getObjValue());
                for (int i = 0; i < S; ++i) {
                    for (int j = 0; j < P; ++j) {
                        var floatAssignment = cplex.getValue(students_project_assignment[i][j]);
                        var isAssigned = Math.abs(floatAssignment - 1.0) < EPSILON;
                        if (isAssigned) {
                            assignOptimizationResult.addPersonProjectAssignment(
                                PersonProjectAssignment
                                    .builder()
                                    .personId(i)
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
