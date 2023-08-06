package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.teams.models.Team;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TeamsService {

    TeamsRepository teamsRepository;

    public Team create(Team team) {
        return teamsRepository.save(team);
    }
}
