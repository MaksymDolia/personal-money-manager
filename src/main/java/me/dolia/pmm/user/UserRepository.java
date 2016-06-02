package me.dolia.pmm.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository to manage {@link User} instances.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves the user by given email.
     *
     * @param email user's email
     * @return the user with the given email or null if none found
     */
    Optional<User> findByEmail(String email);
}