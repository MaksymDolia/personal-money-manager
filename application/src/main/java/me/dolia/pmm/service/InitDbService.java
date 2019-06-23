package me.dolia.pmm.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.dolia.pmm.persistence.entity.Account;
import me.dolia.pmm.persistence.entity.Category;
import me.dolia.pmm.persistence.entity.Operation;
import me.dolia.pmm.persistence.entity.Role;
import me.dolia.pmm.persistence.entity.Transaction;
import me.dolia.pmm.persistence.entity.User;
import me.dolia.pmm.persistence.repository.AccountRepository;
import me.dolia.pmm.persistence.repository.CategoryRepository;
import me.dolia.pmm.persistence.repository.RoleRepository;
import me.dolia.pmm.persistence.repository.TransactionRepository;
import me.dolia.pmm.persistence.repository.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Fulfills database with initial data.
 *
 * @author Maksym Dolia
 */
@Service
@RequiredArgsConstructor
public class InitDbService {

  private static final Currency UAH = Currency.getInstance("UAH");

  private final ApplicationContext context;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final AccountRepository accountRepository;
  private final CategoryRepository categoryRepository;
  private final TransactionRepository transactionRepository;

  /**
   * Stores to persistence layer initial data.
   */
  @PostConstruct
  public void init() {
    if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
      var roleUser = new Role();
      roleUser.setName("ROLE_USER");
      roleRepository.save(roleUser);

      var roleAdmin = new Role();
      roleAdmin.setName("ROLE_ADMIN");
      roleRepository.save(roleAdmin);

      var user = new User();
      user.setEnabled(true);
      user.setEmail("admin@admin");

      var encoder = new BCryptPasswordEncoder();
      user.setPassword(encoder.encode("admin"));
      List<Role> roles = List.of(roleAdmin, roleUser);
      user.setRoles(roles);
      userRepository.save(user);

      // Create account for wallet
      var walletAccount = new Account();
      walletAccount.setName(context.getMessage("Name.default.account", null, Locale.ENGLISH));
      walletAccount.setUser(user);
      walletAccount.setAmount(BigDecimal.ZERO);
      walletAccount.setCurrency(UAH);
      accountRepository.save(walletAccount);

      var bankAccount = new Account();
      bankAccount.setName("Bank");
      bankAccount.setUser(user);
      bankAccount.setAmount(new BigDecimal(500));
      bankAccount.setCurrency(UAH);
      accountRepository.save(bankAccount);

      for (int i = 1; i < 8; i++) {
        var category = new Category();
        category
            .setName(context.getMessage("Name" + i + ".default.category", null, Locale.ENGLISH));
        category.setOperation(i > 5 ? Operation.INCOME : Operation.EXPENSE);
        category.setUser(user);
        categoryRepository.save(category);
      }

      var transaction1 = new Transaction();
      transaction1.setDate(new Date());
      transaction1.setAccount(walletAccount);
      transaction1.setAmount(new BigDecimal(50));
      transaction1.setCurrency(UAH);
      transaction1.setCategory(categoryRepository.findById(9).get());
      transaction1.setOperation(Operation.EXPENSE);
      transaction1.setComment("McDonalds");
      transaction1.setUser(user);
      transactionRepository.save(transaction1);

      var transaction2 = new Transaction();
      var calendar = new GregorianCalendar();
      calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
      transaction2.setDate(calendar.getTime());
      transaction2.setAccount(bankAccount);
      transaction2.setAmount(new BigDecimal(45));
      transaction2.setCurrency(UAH);
      transaction2.setCategory(categoryRepository.findById(10).get());
      transaction2.setOperation(Operation.INCOME);
      transaction2.setComment("Festo");
      transaction2.setUser(user);
      transactionRepository.save(transaction2);
    }
  }
}