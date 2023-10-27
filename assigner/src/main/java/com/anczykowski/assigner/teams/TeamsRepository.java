package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.courses.models.CourseEditionGroup;
import com.anczykowski.assigner.teams.models.Team;

import java.util.List;

public interface TeamsRepository {
    Team save(Team team);

    List<Team> getAll(CourseEditionGroup courseEdition);

    Team get(Integer teamId);
}
