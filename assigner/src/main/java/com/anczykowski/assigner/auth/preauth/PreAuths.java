package com.anczykowski.assigner.auth.preauth;

import com.anczykowski.assigner.courses.repositories.CoursesEditionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
@RequiredArgsConstructor
public class PreAuths {

    @Value("${disable.auth:false}")
    private Boolean disableAuth;

    private final MapSessionRepository sessionRepository;

    private final CoursesEditionRepository coursesEditionRepository;

    public boolean hasAccessToCourseEdition(String courseName, String edition, HttpServletRequest request) {
        if (disableAuth) return true;
        var usosId = getUsosId(request);
        if (usosId == null) return false;
        return coursesEditionRepository.checkIfUserHasAccessToCourseEdition(courseName, edition, usosId);
    }

    private Integer getUsosId(HttpServletRequest request) {
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
