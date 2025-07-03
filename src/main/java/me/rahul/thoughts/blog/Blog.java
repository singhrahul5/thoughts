package me.rahul.thoughts.blog;

import jakarta.persistence.*;
import lombok.*;
import me.rahul.thoughts.user.User;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "blogs")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Long likeCount;

    @Column(nullable = false)
    private Long commentCount;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private BlogType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private Blog parent;

    @Builder
    public Blog(User author, String content, BlogType type, Blog parent) {
        this.author = author;
        this.content = content;
        this.type = type;
        this.parent = parent;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        likeCount = 0L;
        commentCount = 0L;
    }
}
