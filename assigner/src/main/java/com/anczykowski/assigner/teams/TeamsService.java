package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.courses.services.CourseEditionsService;
import com.anczykowski.assigner.teams.models.Team;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TeamsService {

    TeamsRepository teamsRepository;

    CourseEditionsService courseEditionsService;

    public Team create(String courseName, String edition, Team team) {
        var courseEdition = courseEditionsService.get(courseName, edition);
        team.setCourseEdition(courseEdition);
        return teamsRepository.save(team);
    }
}
