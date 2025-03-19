package me.rahul.thoughts.auth;


import me.rahul.thoughts.user.User;
import me.rahul.thoughts.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceTest {

    @InjectMocks
    private JwtTokenService jwtTokenService;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        when(authentication.getAuthorities())
                .thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("USER")));

        System.out.println(authentication.getAuthorities());
        when(authentication.getName()).thenReturn("testUser");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(Jwt.withTokenValue("mock-jwt-token").header("alg", "HS256")
                        .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300)).build());
    }

    @Test
    void generateAccessJwtToken_ShouldReturnToken() {
        String token = jwtTokenService.generateAccessJwtToken(authentication);
        assertNotNull(token);
    }

    @Test
    void generateRefreshJwtToken_ShouldReturnTokenAndSaveToRepository() {
        User mockUser = new User();
        when(userService.loadUserByUsername("testUser")).thenReturn(mockUser);

        String refreshToken = jwtTokenService.generateRefreshJwtToken(authentication);

        assertNotNull(refreshToken);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        verify(userService).loadUserByUsername(any(String.class));
    }
}
