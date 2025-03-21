package me.rahul.thoughts.user;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "following",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_id"}))
public class Following {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;

    @Builder
    public Following(User follower, User followed) {
        this.follower = follower;
        this.followed = followed;
    }
}