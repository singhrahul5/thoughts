package me.rahul.thoughts.auth;

import lombok.RequiredArgsConstructor;
import me.rahul.thoughts.auth.dto.*;
import me.rahul.thoughts.config.EmailDispatcherService;
import me.rahul.thoughts.user.Role;
import me.rahul.thoughts.user.User;
import me.rahul.thoughts.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final JwtTokenService jwtTokenService;
    private final JwtAuthenticationProvider refreshJwtTokenAuthenticationProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailDispatcherService emailDispatcherService;

    public void registerUser(RegisterRequest registerRequest) {
        userRepository.save(User.builder()
                .username(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
                .email(registerRequest.email())
                .role(Role.USER)
                .build());
        emailDispatcherService.sendAccountCreationEmail(registerRequest.email(), registerRequest.username());
    }

    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken userPassAuth =
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        Authentication auth = authManager.authenticate(userPassAuth);

        return new LoginResponse(jwtTokenService.generateAccessJwtToken(auth),
                jwtTokenService.generateRefreshJwtToken(auth));
    }

    public AccessTokenResponse generateAccessToken(AccessTokenRequest accessTokenRequest) {
        BearerTokenAuthenticationToken bearerAuthToken =
                new BearerTokenAuthenticationToken(accessTokenRequest.refreshToken());

        Authentication auth = refreshJwtTokenAuthenticationProvider.authenticate(bearerAuthToken);

        if (!refreshTokenRepository.existsValidToken(accessTokenRequest.refreshToken())) {
            throw new InvalidBearerTokenException("The refresh token is invalid.");
        }
        return new AccessTokenResponse(jwtTokenService.generateAccessJwtToken(auth));
    }
}
