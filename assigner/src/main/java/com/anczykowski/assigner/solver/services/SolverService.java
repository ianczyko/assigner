package com.anczykowski.assigner.solver.services;

import org.springframework.stereotype.Service;

import com.anczykowski.assigner.solver.models.AssignOptimizationResult;
import com.anczykowski.assigner.solver.models.PersonDateAssignment;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;

@Service
public class SolverService {
    static final double EPSILON = 1e-5;

    public AssignOptimizationResult assignDates() {
        try (IloCplex cplex = new IloCplex()) {
            cplex.setOut(null);

            //// parameters
            // This will not be hard-coded in the future but obtained from a database
            int S = 4;
            var D = 2;

            int[][] students_date_preference = {
                {5, 1},
                {3, 4},
                {2, 5},
                {3, 2}
            };

            int[] date_student_limits = {2, 2};

            //// variables

            IloIntVar[][] students_date_assignment = new IloIntVar[S][];
            for (int i = 0; i < S; ++i) {
                students_date_assignment[i] = cplex.boolVarArray(D);
            }

            //// constraints
            // one_date_per_student
            for (int i = 0; i < S; ++i) {
                cplex.addEq(cplex.sum(students_date_assignment[i]), 1);
            }

            // date_student_limits_constraint
            for (int j = 0; j < D; ++j) {
                IloLinearNumExpr column = cplex.linearNumExpr();
                for (int i = 0; i < S; ++i) {
                    column.addTerm(1, students_date_assignment[i][j]);
                }
                cplex.addLe(column, date_student_limits[j]);
            }

            //// objective
            IloLinearNumExpr satisfaction = cplex.linearNumExpr();
            for (int i = 0; i < S; ++i) {
                for (int j = 0; j < D; ++j) {
                    satisfaction.addTerm(students_date_assignment[i][j], students_date_preference[i][j]);
                }
            }

            cplex.addMaximize(satisfaction);

            if (cplex.solve()) {
                var assignOptimizationResult = new AssignOptimizationResult();
                assignOptimizationResult.setObjective(cplex.getObjValue());
                for (int i = 0; i < S; ++i) {
                    for (int j = 0; j < D; ++j) {
                        var floatAssignment = cplex.getValue(students_date_assignment[i][j]);
                        var isAssigned = Math.abs(floatAssignment - 1.0) < EPSILON;
                        if (isAssigned) {
                            assignOptimizationResult.addPersonDateAssignment(
                                PersonDateAssignment
                                    .builder()
                                    .personId(i)
                                    .dateId(j)
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
