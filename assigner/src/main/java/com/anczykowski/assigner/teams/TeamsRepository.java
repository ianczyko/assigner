package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.courses.models.CourseEdition;
import com.anczykowski.assigner.teams.models.Team;

import java.util.List;

public interface TeamsRepository {
    Team save(Team team);

    List<Team> getAll(CourseEdition courseEdition);
}
