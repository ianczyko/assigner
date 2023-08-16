package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.courses.services.CourseEditionsService;
import com.anczykowski.assigner.error.NotFoundException;
import com.anczykowski.assigner.error.UnauthorizedException;
import com.anczykowski.assigner.teams.models.Team;
import com.anczykowski.assigner.users.UsersRepository;
import com.anczykowski.assigner.users.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamsService {

    final TeamsRepository teamsRepository;

    final CourseEditionsService courseEditionsService;

    final UsersRepository usersRepository;

    @Value("${token.digits:6}")
    int tokenDigits;

    @Value("${token.valid-days:2}")
    int tokenValidDays;

    @Transactional
    public Team create(String courseName, String edition, Team team, Integer leaderUsosId) {
        var leader = usersRepository.getByUsosId(leaderUsosId)
                .orElseThrow(() -> new NotFoundException("user with usosId %d not found".formatted(leaderUsosId)));
        team.setLeader(leader);

        var courseEdition = courseEditionsService.get(courseName, edition);
        team.setCourseEdition(courseEdition);
        return teamsRepository.save(team);
    }

    public List<Team> getAll(String courseName, String edition) {
        var courseEdition = courseEditionsService.get(courseName, edition);
        return teamsRepository.getAll(courseEdition);
    }

    @Transactional
    public Team generateAccessToken(Integer teamId) {
        var team = teamsRepository.get(teamId);
        team.regenerateAccessToken(tokenDigits, tokenValidDays);
        return teamsRepository.save(team);
    }

    public Team get(Integer teamId) {
        return teamsRepository.get(teamId);
    }

    @Transactional
    public List<User> addMember(Integer teamId, Integer accessToken, Integer usosId) {
        var team = teamsRepository.get(teamId);
        if (!team.getAccessToken().equals(accessToken)) {
            throw new UnauthorizedException("Unmatched access token");
        }
        var member = usersRepository.getByUsosId(usosId)
                .orElseThrow(() -> new NotFoundException("user with usosId %d not found".formatted(usosId)));

        team.addMember(member);
        teamsRepository.save(team);
        return team.getMembers();
    }

    public List<User> getTeamMembers(Integer teamId) {
        var team = teamsRepository.get(teamId);
        return team.getMembers();
    }
}
