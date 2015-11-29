package me.dolia.pmm.repository;

import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Jpa repository to performs database operations with {@code Category} entity.
 *
 * @author Maksym Dolia
 */
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**
     * Populates all categories by user's email and given Operation.
     *
     * @param email     user's email
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
