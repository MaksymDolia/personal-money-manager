package me.dolia.pmm.bootstrap;

import me.dolia.pmm.account.Account;
import me.dolia.pmm.account.AccountRepository;
import me.dolia.pmm.category.Category;
import me.dolia.pmm.category.CategoryRepository;
import me.dolia.pmm.domain.Operation;
import me.dolia.pmm.role.Role;
import me.dolia.pmm.role.RoleService;
import me.dolia.pmm.user.User;
import me.dolia.pmm.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * This component is responsible for filling database out when the application is newly created and deployed.
 */
@Component
@PropertySource("classpath:bootstrap.properties")
public class DBLoader implements ApplicationListener<ContextRefreshedEvent> {

    private static final String USER_ROLE_NAME = "USER";
    private static final String ADMIN_ROLE_NAME = "ADMIN";

    private final String adminEmail;
    private final String adminPassword;
    private final String[] accountsNames;
    private final String[] expenseCategoriesNames;
    private final String[] incomeCategoriesNames;

    private final RoleService roleService;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public DBLoader(@Value("${pmm.admin.email}") String adminEmail,
                    @Value("${pmm.admin.password}") String adminPassword,
                    @Value("${pmm.bootstrap.accounts}") String[] accountsNames,
                    @Value("${pmm.bootstrap.categories.expense}") String[] expenseCategoriesNames,
                    @Value("${pmm.bootstrap.categories.income}") String[] incomeCategoriesNames,
                    RoleService roleService,
                    UserService userService,
                    CategoryRepository categoryRepository,
                    AccountRepository accountRepository) {
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.accountsNames = accountsNames;
        this.expenseCategoriesNames = expenseCategoriesNames;
        this.incomeCategoriesNames = incomeCategoriesNames;
        this.roleService = roleService;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (userService.isAvailable(adminEmail)) {
            loadRoles();
            User admin = loadAndGetAdminUser();
            loadCategoriesFor(admin);
            loadAccountsFor(admin);
        }
    }

    private void loadRoles() {
        Role roleUser = new Role();
        roleUser.setName(USER_ROLE_NAME);
        roleService.save(roleUser);

        Role roleAdmin = new Role();
        roleAdmin.setName(ADMIN_ROLE_NAME);
        roleService.save(roleAdmin);
    }

    private User loadAndGetAdminUser() {
        User userAdmin = new User();
        userAdmin.setEmail(adminEmail);
        userAdmin.setEnable(Boolean.TRUE);
        userAdmin.setPassword(adminPassword);

        Role roleUser = roleService.find(USER_ROLE_NAME);
        Role roleAdmin = roleService.find(ADMIN_ROLE_NAME);
        Set<Role> roles = new HashSet<>(asList(roleUser, roleAdmin));
        userAdmin.setRoles(roles);
        return userService.save(userAdmin);
    }


    private void loadCategoriesFor(User admin) {
        for (String name : incomeCategoriesNames) {
            Category category = new Category();
            category.setName(name);
            category.setOperation(Operation.INCOME);
            category.setUser(admin);
            categoryRepository.save(category);
        }

        for (String name : expenseCategoriesNames) {
            Category category = new Category();
            category.setName(name);
            category.setOperation(Operation.EXPENSE);
            category.setUser(admin);
            categoryRepository.save(category);
        }
    }

    private void loadAccountsFor(User admin) {
        for (String name : accountsNames) {
            Account account = new Account();
            account.setBalance(BigDecimal.ZERO);
            account.setCurrency(Currency.getInstance("UAH"));
            account.setName(name);
            account.setUser(admin);
            accountRepository.save(account);
        }
    }
}