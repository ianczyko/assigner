package com.anczykowski.assigner.auth;

import com.anczykowski.assigner.users.models.UserType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.MapSessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class SessionFilter extends OncePerRequestFilter {

    final MapSessionRepository sessionRepository;

    @Value("${disable.auth:false}")
    private Boolean disableAuth;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        var requestURI = request.getRequestURI();
        var contextPath = request.getContextPath();
        var endpoint = requestURI.substring(contextPath.length());
        if (endpoint.equals("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        var cookie = WebUtils.getCookie(request, "SESSION");
        if (cookie == null) {
            if(disableAuth){
                var authorities = new ArrayList<GrantedAuthority>();
                authorities.add(new SimpleGrantedAuthority(UserType.COORDINATOR.toString()));
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                123456,
                                null,
                                authorities
                        )
                );
                filterChain.doFilter(request, response);
            }
            else {
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

                String userType = session.getAttribute("userType");
                response.addHeader("user-type", userType);

                // TODO: make this cleaner (maybe just store userType as int in the first place?)
                var userTypeString = UserType.values()[Integer.parseInt(userType)].toString();

                var authorities = new ArrayList<GrantedAuthority>();
                authorities.add(new SimpleGrantedAuthority(userTypeString));

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                authorities
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
