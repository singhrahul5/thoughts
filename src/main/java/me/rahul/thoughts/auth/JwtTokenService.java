package me.rahul.thoughts.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.rahul.thoughts.user.User;
import me.rahul.thoughts.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public String generateAccessJwtToken(Authentication auth) {
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        Instant now = Instant.now();

        String scope = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(auth.getName())
                .issuedAt(now)
                .expiresAt(now.plus(5, ChronoUnit.MINUTES))
                .issuer("self")
                .claim("typ", "Bearer")
                .claim("scope", scope)
                .build();

        log.debug("Authentication#getName() {}", auth.getName());
        log.debug("Scope {}", scope);
        log.debug("Authentication#getPrincipal() {}", auth.getPrincipal());
        log.debug("Authentication#getCredentials() {}", auth.getCredentials());
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet))
                .getTokenValue();
    }

    public String generateRefreshJwtToken(Authentication auth) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(30, ChronoUnit.DAYS);

        String scope = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(auth.getName())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .issuer("self")
                .claim("typ", "Refresh")
                .claim("scope", scope)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet))
                .getTokenValue();

        // save the refresh token with expiration time into database
        User user = userService.loadUserByUsername(auth.getName());

        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expirationTime(expiresAt)
                .build());

        return refreshToken;
    }
}
