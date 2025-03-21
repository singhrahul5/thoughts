package me.rahul.thoughts.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowingRepository extends JpaRepository<Following, Long> {

    Optional<Following> findByFollowerAndFollowed(User follower, User followed);

}
