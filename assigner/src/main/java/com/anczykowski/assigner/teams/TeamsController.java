package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.teams.dto.TeamDto;
import com.anczykowski.assigner.teams.models.Team;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TeamsController {

    ModelMapper modelMapper;

    TeamsService teamsService;

    @PostMapping("/teams")
    public TeamDto newTeam(
            @Valid @RequestBody final TeamDto teamDto
    ) {
        var team = modelMapper.map(teamDto, Team.class);
        var createdTeam = teamsService.create(team);
        return modelMapper.map(createdTeam, TeamDto.class);
    }
}
