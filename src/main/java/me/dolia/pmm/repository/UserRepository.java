package me.dolia.pmm.repository;

import me.dolia.pmm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Jpa repository to performs database operations with {@code User} entity.
 *
 * @author Maksym Dolia
 */
public interface UserRepository extends JpaRepository<User, Integer> {

  /**
   * Looks for user by given email.
   *
   * @param email user's email
   * @return user
   */
  User findOneByEmail(String email);

  /**
   * Deletes user with given email.
   *
   * @param email user's email
   */
  void deleteByEmail(String email);

}
