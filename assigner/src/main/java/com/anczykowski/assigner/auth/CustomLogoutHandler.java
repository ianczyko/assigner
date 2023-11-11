package com.anczykowski.assigner.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.session.MapSessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
@AllArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    MapSessionRepository sessionRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        var sessionCookie = WebUtils.getCookie(request, "SESSION");
        if (sessionCookie != null) {
            String sessionId = sessionCookie.getValue();
            sessionRepository.deleteById(sessionId);
        }
    }
}
