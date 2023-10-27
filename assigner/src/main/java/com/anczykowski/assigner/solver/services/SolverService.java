package com.anczykowski.assigner.solver.services;

import com.anczykowski.assigner.projects.ProjectsService;
import com.anczykowski.assigner.solver.models.AssignOptimizationResult;
import com.anczykowski.assigner.teams.TeamsRepository;
import com.anczykowski.assigner.teams.TeamsService;
import com.anczykowski.assigner.teams.models.ProjectPreference;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.cplex.IloCplex;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SolverService {
    static final double EPSILON = 1e-5;
    static final Integer DEFAULT_RATING = 3; // TODO: move somewhere more project-wise

    final TeamsService teamsService;

    final ProjectsService projectsService;

    final TeamsRepository teamsRepository;

    @Transactional
    public AssignOptimizationResult assignProjects(String courseName, String edition, String groupName) {

        var teams = teamsService.getAll(courseName, edition, groupName);
        var projects = projectsService.getProjects(courseName, edition, groupName);

        try (IloCplex cplex = new IloCplex()) {
            cplex.setOut(null);

            //// parameters
            int T = teams.size();
            var P = projects.size();

            //// variables

            var teams_project_assignment = new IloIntVar[T][];
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
                var column = cplex.linearNumExpr();
                for (int i = 0; i < T; ++i) {
                    column.addTerm(1, teams_project_assignment[i][j]);
                }
                var project = projects.get(j);
                cplex.addLe(column, project.getTeamLimit());
            }

            //// objective
            var satisfaction = cplex.linearNumExpr();
            for (int i = 0; i < T; ++i) {
                for (int j = 0; j < P; ++j) {
                    var team = teams.get(i);
                    var project = projects.get(j);
                    // TODO: below line might be worth optimizing
                    var rating = team.getPreferences().stream()
                            .filter(p -> p.getProject().getId().equals(project.getId()))
                            .findAny().map(ProjectPreference::getRating)
                            .orElse(DEFAULT_RATING);
                    satisfaction.addTerm(teams_project_assignment[i][j], rating);
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
                            var team = teams.get(i);
                            var project = projects.get(j);
                            team.setAssignedProject(project);
                            teamsRepository.save(team);
                        }
                    }
                }
                assignOptimizationResult.setTeams(teams);
                return assignOptimizationResult;
            }
        } catch (IloException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
