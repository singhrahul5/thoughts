package me.rahul.thoughts.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.rahul.thoughts.exception.ResourceConflictException;
import me.rahul.thoughts.exception.BadRequestException;
import me.rahul.thoughts.exception.NotFoundException;
import me.rahul.thoughts.user.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final FollowingRepository followingRepository;

    @Override
    public User loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Wrong Credentials."));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("User '" + username + "' not found"));
    }

    @Transactional
    public void followUser(String userToFollow, String follower) {
        if (userToFollow.equals(follower))
            throw new BadRequestException("You cannot follow yourself.");

        User followedUser = getByUsername(userToFollow);
        User followerUser = getByUsername(follower);

        if (followingRepository.findByFollowerAndFollowed(followerUser, followedUser).isPresent())
            throw new ResourceConflictException("User already followed.");

        followingRepository.save(Following.builder()
                .followed(followedUser)
                .follower(followerUser)
                .build()
        );
    }

    public void unfollowUser(String userToUnfollow, String follower) {
        if (userToUnfollow.equals(follower))
            throw new BadRequestException("You cannot unfollow yourself.");

        User followedUser = getByUsername(userToUnfollow);
        User followerUser = getByUsername(follower);

        Optional<Following> following =
        followingRepository.findByFollowerAndFollowed(followerUser, followedUser);

        if (following.isEmpty())
            throw new ResourceConflictException("You are not following this user.");

        followingRepository.delete(following.get());
    }

    public List<UserDto> getAllFollower(String username) {
        return userRepository.findAllFollower(username);
    }

    public List<UserDto> getAllFollowing(String username) {
        return userRepository.findAllFollowing(username);
    }
}
