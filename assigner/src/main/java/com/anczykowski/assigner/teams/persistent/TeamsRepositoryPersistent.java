package com.anczykowski.assigner.teams.persistent;

import com.anczykowski.assigner.courses.models.CourseEdition;
import com.anczykowski.assigner.teams.TeamsRepository;
import com.anczykowski.assigner.teams.models.Team;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@AllArgsConstructor
public class TeamsRepositoryPersistent implements TeamsRepository {

    TeamsRepositoryPersistentImpl repositoryImpl;

    ModelMapper modelMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Team save(Team team) {
        var teamPersistent = modelMapper.map(team, TeamPersistent.class);
        var teamPersistentSaved = repositoryImpl.save(teamPersistent);
        return modelMapper.map(teamPersistentSaved, Team.class);
    }

    @Override
    public List<Team> getAll(CourseEdition courseEdition) {
        return repositoryImpl.findByCourseEdition_Id(courseEdition.getId())
                .stream()
                .map(c -> modelMapper.map(c, Team.class))
                .toList();
    }

    @Override
    public Team get(Integer teamId) {
        return modelMapper.map(repositoryImpl.getReferenceById(teamId), Team.class);
    }
}

@Component
interface TeamsRepositoryPersistentImpl extends JpaRepository<TeamPersistent, Integer> {
    List<TeamPersistent> findByCourseEdition_Id(Integer courseEditionId);
}