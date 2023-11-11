package com.anczykowski.assigner.auth.authutils;

import com.anczykowski.assigner.courses.repositories.CourseEditionGroupRepository;
import com.anczykowski.assigner.error.ForbiddenException;
import com.anczykowski.assigner.teams.TeamsRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    @Value("${disable.auth:false}")
    private Boolean disableAuth;

    private final MapSessionRepository sessionRepository;

    private final CourseEditionGroupRepository courseEditionGroupRepository;

    private final TeamsRepository teamsRepository;

    public boolean hasAccessToCourseEditionGroup(
            String courseName,
            String edition,
            String groupName,
            HttpServletRequest request
    ) {
        var usosId = getUsosId(request);
        if (usosId == null) return false;
        if (courseEditionGroupRepository.checkIfUserHasAccessToCourseEditionGroup(
                courseName,
                edition,
                groupName,
                usosId
        )) {
            return true;
        }
        if (SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream().map(Object::toString).toList().contains("COORDINATOR")
        ) {
            return true;
        }
        throw new ForbiddenException("User has no access to requested course edition");
    }

    public boolean hasAccessToTeam(Integer teamId, HttpServletRequest request) {
        var usosId = getUsosId(request);
        if (usosId == null) return false;
        var team = teamsRepository.get(teamId);
        if (team.getMembers().stream().anyMatch(m -> m.getUsosId().equals(usosId))) {
            return true;
        }
        throw new ForbiddenException("User has no access to requested team");
    }

    public Integer getUsosId(HttpServletRequest request) {
        if (disableAuth) return 12345678; // Test user
        var session = getSessionFromRequest(request);
        if (session == null) return null;
        return Integer.valueOf(session.getAttribute("usosId"));
    }

    @Nullable
    private MapSession getSessionFromRequest(HttpServletRequest request) {
        var cookie = WebUtils.getCookie(request, "SESSION");
        if (cookie == null) {
            return null;
        }
        return sessionRepository.findById(cookie.getValue());
    }
}
