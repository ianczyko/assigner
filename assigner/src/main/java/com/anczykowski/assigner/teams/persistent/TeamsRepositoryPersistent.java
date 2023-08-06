package com.anczykowski.assigner.teams.persistent;

import com.anczykowski.assigner.teams.TeamsRepository;
import com.anczykowski.assigner.teams.models.Team;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@AllArgsConstructor
public class TeamsRepositoryPersistent implements TeamsRepository {

    TeamsRepositoryPersistentImpl repositoryImpl;

    ModelMapper modelMapper;

    @Override
    @Transactional
    public Team save(Team team) {
        var teamPersistent = modelMapper.map(team, TeamPersistent.class);
        var teamPersistentSaved = repositoryImpl.save(teamPersistent);
        return modelMapper.map(teamPersistentSaved, Team.class);
    }
}

@Component
interface TeamsRepositoryPersistentImpl extends JpaRepository<TeamPersistent, Integer> {
}