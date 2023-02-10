package com.anczykowski.assigner.auth;

import java.io.IOException;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.MapSessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

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
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.emptyList()
                    )
                );
            }
        }
        filterChain.doFilter(request, response);
    }
}
