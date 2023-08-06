package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.teams.dto.TeamDto;
import com.anczykowski.assigner.teams.models.Team;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

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
}
