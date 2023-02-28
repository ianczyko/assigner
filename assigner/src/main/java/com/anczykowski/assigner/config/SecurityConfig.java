package com.anczykowski.assigner.config;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

import com.anczykowski.assigner.auth.CustomLogoutHandler;
import com.anczykowski.assigner.auth.SessionFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableSpringHttpSession
@RequiredArgsConstructor
public class SecurityConfig {
    final SessionFilter sessionFilter;

    final CustomLogoutHandler customLogoutHandler;

    @Value("${disable.auth:false}")
    private Boolean disableAuth;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var patternsToPermit = new ArrayList<>(Arrays.asList("/auth", "/verify"));
        if (disableAuth) patternsToPermit.add("/**");
        var patternsToPermitListArr = patternsToPermit.toArray(String[]::new);
        var logoutSuccessHandler = new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK);
        http
            .cors().disable()
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers(patternsToPermitListArr).permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(sessionFilter, BasicAuthenticationFilter.class)
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .logout().logoutUrl("/logout")
            .addLogoutHandler(customLogoutHandler)
            .logoutSuccessHandler(logoutSuccessHandler)
            .deleteCookies("SESSION", "JSESSIONID").invalidateHttpSession(true);
        return http.build();
    }
}
