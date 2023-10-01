package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.courses.services.CourseEditionsService;
import com.anczykowski.assigner.error.NotFoundException;
import com.anczykowski.assigner.error.ForbiddenException;
import com.anczykowski.assigner.projects.ProjectsService;
import com.anczykowski.assigner.teams.models.ProjectPreference;
import com.anczykowski.assigner.teams.models.ProjectPreferenceId;
import com.anczykowski.assigner.teams.models.Team;
import com.anczykowski.assigner.users.UsersRepository;
import com.anczykowski.assigner.users.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamsService {

    final TeamsRepository teamsRepository;

    final CourseEditionsService courseEditionsService;

    final ProjectsService projectsService;

    final ProjectPreferenceRepository projectPreferenceRepository;

    final UsersRepository usersRepository;

    static final Integer DEFAULT_RATING = 3; // TODO: move somewhere more project-wise

    @Value("${token.digits:6}")
    int tokenDigits;

    @Value("${token.valid-days:2}")
    int tokenValidDays;

    @Transactional
    public Team create(String courseName, String edition, Team team, Integer leaderUsosId) {
        var leader = usersRepository.getByUsosId(leaderUsosId)
                .orElseThrow(() -> new NotFoundException("user with usosId %d not found".formatted(leaderUsosId)));
        team.setLeader(leader);
        team.addMember(leader);

        var courseEdition = courseEditionsService.get(courseName, edition);
        team.setCourseEdition(courseEdition);
        return teamsRepository.save(team);
    }

    public List<Team> getAll(String courseName, String edition) {
        var courseEdition = courseEditionsService.get(courseName, edition);
        return teamsRepository.getAll(courseEdition);
    }

    @Transactional
    public Team generateAccessToken(Integer teamId) {
        var team = teamsRepository.get(teamId);
        team.regenerateAccessToken(tokenDigits, tokenValidDays);
        return teamsRepository.save(team);
    }

    public Team get(Integer teamId) {
        return teamsRepository.get(teamId);
    }

    @Transactional
    public Set<User> addMember(Integer teamId, Integer accessToken, Integer usosId) {
        var team = teamsRepository.get(teamId);
        if (!team.getAccessToken().equals(accessToken)) {
            throw new ForbiddenException("Unmatched access token");
        }
        var member = usersRepository.getByUsosId(usosId)
                .orElseThrow(() -> new NotFoundException("user with usosId %d not found".formatted(usosId)));

        team.addMember(member);
        teamsRepository.save(team);
        return team.getMembers();
    }

    public Set<User> getTeamMembers(Integer teamId) {
        var team = teamsRepository.get(teamId);
        return team.getMembers();
    }

    @Transactional
    public ProjectPreference rateProject(Integer teamId, Integer projectId, Integer rating) {
        var project = projectsService.get(projectId);
        var team = teamsRepository.get(teamId);
        var preference = ProjectPreference.builder()
                .id(new ProjectPreferenceId(project.getId(), team.getId()))
                .project(project)
                .team(team)
                .rating(rating)
                .build();
        return projectPreferenceRepository.save(preference);
    }

    public List<ProjectPreference> getRatings(Integer teamId) {
        var team = teamsRepository.get(teamId);
        return team.getPreferences();
    }

    public Team assignProject(Integer teamId, Integer projectId) {
        var team = teamsRepository.get(teamId);
        var project = projectsService.get(projectId);
        team.setAssignedProject(project);
        return teamsRepository.save(team);
    }

    public List<ProjectPreference> getRatingsView(String courseName, String edition, Integer teamId) {
        var team = teamsRepository.get(teamId);
        var projectPreferenceMap = team.getPreferences().stream().collect(Collectors.toMap(
                item -> item.getProject().getId(),
                item -> item)
        );

        var projects = projectsService.getProjects(courseName, edition);
        return projects.stream().map(
                project -> projectPreferenceMap.getOrDefault(project.getId(), ProjectPreference.builder()
                        .rating(DEFAULT_RATING)
                        .team(team)
                        .project(project)
                        .build()
                )
        ).toList();
    }

    public Optional<Team> getAssignedTeam(String edition, Integer usosId) {
        var accesses =  usersRepository.getAssignedTeamByUsosId(usosId);
        return accesses.stream().filter(acc -> acc.getCourseEdition().getEdition().equals(edition)).findAny();
    }
}
