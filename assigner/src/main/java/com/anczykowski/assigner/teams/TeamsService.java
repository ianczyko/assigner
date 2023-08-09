package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.courses.services.CourseEditionsService;
import com.anczykowski.assigner.teams.models.Team;
import com.anczykowski.assigner.users.UsersRepository;
import com.anczykowski.assigner.users.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamsService {

    final TeamsRepository teamsRepository;

    final CourseEditionsService courseEditionsService;

    final UsersRepository usersRepository;

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

    public boolean addMember(Integer teamId, Integer accessToken, Integer usosId) {
        var team = teamsRepository.get(teamId);
        if (team.getAccessToken().equals(accessToken)) {
            var member = usersRepository.getByUsosId(usosId);
            team.addMember(member);
            teamsRepository.save(team);
            return true;
        }
        return false;
    }

    public List<User> getTeamMembers(Integer teamId) {
        var team = teamsRepository.get(teamId);
        return team.getMembers();
    }
}
