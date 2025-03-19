package me.rahul.thoughts.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-integrationtest.yml")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void user_NewUserCreated_ShouldBeFoundById() {
        User user = User.builder()
                .email("me@example.com")
                .role(Role.USER)
                .username("username")
                .password("password")
                .build();

        user = userRepository.save(user);

        final User userById = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(userById);
        assertEquals(user.getId(), userById.getId());
    }

    @Test
    void findByUsernameOrEmail_UserNotPresent_ReturnEmptyOptional() {
        String testEmail = "test@email.com";
        Optional<User> userByEmail = userRepository.findByUsernameOrEmail(testEmail);
        assertFalse(userByEmail.isPresent());

        String testUsername = "test_username";
        Optional<User> userByUsername = userRepository.findByUsernameOrEmail(testUsername);
        assertFalse(userByUsername.isPresent());
    }

    @Test
    void findByUsernameOrEmail_ValidUsernameOrEmail_ShouldBeFound() {
        User user = User.builder()
                .email("me@example.com")
                .role(Role.USER)
                .username("username")
                .password("password")
                .build();

        user = userRepository.save(user);

        User userByEmail = userRepository.findByUsernameOrEmail(user.getEmail()).orElse(null);
        assertNotNull(userByEmail);
        assertEquals(user.getId(), userByEmail.getId());

        User userByUsername = userRepository.findByUsernameOrEmail(user.getUsername()).orElse(null);
        assertNotNull(userByUsername);
        assertEquals(user.getId(), userByUsername.getId());
    }
}
