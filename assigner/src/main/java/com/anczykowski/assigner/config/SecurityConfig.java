package com.anczykowski.assigner.config;

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
import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableSpringHttpSession
@AllArgsConstructor
public class SecurityConfig {
    SessionFilter sessionFilter;

    CustomLogoutHandler customLogoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var logoutSuccessHandler = new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK);
        http
            .cors().disable()
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers("/auth", "/verify").permitAll()
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
