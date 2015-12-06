package me.dolia.pmm.service;

import me.dolia.pmm.entity.*;
import me.dolia.pmm.repository.*;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.*;

/**
 * Full fills database with initial data.
 *
 * @author Maksym Dolia
 */
@Named
public class InitDbService {

    @Inject
    private ApplicationContext context;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private TransactionRepository transactionRepository;

    /**
     * Stores to persistence layer initial data.
     */
    @PostConstruct
    public void init() {
        if (roleRepository.findByName("ROLE_ADMIN") == null) {
            Role roleUser = new Role();
            roleUser.setName("ROLE_USER");
            roleRepository.save(roleUser);

            Role roleAdmin = new Role();
            roleAdmin.setName("ROLE_ADMIN");
            roleRepository.save(roleAdmin);

            User user = new User();
            user.setEnabled(true);
            user.setEmail("admin@admin");

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
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
            walletAccount.setCurrency(Currency.getInstance("UAH"));
            accountRepository.save(walletAccount);

            Account bankAccount = new Account();
            bankAccount.setName("Bank");
            bankAccount.setUser(user);
            bankAccount.setAmount(new BigDecimal(500));
            bankAccount.setCurrency(Currency.getInstance("UAH"));
            accountRepository.save(bankAccount);

            // Create categories for expenses
            for (int i = 1; i < 6; i++) {
                Category category = new Category();
                category.setName(context.getMessage("Name" + i + ".default.category", null, Locale.ENGLISH));
                category.setType(Operation.EXPENSE);
                category.setUser(user);
                categoryRepository.save(category);
            }

            // Create categories for incomes
            for (int i = 6; i < 8; i++) {
                Category category = new Category();
                category.setName(context.getMessage("Name" + i + ".default.category", null, Locale.ENGLISH));
                category.setType(Operation.INCOME);
                category.setUser(user);
                categoryRepository.save(category);
            }

            Transaction transaction1 = new Transaction();
            transaction1.setDate(new Date());
            transaction1.setAccount(walletAccount);
            transaction1.setAmount(new BigDecimal(50));
            transaction1.setCurrency(Currency.getInstance("UAH"));
            transaction1.setCategory(categoryRepository.findOne(3));
            transaction1.setType(Operation.EXPENSE);
            transaction1.setComment("McDonalds");
            transaction1.setUser(user);
            transactionRepository.save(transaction1);

            Transaction transaction2 = new Transaction();
            Calendar calendar = new GregorianCalendar();
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            transaction2.setDate(calendar.getTime());
            transaction2.setAccount(bankAccount);
            transaction2.setAmount(new BigDecimal(45));
            transaction2.setCurrency(Currency.getInstance("UAH"));
            transaction2.setCategory(categoryRepository.findOne(7));
            transaction2.setType(Operation.INCOME);
            transaction2.setComment("Festo");
            transaction2.setUser(user);
            transactionRepository.save(transaction2);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction1);
            user.setTransactions(transactions);
        }
    }
}