package me.rahul.thoughts.blog.dto;

import jakarta.validation.constraints.NotNull;
import me.rahul.thoughts.blog.BlogType;

public record CreateBlogRequest(
        @NotNull BlogType type,
        Long parentId,
        @NotNull String content
) {
}
