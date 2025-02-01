package com.anczykowski.assigner.solver.services;

import com.anczykowski.assigner.projects.ProjectsService;
import com.anczykowski.assigner.solver.models.AssignOptimizationResult;
import com.anczykowski.assigner.teams.TeamsRepository;
import com.anczykowski.assigner.teams.TeamsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.constraints.Objective;
import javax.constraints.ProblemFactory;
import javax.constraints.VarReal;
import java.util.ArrayList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SolverService {
    static final double EPSILON = 1e-5;

    final TeamsService teamsService;

    final ProjectsService projectsService;

    final TeamsRepository teamsRepository;

    @Transactional
    public AssignOptimizationResult assignProjects(String courseName, String edition, String groupName) {

        var allTeams = teamsService.getAll(courseName, edition, groupName);
        var teams = allTeams.stream().filter(team -> !team.getIsAssignmentFinal()).toList();

        var projects = projectsService.getProjects(courseName, edition, groupName);

        var problem = ProblemFactory.newProblem("AssignProjects");

        //// parameters
        var T = teams.size();
        var P = projects.size();

        //// variables
        var teamProjectAssignment = new VarReal[T][P];
        for (var t = 0; t < T; t++) {
            for (var p = 0; p < P; p++) {
                teamProjectAssignment[t][p] = problem.variableReal("team_%d_project_%d".formatted(t, p), 0.0, 1.0);
            }
        }

        //// constraints
        // teams_project_assignment
        for (var t = 0; t < T; t++) {
            problem.post(problem.sum(teamProjectAssignment[t]), "=", 1.0);
        }

        // project_team_limits_constraint
        for (var p = 0; p < P; p++) {
            var column = new VarReal[T];
            for (var t = 0; t < T; t++) {
                column[t] = teamProjectAssignment[t][p];
            }
            var project = projects.get(p);
            problem.post(problem.sum(column), "<=", project.getEffectiveLimit());
        }

        //// objective
        var satisfaction = new ArrayList<VarReal>(T * P);
        for (int i = 0; i < T; ++i) {
            for (int j = 0; j < P; ++j) {
                var team = teams.get(i);
                var project = projects.get(j);
                var rating = team.getPreferenceFor(project);
                satisfaction.add(teamProjectAssignment[i][j].multiply(rating));
            }
        }
        var satArr = satisfaction.toArray(new VarReal[0]);
        var objective = problem.sum(satArr);
        objective.setName("objective");

        var solver = problem.getSolver();
        var solution = solver.findOptimalSolution(Objective.MAXIMIZE, objective);

        if (solution != null) {
            var assignOptimizationResult = new AssignOptimizationResult();
            assignOptimizationResult.setObjective(solution.getValueReal("objective"));
            for (var t = 0; t < T; ++t) {
                for (var p = 0; p < P; ++p) {
                    var floatAssignment = solution.getValueReal(teamProjectAssignment[t][p].getName());
                    var isAssigned = Math.abs(floatAssignment - 1.0) < EPSILON;
                    if (isAssigned) {
                        var team = teams.get(t);
                        var project = projects.get(p);
                        team.setAssignedProject(project);
                        teamsRepository.save(team);
                    }
                }
            }
            assignOptimizationResult.setTeams(teams);
            return assignOptimizationResult;

        }
        return null;
    }
}
