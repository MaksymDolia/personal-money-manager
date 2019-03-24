package me.dolia.pmm.persistence.repository;

import java.util.Optional;
import me.dolia.pmm.persistence.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Jpa repository to performs database operations with {@code Role} entity.
 *
 * @author Maksym Dolia
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {

  /**
   * Returns role bi given name.
   *
   * @param name role's name
   * @return role
   */
  Optional<Role> findByName(String name);

}
