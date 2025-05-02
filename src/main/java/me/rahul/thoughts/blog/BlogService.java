package me.rahul.thoughts.blog;

import lombok.RequiredArgsConstructor;
import me.rahul.thoughts.blog.dto.BlogResponse;
import me.rahul.thoughts.blog.dto.CreateBlogRequest;
import me.rahul.thoughts.exception.NotFoundException;
import me.rahul.thoughts.user.User;
import me.rahul.thoughts.user.UserRepository;
import me.rahul.thoughts.user.UserService;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {

    final private BlogRepository blogRepository;
    final private UserService userService;
    private final UserRepository userRepository;

    public void createBlog(CreateBlogRequest createBlogRequest, String username) {
        User author = userService.getByUsername(username);
        Blog newBlog = Blog.builder()
                .content(createBlogRequest.content())
                .type(BlogType.POST)
                .author(author)
                .build();
        blogRepository.save(newBlog);
    }

    public BlogResponse getBlog(Long id) {
        return blogRepository.findBlogById(id)
                .orElseThrow(() -> new NotFoundException("Blog with id '" + id + "' not found."));
    }

    public List<BlogResponse> getPopularBlogs() {
        return blogRepository.getPopularBlogs();
    }

    public List<BlogResponse> getBlogsFromFollowedUser(String username) {
        return blogRepository.findFromFollowedUser(username);
    }
}
