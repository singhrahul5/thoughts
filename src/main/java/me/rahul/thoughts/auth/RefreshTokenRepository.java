package me.rahul.thoughts.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Date;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query(value = """
            SELECT COUNT(r) > 0 FROM RefreshToken r
            WHERE r.token = :token AND r.expirationTime > CURRENT_TIMESTAMP AND r.revoked = false
            """)
    boolean existsValidToken(String token);
}