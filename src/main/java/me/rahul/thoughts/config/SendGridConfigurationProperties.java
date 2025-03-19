package me.rahul.thoughts.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@AllArgsConstructor
@Getter
@Validated
@ConfigurationProperties(prefix = "app.sendgrid")
class SendGridConfigurationProperties {
    @NotBlank
    @Pattern(regexp = "^SG[0-9a-zA-Z._]{67}$")
    private String apiKey;

    @Email
    @NotBlank
    private String fromEmail;

    @NotBlank
    private String fromName;
}
