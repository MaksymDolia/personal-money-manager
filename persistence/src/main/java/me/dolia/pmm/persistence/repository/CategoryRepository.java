package me.dolia.pmm.persistence.repository;

import java.util.List;
import me.dolia.pmm.persistence.entity.Category;
import me.dolia.pmm.persistence.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Jpa repository to performs database operations with {@code Category} entity.
 *
 * @author Maksym Dolia
 */
public interface CategoryRepository extends JpaRepository<Category, Integer> {

  /**
   * Populates all categories by user's email and given Operation.
   *
   * @param email user's email
   * @param operation given operation
   * @return list of categories
   */
  List<Category> findAllByUserEmailAndOperation(String email, Operation operation);

  /**
   * Returns all user's categories.
   *
   * @param email user's email
   * @return list of categories
   */
  List<Category> findAllByUserEmail(String email);

}
