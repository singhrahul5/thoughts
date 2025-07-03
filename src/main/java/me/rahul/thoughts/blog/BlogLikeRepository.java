package me.rahul.thoughts.blog;

import me.rahul.thoughts.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogLikeRepository extends JpaRepository<BlogLike, Long> {

    Optional<BlogLike> findByUserAndBlog(User user, Blog blog);
}