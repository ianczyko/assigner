package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.teams.models.Team;
import com.anczykowski.assigner.teams.models.projections.TeamFlat;
import com.anczykowski.assigner.users.models.User;

import java.util.List;

public interface TeamsRepository {
    Team save(Team team);

    List<Team> getAll(Integer courseEditionId);

    List<TeamFlat> getAllFlat(Integer courseEditionId);

    Team get(Integer teamId);

    Team addMemberToTeam(Integer teamId, User user);

    @SuppressWarnings("UnusedReturnValue")
    Team removeMemberFromTeam(Integer teamId, User user);

    void remove(Integer teamId);
}
