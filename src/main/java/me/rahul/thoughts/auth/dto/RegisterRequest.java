package me.rahul.thoughts.auth.dto;

import jakarta.validation.constraints.Email;
import me.rahul.thoughts.validation.Password;
import me.rahul.thoughts.validation.Username;

public record RegisterRequest(
        @Username String username,
        @Email String email,
        @Password String password
) {
}
