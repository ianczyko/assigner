package com.anczykowski.assigner.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.MapSessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;

@Component
@AllArgsConstructor
public class SessionFilter extends OncePerRequestFilter {

    MapSessionRepository sessionRepository;

    @Override
    protected void doFilterInternal(
        @NotNull HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        var cookie = WebUtils.getCookie(request, "SESSION");
        if (cookie == null) {
            filterChain.doFilter(request, response);
            return;
        }
        var session = sessionRepository.findById(cookie.getValue());
        if (session != null) {
            String userId = session.getAttribute("accessToken");
            if (userId != null) {
                session.setLastAccessedTime(Instant.now());
                sessionRepository.save(session);
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.emptyList()
                    )
                );
            }
        } else {
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        filterChain.doFilter(request, response);
    }
}
