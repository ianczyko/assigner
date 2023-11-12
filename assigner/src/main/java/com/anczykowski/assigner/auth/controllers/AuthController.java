package com.anczykowski.assigner.auth.controllers;

import com.anczykowski.assigner.auth.dto.AuthRequest;
import com.anczykowski.assigner.auth.dto.AuthResponse;
import com.anczykowski.assigner.auth.dto.ProfileResponseDto;
import com.anczykowski.assigner.auth.dto.VerifyRequest;
import com.anczykowski.assigner.auth.services.AuthService;
import com.anczykowski.assigner.error.ForbiddenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

@RestController
@RequiredArgsConstructor
public class AuthController {

    final ModelMapper modelMapper;

    final AuthService authService;

    @PostMapping("/auth")
    public AuthResponse auth(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        var session = authService.createSession();
        var authorizeUrl = authService.getToken(authRequest.getCallbackUrl(), session.getId());
        var cookie = new Cookie("SESSION", session.getId());
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "Strict");
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
            throw new ForbiddenException();
        }
        authService.verify(verifyRequest.getVerifier(), cookie.getValue());
        return ResponseEntity.ok().build();
    }


    @GetMapping("/profile")
    public ProfileResponseDto profile(HttpServletRequest request) {
        var cookie = WebUtils.getCookie(request, "SESSION");
        if (cookie == null) {
            throw new ForbiddenException();
        }
        return modelMapper.map(authService.userData(cookie.getValue()), ProfileResponseDto.class);
    }
}
