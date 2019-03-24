package me.dolia.pmm.persistence.repository;

import me.dolia.pmm.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Jpa repository to performs database operations with {@code User} entity.
 *
 * @author Maksym Dolia
 */
public interface UserRepository extends JpaRepository<User, String> {

}
