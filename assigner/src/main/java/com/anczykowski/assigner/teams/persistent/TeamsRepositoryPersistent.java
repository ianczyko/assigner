package com.anczykowski.assigner.teams.persistent;

import com.anczykowski.assigner.teams.TeamsRepository;
import com.anczykowski.assigner.teams.models.Team;
import com.anczykowski.assigner.teams.models.projections.TeamFlat;
import com.anczykowski.assigner.teams.persistent.projections.TeamPersistentFlat;
import com.anczykowski.assigner.users.UsersRepository;
import com.anczykowski.assigner.users.models.User;
import com.anczykowski.assigner.users.models.projections.UserFlat;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class TeamsRepositoryPersistent implements TeamsRepository {

    TeamsRepositoryPersistentImpl repositoryImpl;

    UsersRepository usersRepository;

    ModelMapper modelMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Team save(Team team) {
        var teamPersistent = modelMapper.map(team, TeamPersistent.class);
        var teamPersistentSaved = repositoryImpl.save(teamPersistent);
        return modelMapper.map(teamPersistentSaved, Team.class);
    }

    @Override
    public List<Team> getAll(Integer courseEditionId) {
        return repositoryImpl.findByCourseEditionGroup_IdOrderById(courseEditionId)
                .stream()
                .map(c -> modelMapper.map(c, Team.class))
                .toList();
    }

    @Override
    public List<TeamFlat> getAllFlat(Integer courseEditionId) {
        return repositoryImpl.findFlatByCourseEditionGroup_IdOrderById(courseEditionId)
                .stream()
                .map(c -> TeamFlat.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .members(c.getMembers().stream().map(cm -> UserFlat.builder()
                                .id(cm.getId())
                                .name(cm.getName())
                                .secondName(cm.getSecondName())
                                .surname(cm.getSurname())
                                .build()
                        ).collect(Collectors.toSet()))
                        .build()
                )
                .toList();
    }

    @Override
    public Team get(Integer teamId) {
        return modelMapper.map(repositoryImpl.getReferenceById(teamId), Team.class);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Team addMemberToTeam(Integer teamId, User user) {
        var teamPersistent = repositoryImpl.getReferenceById(teamId);
        var userPersistent = usersRepository.getUserReferenceById(user.getId());
        teamPersistent.getMembers().add(userPersistent);
        var teamPersistentSaved = repositoryImpl.save(teamPersistent);
        return modelMapper.map(teamPersistentSaved, Team.class);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Team removeMemberFromTeam(Integer teamId, User user) {
        var teamPersistent = repositoryImpl.getReferenceById(teamId);
        var userPersistent = usersRepository.getUserReferenceById(user.getId());
        teamPersistent.getMembers().remove(userPersistent);
        var teamPersistentSaved = repositoryImpl.save(teamPersistent);
        return modelMapper.map(teamPersistentSaved, Team.class);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void remove(Integer teamId) {
        repositoryImpl.deleteById(teamId);
    }
}

@Component
interface TeamsRepositoryPersistentImpl extends JpaRepository<TeamPersistent, Integer> {
    List<TeamPersistent> findByCourseEditionGroup_IdOrderById(Integer courseEditionId);

    List<TeamPersistentFlat> findFlatByCourseEditionGroup_IdOrderById(Integer courseEditionId);
}