package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.auth.authutils.AuthUtils;
import com.anczykowski.assigner.teams.dto.*;
import com.anczykowski.assigner.teams.models.Team;
import com.anczykowski.assigner.users.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/courses/{courseName}/editions/{edition}/groups/{groupName}/teams")
public class TeamsController {

    ModelMapper modelMapper;

    TeamsService teamsService;

    AuthUtils authUtils;

    @PostMapping
    public TeamDto newTeam(
            @PathVariable String courseName,
            @PathVariable String edition,
            @PathVariable String groupName,
            @Valid @RequestBody final TeamDto teamDto,
            @RequestParam(name = "add-creator", defaultValue = "true") Boolean addCreator,
            HttpServletRequest request
    ) {
        var usosId = authUtils.getUsosId(request);
        var team = modelMapper.map(teamDto, Team.class);
        var createdTeam = teamsService.create(courseName, edition, groupName, team, usosId, addCreator);
        return modelMapper.map(createdTeam, TeamDto.class);
    }

    @PutMapping("/{teamId}/access-token")
    public TeamAccessTokenDto generateAccessToken(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @SuppressWarnings("unused") @PathVariable String groupName,
            @PathVariable Integer teamId) {
        var team = teamsService.generateAccessToken(teamId);
        return modelMapper.map(team, TeamAccessTokenDto.class);
    }

    @GetMapping("/{teamId}/access-token")
    @PreAuthorize("@authUtils.hasAccessToTeam(#teamId, #request)")
    public TeamAccessTokenDto getAccessToken(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @SuppressWarnings("unused") @PathVariable String groupName,
            @PathVariable Integer teamId,
            HttpServletRequest request
    ) {
        var team = teamsService.get(teamId);
        return modelMapper.map(team, TeamAccessTokenDto.class);
    }

    @PostMapping("/{teamId}/members")
    @PreAuthorize("@authUtils.hasAccessToCourseEditionGroup(#courseName, #edition, #groupName, #request)")
    public List<UserDto> addTeamMember(
            @PathVariable String courseName,
            @PathVariable String edition,
            @PathVariable String groupName,
            @PathVariable Integer teamId,
            @RequestParam(name = "access-token") Integer accessToken,
            HttpServletRequest request
    ) {
        var usosId = authUtils.getUsosId(request);
        return teamsService.addMember(teamId, accessToken, usosId)
                .stream()
                .map(c -> modelMapper.map(c, UserDto.class))
                .toList();
    }

    @PostMapping("/{teamId}/leave")
    @PreAuthorize("@authUtils.hasAccessToCourseEditionGroup(#courseName, #edition, #groupName, #request)")
    public ResponseEntity<Void> leaveTeam(
            @PathVariable String courseName,
            @PathVariable String edition,
            @PathVariable String groupName,
            @PathVariable Integer teamId,
            HttpServletRequest request
    ) {
        var usosId = authUtils.getUsosId(request);
        teamsService.leaveTeam(teamId, usosId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/manual-reassignment")
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public ResponseEntity<Void> manualTeamAssign(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @SuppressWarnings("unused") @PathVariable String groupName,
            @RequestParam(name = "team-id", required = false) Integer teamId,
            @RequestParam(name = "previous-team-id", required = false) Integer previousTeamId,
            @RequestParam(name = "usos-id") Integer usosId
    ) {
        teamsService.manualTeamAssign(usosId, teamId, previousTeamId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{teamId}/members")
    @PreAuthorize("@authUtils.hasAccessToCourseEditionGroup(#courseName, #edition, #groupName, #request)")
    public List<UserDto> getTeamMembers(
            @PathVariable String courseName,
            @PathVariable String edition,
            @PathVariable String groupName,
            @PathVariable Integer teamId,
            HttpServletRequest request
    ) {
        return teamsService.getTeamMembers(teamId)
                .stream()
                .map(c -> modelMapper.map(c, UserDto.class))
                .toList();
    }

    @PutMapping("/{teamId}/project-ratings")
    @PreAuthorize("@authUtils.hasAccessToTeam(#teamId, #request)")
    public ProjectPreferenceDto rateProject(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @SuppressWarnings("unused") @PathVariable String groupName,
            @PathVariable Integer teamId,
            @RequestParam(name = "project-id") Integer projectId,
            @RequestParam Integer rating,
            HttpServletRequest request
    ) {
        var preference = teamsService.rateProject(teamId, projectId, rating);
        return modelMapper.map(preference, ProjectPreferenceDto.class);
    }

    @PutMapping("/{teamId}/assigned-project")
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public TeamDto assignProjectToTeam(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @SuppressWarnings("unused") @PathVariable String groupName,
            @PathVariable Integer teamId,
            @RequestParam(name = "project-id", required = false) Integer projectId
    ) {
        var team = teamsService.assignProject(teamId, projectId);
        return modelMapper.map(team, TeamDto.class);
    }

    @GetMapping("/{teamId}/project-ratings")
    @PreAuthorize("@authUtils.hasAccessToCourseEditionGroup(#courseName, #edition, #groupName, #request)")
    public List<ProjectPreferenceDto> getRatings(
            @PathVariable String courseName,
            @PathVariable String edition,
            @PathVariable String groupName,
            @PathVariable Integer teamId,
            HttpServletRequest request
    ) {
        return teamsService.getRatings(teamId)
                .stream()
                .map(c -> modelMapper.map(c, ProjectPreferenceDto.class))
                .toList();
    }

    // TODO: Write tests for this endpoint
    @GetMapping("/{teamId}/project-ratings/view")
    @PreAuthorize("@authUtils.hasAccessToCourseEditionGroup(#courseName, #edition, #groupName, #request)")
    public List<ProjectPreferenceDto> getRatingsView(
            @PathVariable String courseName,
            @PathVariable String edition,
            @PathVariable String groupName,
            @PathVariable Integer teamId,
            HttpServletRequest request
    ) {
        return teamsService.getRatingsView(courseName, edition, groupName, teamId)
                .stream()
                .map(c -> modelMapper.map(c, ProjectPreferenceDto.class))
                .toList();
    }

    @GetMapping
    public List<TeamDto> getTeams(
            @PathVariable String courseName,
            @PathVariable String edition,
            @PathVariable String groupName
    ) {
        return teamsService.getAll(courseName, edition, groupName)
                .stream()
                .map(c -> modelMapper.map(c, TeamDto.class))
                .toList();
    }

    @GetMapping("/{teamId}")
    public TeamDetailedWithAccessDto getTeam(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @SuppressWarnings("unused") @PathVariable String groupName,
            @PathVariable Integer teamId,
            HttpServletRequest request
    ) {
        var teamDto = modelMapper.map(teamsService.get(teamId), TeamDetailedWithAccessDto.class);
        var hasAccess = false;
        try {
            hasAccess = authUtils.hasAccessToTeam(teamId, request);
        } catch (RuntimeException ignored) {
        }
        teamDto.setReadonly(!hasAccess);
        return teamDto;
    }

    @DeleteMapping("/{teamId}")
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public ResponseEntity<Void> deleteTeam(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @SuppressWarnings("unused") @PathVariable String groupName,
            @PathVariable Integer teamId
    ) {
        teamsService.remove(teamId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/assigned-team")
    public TeamDetailedDto getAssignedTeam(
            @PathVariable String courseName,
            @PathVariable String edition,
            @PathVariable String groupName,
            HttpServletRequest request
    ) {
        var usosId = authUtils.getUsosId(request);
        return teamsService.getAssignedTeam(courseName, edition, groupName, usosId)
                .map(team -> modelMapper.map(team, TeamDetailedDto.class))
                .orElse(new TeamDetailedDto());
    }

    @PutMapping("/{teamId}/assignment-final")
    public TeamDetailedWithAccessDto setIsAssignmentFinal(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @SuppressWarnings("unused") @PathVariable String groupName,
            @PathVariable Integer teamId,
            @RequestParam(name = "is-final") Boolean isFinal
    ) {
        return modelMapper.map(teamsService.setIsAssignmentFinal(teamId, isFinal), TeamDetailedWithAccessDto.class);
    }

}
