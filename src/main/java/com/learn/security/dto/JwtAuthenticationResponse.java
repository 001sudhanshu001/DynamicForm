package com.learn.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learn.entity.UserSession;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String accessToken;
    private String refreshToken;

    @JsonIgnore
    private List<UserSession> loggedOutSessions = new ArrayList<>();

    @JsonIgnore
    private String userName;
}
