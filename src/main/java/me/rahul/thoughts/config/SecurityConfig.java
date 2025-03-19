package me.rahul.thoughts.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.rahul.thoughts.user.Role;
import me.rahul.thoughts.user.UserService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@EnableAsync
@EnableMethodSecurity
@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(SecurityConfigurationProperties.class)
public class SecurityConfig {

    private final static String ROLE_PREFIX = "ROLE_";

    private final SecurityConfigurationProperties securityConfigProperties;

    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role(Role.ADMIN.name()).implies(Role.USER.name())
                .build();
    }

    // and, if using pre-post method security also add
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setDefaultRolePrefix(ROLE_PREFIX);
        log.debug("role prefix for user role {}", ROLE_PREFIX);
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix(ROLE_PREFIX);
        log.debug("Scope prefix for jwt token {}", ROLE_PREFIX);
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtAuthenticationConverter;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(securityConfigProperties.getSecretKey())
                .macAlgorithm(MacAlgorithm.HS256).build();


        // don't allow Refresh token
        jwtDecoder.setJwtValidator(jwt -> {
            String tokenType = jwt.getClaimAsString("typ");

            if (tokenType == null || !tokenType.equals("Bearer")) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.UNSUPPORTED_TOKEN_TYPE,
                        "The " + jwt + " is not of typ Bearer", null);
                log.debug("The {} is not of typ Bearer", jwt);
                return OAuth2TokenValidatorResult.failure(error);
            }

            return OAuth2TokenValidatorResult.success();
        });

        return jwtDecoder;
    }

    @Bean
    JwtEncoder jwtEncoder() {
        /*
        OctetSequenceKey jwk = new OctetSequenceKey.Builder(jwtSecretKey)
                .algorithm(JWSAlgorithm.HS256)
                .build();
        JWKSet jwkSet = new JWKSet(jwk);
        return new NimbusJwtEncoder((jwkSelector, securityContext) -> jwkSelector.select(jwkSet));
         */
        return new NimbusJwtEncoder(new ImmutableSecret<>(securityConfigProperties.getSecretKey()));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //to use AuthenticationManager
    @Bean
    ProviderManager providerManager(UserService userService, PasswordEncoder passwordEncoder, JwtDecoder jwtDecoder) {
        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider(passwordEncoder);
        daoAuthProvider.setUserDetailsService(userService);
//        JwtAuthenticationProvider jwtAuthProvider = new JwtAuthenticationProvider(jwtDecoder);
        return new ProviderManager(daoAuthProvider);
    }

    @Bean
    JwtAuthenticationProvider jwtAuthenticationProvider() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(securityConfigProperties.getSecretKey())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        // add expiration validation
        OAuth2TokenValidator<Jwt> withExpiry = new JwtTimestampValidator(); // Validates 'exp'

        OAuth2TokenValidator<Jwt> withType = jwt -> {
            String tokenType = jwt.getClaimAsString("typ");
            if (tokenType == null || !tokenType.equals("Refresh")) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.UNSUPPORTED_TOKEN_TYPE,
                        "The jwt is not of type Refresh", null);
                log.debug("The {} is not of typ Refresh", jwt);
                return OAuth2TokenValidatorResult.failure(error);
            }

            return OAuth2TokenValidatorResult.success();
        };

        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(List.of(withExpiry, withType));
        decoder.setJwtValidator(validator);
        return new JwtAuthenticationProvider(decoder);
    }
}
