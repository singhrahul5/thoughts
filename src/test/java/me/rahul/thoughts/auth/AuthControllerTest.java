package me.rahul.thoughts.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rahul.thoughts.auth.dto.*;
import me.rahul.thoughts.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService service;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void givenRegisterRequest_whenValidRequestBody_thenRegisterUser() throws Exception {
        RegisterRequest request = new RegisterRequest("testusername", "test@example.com", "validpassword");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(service).registerUser(any(RegisterRequest.class));
    }

    @Test
    void givenRegisterRequest_whenInvalidUsername_thenStatusShouldBeBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("0_invalid_username", "test@example.com", "validpassword");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(service, never()).registerUser(any(RegisterRequest.class));
    }

    @Test
    void givenRegisterRequest_whenInvalidEmail_thenStatusShouldBeBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("testusername", "invalid-email", "validpassword");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(service, never()).registerUser(any(RegisterRequest.class));
    }

    @Test
    void givenRegisterRequest_whenInvalidPassword_thenStatusShouldBeBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("testusername", "test@example.com", "invalid");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(service, never()).registerUser(any(RegisterRequest.class));
    }

    @Test
    void givenLoginRequest_whenValidUser_thenAuthenticateUserAndReturnAccessAndRefreshToken() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        LoginResponse response = new LoginResponse("mock-access-token", "mock-refresh-token");

        when(service.authenticateUser(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"));

        verify(service).authenticateUser(any(LoginRequest.class));
    }

    @Test
    void givenLoginRequest_whenInvalidEmail_thenStatusShouldBeBadRequest() throws Exception {
        LoginRequest request = new LoginRequest("invalid-email", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(service, never()).authenticateUser(any());
    }

    @Test
    void givenLoginRequest_whenInvalidPassword_thenStatusShouldBeBadRequest() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "invalid");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(service, never()).authenticateUser(any());
    }

    @Test
    void givenAccessTokenRequest_whenValidRefreshToken_thenGenerateAccessToken() throws Exception {
        AccessTokenRequest request = new AccessTokenRequest("refresh-token");
        AccessTokenResponse response = new AccessTokenResponse("new-access-token");

        when(service.generateAccessToken(any(AccessTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"));
    }
}

