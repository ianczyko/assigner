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
            var requestURI = request.getRequestURI();
            var contextPath = request.getContextPath();
            var endpoint = requestURI.substring(contextPath.length());
            if (endpoint.equals("/auth")) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
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
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
