package me.rahul.thoughts.blog;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.rahul.thoughts.blog.dto.BlogResponse;
import me.rahul.thoughts.blog.dto.CreateBlogRequest;
import me.rahul.thoughts.exception.BadRequestException;
import me.rahul.thoughts.exception.NotFoundException;
import me.rahul.thoughts.user.User;
import me.rahul.thoughts.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {

    final private BlogRepository blogRepository;
    final private UserService userService;
    final private BlogLikeRepository blogLikeRepository;

    @Transactional
    public void createBlog(CreateBlogRequest createBlogRequest, String username) {
        User author = userService.getByUsername(username);

        if (createBlogRequest.type().equals(BlogType.POST)) {
            Blog newBlog = Blog.builder()
                    .content(createBlogRequest.content())
                    .type(createBlogRequest.type())
                    .author(author)
                    .build();
            blogRepository.save(newBlog);
        } else {
            if (createBlogRequest.parentId() == null)
                throw new BadRequestException("Parent id of a reply must be not null.");
            Blog parentBlog = blogRepository.findById(createBlogRequest.parentId())
                    .orElseThrow(() -> new NotFoundException("Blog with id '" + createBlogRequest.parentId() + "' not found."));
            Blog newBlog = Blog.builder()
                    .content(createBlogRequest.content())
                    .type(createBlogRequest.type())
                    .parent(parentBlog)
                    .author(author)
                    .build();
            blogRepository.save(newBlog);
            blogRepository.incrementCommentCount(parentBlog.getId());
        }
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

    public void likeBlog(Long blogId, String username) {
        User user = userService.getByUsername(username);
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new NotFoundException("Blog '" + blogId + "' not found."));

        if (blogLikeRepository.findByUserAndBlog(user, blog).isEmpty()) {
            blogLikeRepository.save(
                    BlogLike.builder()
                            .user(user)
                            .blog(blog)
                            .build()
            );
            blogRepository.incrementLikeCount(blogId);
        }
    }

    public void dislikeBlog(Long blogId, String username) {
        User user = userService.getByUsername(username);
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new NotFoundException("Blog '" + blogId + "' not found."));

        Optional<BlogLike> blogLike ;
        if ((blogLike = blogLikeRepository.findByUserAndBlog(user, blog)).isPresent()) {
            blogLikeRepository.delete(blogLike.get());
            blogRepository.decrementLikeCount(blogId);
        }
    }

}
