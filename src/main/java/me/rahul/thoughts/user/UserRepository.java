package me.rahul.thoughts.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE active = true AND (email=:usernameOrEmail OR username=:usernameOrEmail)")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

}
