package com.learn.controller;

import com.learn.security.dto.*;
import com.learn.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

        return ResponseEntity.ok(authenticationService.signup(request, false));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/create-admin")
    public ResponseEntity<JwtAuthenticationResponse> createAdmin(
            @RequestBody @Valid SignUpRequest request) {

        return ResponseEntity.ok(authenticationService.signup(request, true));
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(
            @RequestBody @Valid SigninRequest request) {
        JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.signin(request);

        // TODO : Can trigger Logout for other Session
        return ResponseEntity.ok(jwtAuthenticationResponse);
    }

    @PostMapping("/signin-exclusively")
    public ResponseEntity<JwtAuthenticationResponse> signInExclusively(
            @RequestBody @Valid SigninRequest request) {
        JwtAuthenticationResponse jwtAuthenticationResponse =
                authenticationService.signinExclusively(request);

        // TODO : Trigger Logout Event for others
        return ResponseEntity.ok(jwtAuthenticationResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(
            @RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authenticationService.refresh(refreshTokenRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody LogOutRequest logOutRequest) {
        String userName = authenticationService.logout(logOutRequest);

        return ResponseEntity.ok("User has successfully logged out from the system!");
    }
}
