package com.learn.controller;

import com.learn.security.dto.JwtAuthenticationResponse;
import com.learn.security.dto.SignUpRequest;
import com.learn.security.dto.SigninRequest;
import com.learn.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signup(
            @RequestBody @Valid SignUpRequest request) {

        return ResponseEntity.ok(authenticationService.signup(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(
            @RequestBody @Valid SigninRequest request) {
        JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.signin(request);

        // TODO : Can trigger Logout for other Sessions
        return ResponseEntity.ok(jwtAuthenticationResponse);
    }
}
