package me.rahul.thoughts.auth;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.rahul.thoughts.user.User;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private Instant expirationTime;

    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Builder
    public RefreshToken(User user, String token, Instant expirationTime) {
        this.user = user;
        this.token = token;
        this.expirationTime = expirationTime;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        revoked = false;
    }
}

