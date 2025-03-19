package me.rahul.thoughts.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.rahul.thoughts.auth.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import static me.rahul.thoughts.constants.AppConstants.API_PATH;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(API_PATH + "/auth")
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        service.registerUser(registerRequest);
    }

    @PostMapping("/login")
    public LoginResponse authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {
        return service.authenticateUser(loginRequest);
    }

    @PostMapping("/access-token")
    public AccessTokenResponse getAccessToken(@RequestBody @Valid AccessTokenRequest accessTokenRequest) {
        return service.generateAccessToken(accessTokenRequest);
    }
}