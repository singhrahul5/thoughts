package me.rahul.thoughts.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Getter
@Slf4j
@Validated
@ConfigurationProperties(prefix = "app.security")
public class SecurityConfigurationProperties {
    private SecretKey secretKey;

    public void setSecretKey(@NotNull String secretKey) {
        log.debug("app.secret-key = {}", secretKey);
        this.secretKey = new SecretKeySpec(Base64.getDecoder().decode(secretKey), "HS256");
        log.debug("app.secret-key byte array length = {}", this.secretKey.getEncoded().length);
    }

}
