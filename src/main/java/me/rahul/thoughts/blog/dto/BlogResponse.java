package me.rahul.thoughts.blog.dto;

import me.rahul.thoughts.blog.Blog;
import me.rahul.thoughts.blog.BlogType;

import java.time.Instant;

public record BlogResponse(
        Long id,
        String author,
        String content,
        Instant createdAt,
        Long likeCount,
        Long commentCount,
        BlogType type
) {
}
