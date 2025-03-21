package me.rahul.thoughts.user;

import me.rahul.thoughts.user.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE active = true AND (email=:usernameOrEmail OR username=:usernameOrEmail)")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    @Query("SELECT u FROM User u WHERE active = true AND username=:username")
    Optional<User> findByUsername(String username);

    @Query(value = """
    SELECT new me.rahul.thoughts.user.dto.UserDto(u.username) FROM User u
    LEFT JOIN Following f ON u = f.follower
    WHERE f.followed = (
    SELECT u2 FROM User u2
    WHERE u2.username = :followedUser
    )
    """)
    List<UserDto> findAllFollower(String followedUser);

    @Query(value = """
    SELECT new me.rahul.thoughts.user.dto.UserDto(u.username)  FROM User u
    LEFT JOIN Following f ON u = f.followed
    WHERE f.follower = (
    SELECT u2 FROM User u2
    WHERE u2.username = :followerUser
    )
    """)
    List<UserDto> findAllFollowing(String followerUser);
}
