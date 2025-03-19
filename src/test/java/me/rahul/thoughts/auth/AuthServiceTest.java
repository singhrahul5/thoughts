package me.rahul.thoughts.auth;

import me.rahul.thoughts.auth.dto.*;
import me.rahul.thoughts.config.EmailDispatcherService;
import me.rahul.thoughts.user.User;
import me.rahul.thoughts.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private JwtAuthenticationProvider refreshJwtTokenAuthenticationProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailDispatcherService emailDispatcherService;

    @InjectMocks
    private AuthService authService;


    @Test
    void registerUser_ShouldSaveUser() {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "test@example.com", "password");

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        authService.registerUser(registerRequest);

        verify(userRepository).save(any(User.class));
        verify(emailDispatcherService).sendAccountCreationEmail("test@example.com", "testUser");
    }

    @Test
    void authenticateUser_ShouldReturnTokens() {
        LoginRequest loginRequest = new LoginRequest("testUser", "password");
        Authentication authentication = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenService.generateAccessJwtToken(any())).thenReturn("accessToken");
        when(jwtTokenService.generateRefreshJwtToken(any())).thenReturn("refreshToken");

        LoginResponse response = authService.authenticateUser(loginRequest);

        assertNotNull(response);
        assertEquals("accessToken", response.accessToken());
        assertEquals("refreshToken", response.refreshToken());
    }

    @Test
    void generateAccessToken_ValidRefreshToken_ReturnAccessToken() {
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest("validRefreshToken");
        Authentication authentication = mock(Authentication.class);
        when(refreshJwtTokenAuthenticationProvider.authenticate(any(BearerTokenAuthenticationToken.class)))
                .thenReturn(authentication);
        when(refreshTokenRepository.existsValidToken("validRefreshToken")).thenReturn(true);
        when(jwtTokenService.generateAccessJwtToken(any())).thenReturn("newAccessToken");

        AccessTokenResponse response = authService.generateAccessToken(accessTokenRequest);

        assertNotNull(response);
        assertEquals("newAccessToken", response.accessToken());
    }

    @Test
    void generateAccessToken_InvalidToken_ThrowException() {
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest("invalidRefreshToken");
        when(refreshTokenRepository.existsValidToken("invalidRefreshToken")).thenReturn(false);

        assertThrows(InvalidBearerTokenException.class, () -> authService.generateAccessToken(accessTokenRequest));
    }
}
