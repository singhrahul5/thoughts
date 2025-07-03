package me.rahul.thoughts.blog;

import jakarta.transaction.Transactional;
import me.rahul.thoughts.blog.dto.BlogResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Query("""
            SELECT new me.rahul.thoughts.blog.dto.BlogResponse(
                b.id,
                u.username,
                b.content,
                b.createdAt,
                b.likeCount,
                b.commentCount,
                b.type
            )
            FROM Blog b
            JOIN User u ON b.author = u
            ORDER BY (b.likeCount * 1.0 + b.commentCount * 1.5 +
                      2.0 / (CAST(FUNCTION('TIMESTAMPDIFF', HOUR, b.createdAt, CURRENT_TIMESTAMP) AS integer) + 1)) DESC
            """)
    List<BlogResponse> getPopularBlogs();

    @Query("""
            SELECT new me.rahul.thoughts.blog.dto.BlogResponse(
                b.id,
                u.username,
                b.content,
                b.createdAt,
                b.likeCount,
                b.commentCount,
                b.type
            )
            FROM Blog b
            JOIN User u ON b.author = u
            WHERE b.id = :id
            """)
    Optional<BlogResponse> findBlogById(Long id);

    @Query("""
            SELECT new me.rahul.thoughts.blog.dto.BlogResponse(
                b.id,
                u.username,
                b.content,
                b.createdAt,
                b.likeCount,
                b.commentCount,
                b.type
            )
            FROM Blog b
            JOIN User u ON b.author = u
            WHERE b.author =
            (
                SELECT ff.followed
                FROM Following ff
                JOIN User uu ON uu = ff.follower
                WHERE uu.username = :username
            )
            ORDER BY b.createdAt DESC
            """)
    List<BlogResponse> findFromFollowedUser(String username);

    @Transactional
    @Modifying
    @Query("""
            UPDATE Blog b
            SET b.commentCount = b.commentCount + 1
            WHERE b.id = :id
            """)
    void incrementCommentCount(Long id);

    @Transactional
    @Modifying
    @Query("""
            UPDATE Blog b
            SET b.commentCount = b.commentCount - 1
            WHERE b.id = :id
            """)
    void decrementCommentCount(Long id);

    @Transactional
    @Modifying
    @Query("""
            UPDATE Blog b
            SET b.likeCount = b.likeCount + 1
            WHERE b.id = :id
            """)
    void incrementLikeCount(Long id);

    @Transactional
    @Modifying
    @Query("""
            UPDATE Blog b
            SET b.likeCount = b.likeCount - 1
            WHERE b.id = :id
            """)
    void decrementLikeCount(Long id);
}
