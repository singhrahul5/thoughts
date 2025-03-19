package me.rahul.thoughts.auth;

import me.rahul.thoughts.user.Role;
import me.rahul.thoughts.user.User;
import me.rahul.thoughts.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-integrationtest.yml")
public class RefreshTokenRepositoryTest {
    final static String JWT_REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9" +
            ".eyJpc3MiOiJzZWxmIiwic3ViIjoicmFodWwiLCJ0eXAiOiJSZWZyZXNoIiwiZXhwIjoxNzM4NDM3NTQ2LCJpYXQiOjE3Mzg0MzI0ODgsInNjb3BlIjoiVVNFUiJ9.VZgdDnl7zr5IKWvhxN-wcEBXXkFmCId93VHsmBCYpew";

    @Autowired
    private RefreshTokenRepository repository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void saveUser() {
        user = userRepository.save(User.builder()
                .email("me@example.com")
                .role(Role.USER)
                .username("username")
                .password("password")
                .build());
    }

    @Test
    void refreshToken_StoredInDB_CanBeFoundByRefreshToken() {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(JWT_REFRESH_TOKEN)
                .user(user)
                .expirationTime(Instant.now().plus(30, ChronoUnit.MINUTES))
                .build();

        repository.save(refreshToken);


        boolean found = repository.existsValidToken(JWT_REFRESH_TOKEN);
        assertTrue(found);
    }


    @Test
    void refreshToken_TokenExpired_ExistsValidTokenReturnsFalse() {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(JWT_REFRESH_TOKEN)
                .user(user)
                .expirationTime(Instant.now().minus(30, ChronoUnit.MINUTES))
                .build();

        repository.save(refreshToken);

        boolean found = repository.existsValidToken(JWT_REFRESH_TOKEN);
        assertFalse(found);
    }
}
