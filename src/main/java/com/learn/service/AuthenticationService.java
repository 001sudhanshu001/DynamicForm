package com.learn.service;

import com.github.f4b6a3.ulid.Ulid;
import com.learn.entity.AppUser;
import com.learn.entity.UserSession;
import com.learn.enums.Role;
import com.learn.enums.TokenType;
import com.learn.properties.JwtTokenProperty;
import com.learn.repository.UserRepository;
import com.learn.repository.UserSessionDetailRepository;
import com.learn.security.dto.JwtAuthenticationResponse;
import com.learn.security.dto.SignUpRequest;
import com.learn.security.dto.SigninRequest;
import com.learn.security.dto.UserDetailsImpl;
import com.learn.security.exception.JwtSecurityException;
import com.learn.security.helper.SessionCreationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserSessionDetailRepository userSessionDetailRepository;
    private final AuthenticationManager authenticationManager;
    private final SessionCreationHelper sessionCreationHelper;

    @Transactional
    public JwtAuthenticationResponse signup(SignUpRequest signUpRequest) {
        AppUser appUser = AppUser.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(appUser);

        Map<TokenType, String> tokenMappedByType = jwtService.generateBothToken(new UserDetailsImpl(appUser));

        saveLoginSession(tokenMappedByType, appUser);

        return JwtAuthenticationResponse.builder()
                .accessToken(tokenMappedByType.get(TokenType.ACCESS_TOKEN))
                .refreshToken(tokenMappedByType.get(TokenType.REFRESH_TOKEN))
                .build();
    }


    private void saveLoginSession(Map<TokenType, String> tokenMappedByType, AppUser user) {
        String accessToken = tokenMappedByType.get(TokenType.ACCESS_TOKEN);
        String refreshToken = tokenMappedByType.get(TokenType.REFRESH_TOKEN);

        Date refreshTokenWillExpireAt = new Date(Ulid.from(refreshToken).getTime());

        UserSession sessionDetail = new UserSession();

        sessionDetail.setActiveRefreshToken(refreshToken);
        sessionDetail.setAppUser(user);
        sessionDetail.setRefreshTokenExpiryDate(refreshTokenWillExpireAt);
        sessionDetail.setActiveAccessToken(accessToken);

        Date date = new Date();
        sessionDetail.setCreatedDate(date);
        sessionDetail.setLastModifiedDate(date);

        userSessionDetailRepository.save(sessionDetail);
    }

    @Transactional
    public JwtAuthenticationResponse signin(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
        );

        AppUser user = userRepository.findByEmail(request.getUserName())
                .orElseThrow(() -> new JwtSecurityException(
                        JwtSecurityException.JWTErrorCode.USER_NOT_FOUND,
                        "Invalid email or password")
                );

        List<UserSession> logoutSessions = validateAndReturnLogoutSession(user);

        Map<TokenType, String> tokenMappedByType = jwtService.generateBothToken(new UserDetailsImpl(user));
        saveLoginSession(tokenMappedByType, user);

        return JwtAuthenticationResponse.builder()
                .accessToken(tokenMappedByType.get(TokenType.ACCESS_TOKEN))
                .refreshToken(tokenMappedByType.get(TokenType.REFRESH_TOKEN))
                .loggedOutSessions(logoutSessions) // This is added to Trigger logout session in Controller
                .userName(request.getUserName())
                .build();
    }

    private List<UserSession> validateAndReturnLogoutSession(AppUser user) {
        List<UserSession> sessions = user.getUserSessions();
        if (sessions.isEmpty()) return List.of();

        int numberOfSessionsInDB = sessions.size();

        // methods calling order matters here
        removeInActiveSessionFromDB(sessions);
        throwExceptionIfNewSessionNotAllowed(numberOfSessionsInDB);
        return removeSessionIfAllowed(numberOfSessionsInDB, sessions);
    }

    private void removeInActiveSessionFromDB(List<UserSession> sessions) {
        List<UserSession> inActiveUserSessions = sessions.stream()
                .filter(UserSession::hasRefreshDateCrossed)
                .filter(this::isAccessTokenExpired)
                .toList();

        if (inActiveUserSessions.isEmpty()) {
            return;
        }

        // Delete InActive Sessions From Database
        userSessionDetailRepository.deleteAll(inActiveUserSessions);

        // Remove From Session List
        sessions.removeAll(inActiveUserSessions);
    }

    private boolean isAccessTokenExpired(UserSession session) {
        String accessToken = session.getActiveAccessToken();
        Date tokenExpiryDate = jwtService.getTokenExpiryFromExpiredJWT(accessToken);
        return tokenExpiryDate.before(new Date());
    }

    private void throwExceptionIfNewSessionNotAllowed(int numberOfSessionsInDB) {
        if (!sessionCreationHelper.canCreateNewSession(numberOfSessionsInDB)) {
            throw new JwtSecurityException(
                    JwtSecurityException.JWTErrorCode.MAX_SESSION_REACHED,
                    "Session Not Allowed, You Have To Logout From Other Device First"
            );
        }
    }

    private List<UserSession> removeSessionIfAllowed(
            int numberOfSessionsInDB, List<UserSession> sessions) {
        Integer allowedSessionCount = JwtTokenProperty.ALLOWED_SESSION_COUNT;
        if (sessionCreationHelper.doWeNeedToRemoveOldSession(numberOfSessionsInDB)) {
            sessions.sort(Comparator.comparing(UserSession::getCreatedDate));

            int sessionListSizeShouldBeForCreatingNewOne = allowedSessionCount - 1;

            List<UserSession> deletableSessions = new ArrayList<>();
            while (sessions.size() != sessionListSizeShouldBeForCreatingNewOne) {
                deletableSessions.add(sessions.remove(0));
            }
            // TODO : Even after deleting the session the User will be allowed to access Resource
            //  for token expiration duration. This is something we have to bear as per the business needs,
            //  Because for each request we can't validate the Token from the DB
            userSessionDetailRepository.deleteAllInBatch(deletableSessions);
            return deletableSessions;
        }

        return List.of();
    }
}
