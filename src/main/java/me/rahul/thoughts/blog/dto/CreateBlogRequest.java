package me.rahul.thoughts.blog.dto;

import jakarta.validation.constraints.NotNull;

public record CreateBlogRequest(
        @NotNull String content
) {
}
