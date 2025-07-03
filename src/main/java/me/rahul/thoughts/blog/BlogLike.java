package me.rahul.thoughts.blog;


import jakarta.persistence.*;
import lombok.*;
import me.rahul.thoughts.user.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "blog_likes")
public class BlogLike {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @Builder
    public BlogLike(User user, Blog blog) {
        this.user = user;
        this.blog = blog;
    }
}