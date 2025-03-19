package me.rahul.thoughts.auth.dto;

import me.rahul.thoughts.validation.Password;
import me.rahul.thoughts.validation.UsernameOrEmail;

public record LoginRequest(
        @UsernameOrEmail String username,
        @Password String password
) {
}
