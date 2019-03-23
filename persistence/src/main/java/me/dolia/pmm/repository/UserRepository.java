package me.dolia.pmm.repository;

import me.dolia.pmm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Jpa repository to performs database operations with {@code User} entity.
 *
 * @author Maksym Dolia
 */
public interface UserRepository extends JpaRepository<User, String> {

}
