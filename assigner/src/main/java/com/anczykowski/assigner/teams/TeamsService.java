package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.courses.services.CourseEditionGroupsService;
import com.anczykowski.assigner.error.ForbiddenException;
import com.anczykowski.assigner.error.NotFoundException;
import com.anczykowski.assigner.projects.ProjectsService;
import com.anczykowski.assigner.teams.models.ProjectPreference;
import com.anczykowski.assigner.teams.models.ProjectPreferenceId;
import com.anczykowski.assigner.teams.models.Team;
import com.anczykowski.assigner.teams.models.projections.TeamFlat;
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

    final CourseEditionGroupsService courseEditionsService;

    final ProjectsService projectsService;

    final ProjectPreferenceRepository projectPreferenceRepository;

    final UsersRepository usersRepository;

    @Value("${token.digits:6}")
    int tokenDigits;

    @Value("${token.valid-days:2}")
    int tokenValidDays;

    @Transactional
    public Team create(
            String courseName,
            String edition,
            String groupName,
            Team team,
            Integer creatorUsosId,
            Boolean addCreator) {

        if (addCreator) {
            var creator = usersRepository.getByUsosId(creatorUsosId)
                    .orElseThrow(() -> new NotFoundException("user with usosId %d not found".formatted(creatorUsosId)));
            team.addMember(creator);
        }

        var courseEdition = courseEditionsService.get(courseName, edition, groupName);
        team.setCourseEditionGroup(courseEdition);
        return teamsRepository.save(team);
    }

    public List<Team> getAll(String courseName, String edition, String groupName) {
        var courseEditionId = courseEditionsService.getId(courseName, edition, groupName);
        return teamsRepository.getAll(courseEditionId);
    }

    public List<TeamFlat> getAllFlat(String courseName, String edition, String groupName) {
        var courseEditionId = courseEditionsService.getId(courseName, edition, groupName);
        return teamsRepository.getAllFlat(courseEditionId);
    }

    @Transactional
    public Team generateAccessToken(Integer teamId) {
        var team = teamsRepository.getFull(teamId);
        team.regenerateAccessToken(tokenDigits, tokenValidDays);
        return teamsRepository.save(team);
    }

    public Team get(Integer teamId) {
        return teamsRepository.get(teamId);
    }

    @Transactional
    public Set<User> addMember(Integer teamId, Integer accessToken, Integer usosId) {
        var team = teamsRepository.getFull(teamId);
        if (!team.getAccessToken().equals(accessToken)) {
            throw new ForbiddenException("Unmatched access token");
        }
        var member = usersRepository.getByUsosId(usosId)
                .orElseThrow(() -> new NotFoundException("user with usosId %d not found".formatted(usosId)));

        var teamSaved = teamsRepository.addMemberToTeam(teamId, member);
        return teamSaved.getMembers();
    }

    public Set<User> getTeamMembers(Integer teamId) {
        var team = teamsRepository.get(teamId);
        return team.getMembers();
    }

    @Transactional
    public ProjectPreference rateProject(Integer teamId, Integer projectId, Integer rating) {
        var project = projectsService.getFull(projectId);
        var team = teamsRepository.getFull(teamId);
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

    @Transactional
    public Team assignProject(Integer teamId, Integer projectId) {
        var team = teamsRepository.getFull(teamId);
        var project = projectId == null ? null : projectsService.getFull(projectId);
        team.setAssignedProject(project);
        return teamsRepository.save(team);
    }

    public List<ProjectPreference> getRatingsView(
            String courseName,
            String edition,
            String groupName,
            Integer teamId
    ) {
        var team = teamsRepository.get(teamId);
        var projectPreferenceMap = team.getPreferences().stream().collect(Collectors.toMap(
                item -> item.getProject().getId(),
                item -> item)
        );

        var projects = projectsService.getProjects(courseName, edition, groupName);
        return projects.stream().map(
                project -> projectPreferenceMap.getOrDefault(project.getId(), ProjectPreference.builder()
                        .rating(team.getDefaultRating())
                        .team(team)
                        .project(project)
                        .build()
                )
        ).toList();
    }

    public Optional<Team> getAssignedTeam(String courseName, String edition, String groupName, Integer usosId) {
        var accesses = usersRepository.getAssignedTeamByUsosId(usosId);
        return accesses.stream().filter(acc ->
                acc.getCourseEditionGroup().getGroupName().equals(groupName) &&
                acc.getCourseEditionGroup().getCourseEdition().getEdition().equals(edition) &&
                acc.getCourseEditionGroup().getCourseEdition().getCourse().getName().equals(courseName)
        ).findAny();
    }

    @Transactional
    public Team setIsAssignmentFinal(Integer teamId, Boolean isApproved) {
        var team = teamsRepository.getFull(teamId);
        team.setIsAssignmentFinal(isApproved);
        return teamsRepository.save(team);
    }

    @Transactional
    public void manualTeamAssign(Integer usosId, Integer teamId, Integer previousTeamId) {

        var user = usersRepository.getByUsosId(usosId)
                .orElseThrow(() -> new NotFoundException("user with usosId %d not found".formatted(usosId)));

        if (previousTeamId != null) {
            teamsRepository.removeMemberFromTeam(previousTeamId, user);
        }

        if (teamId != null) {
            teamsRepository.addMemberToTeam(teamId, user);
        }
    }

    @Transactional
    public void leaveTeam(Integer teamId, Integer usosId) {
        var user = usersRepository.getByUsosId(usosId)
                .orElseThrow(() -> new NotFoundException("user with usosId %d not found".formatted(usosId)));

        teamsRepository.removeMemberFromTeam(teamId, user);
    }

    @Transactional
    public void remove(Integer teamId) {
        teamsRepository.remove(teamId);
    }
}
