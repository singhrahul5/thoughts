package me.rahul.thoughts.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.rahul.thoughts.user.dto.FollowRequest;
import me.rahul.thoughts.user.dto.UnfollowRequest;
import me.rahul.thoughts.user.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static me.rahul.thoughts.constants.AppConstants.API_PATH;

@RequiredArgsConstructor
@RestController
@RequestMapping(API_PATH + "/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/follow")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void followUser(@RequestBody @Valid FollowRequest followRequest, Authentication auth) {
        userService.followUser(followRequest.userToFollow(), auth.getName());
    }

    @PostMapping("/unfollow")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollowUser(@RequestBody @Valid UnfollowRequest unfollowRequest, Authentication auth) {
        userService.unfollowUser(unfollowRequest.userToUnfollow(), auth.getName());
    }

    @GetMapping("/follower")
    @PreAuthorize("hasRole('USER')")
    public List<UserDto> getAllFollower(Authentication auth) {
        return userService.getAllFollower(auth.getName());
    }

    @GetMapping("/following")
    @PreAuthorize("hasRole('USER')")
    public List<UserDto> getAllFollowing(Authentication auth) {
        return userService.getAllFollowing(auth.getName());
    }
}
