package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.auth.authutils.AuthUtils;
import com.anczykowski.assigner.teams.dto.ProjectPreferenceDto;
import com.anczykowski.assigner.teams.dto.TeamAccessTokenDto;
import com.anczykowski.assigner.teams.dto.TeamDetailedDto;
import com.anczykowski.assigner.teams.dto.TeamDto;
import com.anczykowski.assigner.teams.models.Team;
import com.anczykowski.assigner.users.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/courses/{courseName}/editions/{edition}/teams")
public class TeamsController {

    ModelMapper modelMapper;

    TeamsService teamsService;

    AuthUtils authUtils;

    @PostMapping
    public TeamDto newTeam(
            @PathVariable String courseName,
            @PathVariable String edition,
            @Valid @RequestBody final TeamDto teamDto,
            HttpServletRequest request
    ) {
        var usosId = authUtils.getUsosId(request);
        var team = modelMapper.map(teamDto, Team.class);
        var createdTeam = teamsService.create(courseName, edition, team, usosId);
        return modelMapper.map(createdTeam, TeamDto.class);
    }

    @PutMapping("/{teamId}/access-token")
    public TeamAccessTokenDto generateAccessToken(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @PathVariable Integer teamId
    ) {
        var team = teamsService.generateAccessToken(teamId);
        return modelMapper.map(team, TeamAccessTokenDto.class);
    }

    @GetMapping("/{teamId}/access-token")
    public TeamAccessTokenDto getAccessToken(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @PathVariable Integer teamId
    ) {
        var team = teamsService.get(teamId);
        return modelMapper.map(team, TeamAccessTokenDto.class);
    }

    @PostMapping("/{teamId}/members")
    @PreAuthorize("@authUtils.hasAccessToCourseEdition(#courseName, #edition, #request)")
    public List<UserDto> addTeamMember(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
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

    @GetMapping("/{teamId}/members")
    @PreAuthorize("@authUtils.hasAccessToCourseEdition(#courseName, #edition, #request)")
    public List<UserDto> getTeamMembers(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
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
            @PathVariable Integer teamId,
            @RequestParam(name = "project-id") Integer projectId,
            @RequestParam Integer rating,
            HttpServletRequest request
    ) {
        var preference = teamsService.rateProject(teamId, projectId, rating);
        return modelMapper.map(preference, ProjectPreferenceDto.class);
    }

    @PutMapping("/{teamId}/assigned-project")
    @PreAuthorize("@authUtils.hasAccessToCourseEdition(#courseName, #edition, #request)")
    // TODO: PreAuth of coordinator (elevated access)
    public TeamDto assignProjectToTeam(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @PathVariable Integer teamId,
            @RequestParam(name = "project-id") Integer projectId,
            HttpServletRequest request
    ) {
        var team = teamsService.assignProject(teamId, projectId);
        return modelMapper.map(team, TeamDto.class);
    }

    @GetMapping("/{teamId}/project-ratings")
    @PreAuthorize("@authUtils.hasAccessToCourseEdition(#courseName, #edition, #request)")
    public List<ProjectPreferenceDto> getRatings(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @PathVariable Integer teamId,
            HttpServletRequest request
    ) {
        return teamsService.getRatings(teamId)
                .stream()
                .map(c -> modelMapper.map(c, ProjectPreferenceDto.class))
                .toList();
    }

    @GetMapping
    public List<TeamDto> getTeams(
            @PathVariable String courseName,
            @PathVariable String edition
    ) {
        return teamsService.getAll(courseName, edition)
                .stream()
                .map(c -> modelMapper.map(c, TeamDto.class))
                .toList();
    }

    @GetMapping("/{teamId}")
    public TeamDetailedDto getTeam(
            @SuppressWarnings("unused") @PathVariable String courseName,
            @SuppressWarnings("unused") @PathVariable String edition,
            @PathVariable Integer teamId
    ) {
        return modelMapper.map(teamsService.get(teamId), TeamDetailedDto.class);
    }
}
