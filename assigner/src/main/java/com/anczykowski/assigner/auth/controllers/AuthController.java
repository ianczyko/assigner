package com.anczykowski.assigner.auth.controllers;

import com.anczykowski.assigner.auth.dto.AuthRequest;
import com.anczykowski.assigner.auth.dto.AuthResponse;
import com.anczykowski.assigner.auth.dto.ProfileResponse;
import com.anczykowski.assigner.auth.dto.VerifyRequest;
import com.anczykowski.assigner.auth.services.AuthService;
import com.anczykowski.assigner.error.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

@RestController
@AllArgsConstructor
public class AuthController {

    AuthService authService;

    @PostMapping("/auth")
    public AuthResponse auth(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        var authorizeUrl = authService.getToken(authRequest.getCallbackUrl());
        var session = authService.createSession();
        var cookie = new Cookie("SESSION", session.getId());
        cookie.setHttpOnly(true);
        var minute = 60;
        var hour = 60 * minute;
        cookie.setMaxAge(6 * hour);
        response.addCookie(cookie);
        return new AuthResponse(authorizeUrl);
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@RequestBody VerifyRequest verifyRequest, HttpServletRequest request) {
        var cookie = WebUtils.getCookie(request, "SESSION");
        if (cookie == null) {
            throw new UnauthorizedException();
        }
        authService.verify(verifyRequest.getVerifier(), cookie.getValue());
        return ResponseEntity.ok().build();
    }


    @GetMapping("/profile")
    public ProfileResponse profile(HttpServletRequest request) {
        var cookie = WebUtils.getCookie(request, "SESSION");
        if (cookie == null) {
            throw new UnauthorizedException();
        }
        // TODO: service should not return response, domain to dto mapping should happen here
        return authService.userData(cookie.getValue());
    }
}
