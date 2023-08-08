package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.teams.dto.TeamAccessTokenDto;
import com.anczykowski.assigner.teams.dto.TeamDto;
import com.anczykowski.assigner.teams.models.Team;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/courses/{courseName}/editions/{edition}/teams")
public class TeamsController {

    ModelMapper modelMapper;

    TeamsService teamsService;

    @PostMapping
    public TeamDto newTeam(
            @PathVariable String courseName,
            @PathVariable String edition,
            @Valid @RequestBody final TeamDto teamDto
    ) {
        var team = modelMapper.map(teamDto, Team.class);
        var createdTeam = teamsService.create(courseName, edition, team);
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
}
