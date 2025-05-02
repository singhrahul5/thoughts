package me.rahul.thoughts.blog;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.rahul.thoughts.blog.dto.BlogResponse;
import me.rahul.thoughts.blog.dto.CreateBlogRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static me.rahul.thoughts.constants.AppConstants.API_PATH;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(API_PATH + "/blogs")
public class BlogController {
    final private BlogService blogService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void createBlog(@RequestBody @Valid CreateBlogRequest createBlogRequest,
                           Authentication auth) {
        blogService.createBlog(createBlogRequest, auth.getName());
    }

    @GetMapping("/{id}")
    public BlogResponse getBlog(@PathVariable Long id) {
        return blogService.getBlog(id);
    }

    @GetMapping("/trending")
    public List<BlogResponse> getPopularBlogs(Authentication auth) {
        return blogService.getPopularBlogs();
    }

    @GetMapping("/following")
    @PreAuthorize("hasRole('USER')")
    public List<BlogResponse> getBlogsFromFollowedUser(Authentication auth) {
        return blogService.getBlogsFromFollowedUser(auth.getName());
    }
}