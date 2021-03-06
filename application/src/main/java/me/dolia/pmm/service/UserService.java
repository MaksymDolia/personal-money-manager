package me.dolia.pmm.service;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import me.dolia.pmm.persistence.entity.Account;
import me.dolia.pmm.persistence.entity.Category;
import me.dolia.pmm.persistence.entity.Operation;
import me.dolia.pmm.persistence.entity.User;
import me.dolia.pmm.persistence.repository.AccountRepository;
import me.dolia.pmm.persistence.repository.CategoryRepository;
import me.dolia.pmm.persistence.repository.RoleRepository;
import me.dolia.pmm.persistence.repository.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to deal with users.
 *
 * @author Maksym Dolia
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final ApplicationContext context;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AccountRepository accountRepository;
  private final CategoryRepository categoryRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public void save(String email, String rawPassword) {
    var user = new User();
    user.setEmail(email);
    user.setEnabled(true);
    user.setPassword(passwordEncoder.encode(rawPassword));
    var userRole = roleRepository.findByName("ROLE_USER");
    user.setRoles(userRole.map(List::of).orElseGet(Collections::emptyList));
    var savedUser = userRepository.save(user);
    createDefaultAccountsAndCategoriesForNewUser(savedUser);
  }

  /**
   * Returns user by given email.
   *
   * @param email user's email
   * @return user
   */
  public User findOneByEmail(String email) {
    requireNonNull(email, "email is null");
    return userRepository.findById(email).orElseThrow(
        () -> new NotFoundException(String.format("User with email '%s' does not exist", email)));
  }

  /**
   * Creates and assigns to user initial data when new user has been creating.
   *
   * @param user user
   */
  private void createDefaultAccountsAndCategoriesForNewUser(User user) {

    //Create account for wallet
    var walletAccount = new Account();
    walletAccount.setName(context.getMessage("Name.default.account", null, Locale.ENGLISH));
    walletAccount.setUser(user);
    walletAccount.setAmount(new BigDecimal(0));
    walletAccount.setCurrency(Currency.getInstance("UAH"));
    accountRepository.save(walletAccount);

    //Create categories for expenses
    var expensesCategories = IntStream.range(1, 6).mapToObj(i -> {
      Category category = new Category();
      category.setName(context.getMessage("Name" + i + ".default.category", null, Locale.ENGLISH));
      category.setOperation(Operation.EXPENSE);
      category.setUser(user);
      return category;
    }).collect(toList());
    categoryRepository.saveAll(expensesCategories);

    //Create categories for incomes
    var incomeCategories = IntStream.range(6, 8).mapToObj(i -> {
      Category category = new Category();
      category.setName(context.getMessage("Name" + i + ".default.category", null, Locale.ENGLISH));
      category.setOperation(Operation.INCOME);
      category.setUser(user);
      return category;
    }).collect(toList());
    categoryRepository.saveAll(incomeCategories);
  }

  /**
   * Deletes user and all his data by given user's email.
   *
   * @param email user's email
   */
  @Transactional
  @PreAuthorize("#email == authentication.name or hasRole('ADMIN')")
  public void deleteByEmail(@P("email") String email) {
    userRepository.deleteById(email);
  }

}
