package me.dolia.pmm.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Role;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.entity.User;
import me.dolia.pmm.repository.AccountRepository;
import me.dolia.pmm.repository.CategoryRepository;
import me.dolia.pmm.repository.RoleRepository;
import me.dolia.pmm.repository.TransactionRepository;
import me.dolia.pmm.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Fulfills database with initial data.
 *
 * @author Maksym Dolia
 */
@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

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
  @Override
  public void run(String... args) {
    if (!roleRepository.findByName("ROLE_ADMIN").isPresent()) {
      Role roleUser = new Role();
      roleUser.setName("ROLE_USER");
      roleRepository.save(roleUser);

      Role roleAdmin = new Role();
      roleAdmin.setName("ROLE_ADMIN");
      roleRepository.save(roleAdmin);

      User user = new User();
      user.setEnabled(true);
      user.setEmail("admin@admin");

      PasswordEncoder encoder = new BCryptPasswordEncoder();
      user.setPassword(encoder.encode("admin"));
      List<Role> roles = new ArrayList<>();
      roles.add(roleAdmin);
      roles.add(roleUser);
      user.setRoles(roles);
      userRepository.save(user);

      // Create account for wallet
      Account walletAccount = new Account();
      walletAccount.setName(context.getMessage("Name.default.account", null, Locale.ENGLISH));
      walletAccount.setUser(user);
      walletAccount.setAmount(new BigDecimal(0));
      walletAccount.setCurrency(UAH);
      accountRepository.save(walletAccount);

      Account bankAccount = new Account();
      bankAccount.setName("Bank");
      bankAccount.setUser(user);
      bankAccount.setAmount(new BigDecimal(500));
      bankAccount.setCurrency(UAH);
      accountRepository.save(bankAccount);

      for (int i = 1; i < 8; i++) {
        Category category = new Category();
        category
            .setName(context.getMessage("Name" + i + ".default.category", null, Locale.ENGLISH));
        category.setOperation(i > 5 ? Operation.INCOME : Operation.EXPENSE);
        category.setUser(user);
        categoryRepository.save(category);
      }

      Transaction transaction1 = new Transaction();
      transaction1.setDate(new Date());
      transaction1.setAccount(walletAccount);
      transaction1.setAmount(new BigDecimal(50));
      transaction1.setCurrency(UAH);
      transaction1.setCategory(categoryRepository.findById(9).get());
      transaction1.setOperation(Operation.EXPENSE);
      transaction1.setComment("McDonalds");
      transaction1.setUser(user);
      transactionRepository.save(transaction1);

      Transaction transaction2 = new Transaction();
      Calendar calendar = new GregorianCalendar();
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