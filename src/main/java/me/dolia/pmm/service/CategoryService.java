package me.dolia.pmm.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.entity.User;
import me.dolia.pmm.repository.CategoryRepository;
import me.dolia.pmm.repository.TransactionRepository;
import me.dolia.pmm.repository.UserRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

/**
 * Service to deal with categories.
 *
 * @author Maksym Dolia
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;
  private final TransactionRepository transactionRepository;

  /**
   * Looks and returns category by given id.
   *
   * @param id category's id
   * @return category
   */
  @PostAuthorize("returnObject.user.email == authentication.name or hasRole('ADMIN')")
  public Category findOne(Integer id) {
    return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Category with ID %d was not found.", id)));
  }

  /**
   * Looks for all given user's categories, and by given operation.
   *
   * @param email user's email
   * @param operation given operation
   * @return list of categories
   */
  public List<Category> findAllByUserEmailAndOperation(String email, Operation operation) {
    return categoryRepository.findAllByUserEmailAndOperation(email, operation);
  }

  /**
   * Performs saving of given category to user with given email.
   *
   * @param category category to be stored
   * @param email user's email
   */
  public void save(Category category, String email) {
    User user = userRepository.findOneByEmail(email);
    category.setUser(user);
    categoryRepository.save(category);
  }

  /**
   * Deletes given category from persistence.
   *
   * @param category category to be deleted
   */
  @PreAuthorize("#category.user.email == authentication.name or hasRole('ADMIN')")
  public void delete(@P("category") Category category) {
    categoryRepository.delete(category);
  }

  /**
   * Performs saving of given category.
   *
   * @param category category
   */
  public void save(Category category) {
    categoryRepository.save(category);
  }

  /**
   * Update category with given data.
   *
   * @param existingCategory category to be updated
   * @param categoryNewData new category's data
   */
  public void editCategory(Category existingCategory, Category categoryNewData) {
    existingCategory.setName(categoryNewData.getName());
    existingCategory.setOperation(categoryNewData.getOperation());
    save(existingCategory);
  }

  /**
   * Change transaction's category.
   *
   * @param fromId id of current category
   * @param toId id of category transaction is moving to
   */
  public void transferTransactions(int fromId, int toId) {
    Category fromCategory = findOne(fromId);
    Category toCategory = findOne(toId);
    if (fromCategory != null || toCategory != null) {
      transferTransactions(fromCategory, toCategory);
    }

  }

  /**
   * Looks for all categories by given user's email.
   *
   * @param email user's email
   * @return list of categories
   */
  public List<Category> findAllByUserEmail(String email) {
    return categoryRepository.findAllByUserEmail(email);
  }

  @PreAuthorize("#fromCategory.user.email == authentication.name or #toCategory.user.email == authentication.name or hasRole('ADMIN')")
  private void transferTransactions(@P("fromCategory") Category fromCategory,
      @P("toCategory") Category toCategory) {
    List<Transaction> transactions = transactionRepository.findAllByCategory(fromCategory);
    if (!transactions.isEmpty()) {
      for (Transaction transaction : transactions) {
        transaction.setCategory(toCategory);
        transactionRepository.save(transaction);
      }
    }
  }
}
