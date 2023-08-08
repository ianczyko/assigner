package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.courses.services.CourseEditionsService;
import com.anczykowski.assigner.teams.models.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamsService {

    final TeamsRepository teamsRepository;

    final CourseEditionsService courseEditionsService;

    @Value("${token.digits:6}")
    int tokenDigits;

    @Value("${token.valid-days:2}")
    int tokenValidDays;

    public Team create(String courseName, String edition, Team team) {
        var courseEdition = courseEditionsService.get(courseName, edition);
        team.setCourseEdition(courseEdition);
        return teamsRepository.save(team);
    }

    public List<Team> getAll(String courseName, String edition) {
        var courseEdition = courseEditionsService.get(courseName, edition);
        return teamsRepository.getAll(courseEdition);
    }

    public Team generateAccessToken(Integer teamId) {
        var team = teamsRepository.get(teamId);
        team.regenerateAccessToken(tokenDigits, tokenValidDays);
        return teamsRepository.save(team);
    }

    public Team get(Integer teamId) {
        return teamsRepository.get(teamId);
    }
}
