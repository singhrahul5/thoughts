package me.rahul.thoughts.config;

import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(SendGridConfigurationProperties.class)
class SendGridConfiguration {
    private final SendGridConfigurationProperties sendGridConfigurationProperties;

    @Bean
    public SendGrid sendGrid() {
        String apiKey = sendGridConfigurationProperties.getApiKey();
        return new SendGrid(apiKey);
    }

    @Bean
    public Email fromEmail() {
        String fromEmail = sendGridConfigurationProperties.getFromEmail();
        String fromName = sendGridConfigurationProperties.getFromName();
        return new Email(fromEmail, fromName);
    }
}
