package me.dolia.pmm.util;

import me.dolia.pmm.account.Account;
import me.dolia.pmm.category.Category;
import me.dolia.pmm.domain.Operation;
import me.dolia.pmm.user.User;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * @author Maksym Dolia
 * @since 28.05.2016
 */
public class TestUtils {

    public static User createUser() {
        return createUser("some@email.com");
    }

    public static User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("somePassword");
        user.setEnable(true);
        return user;
    }

    public static Category createCategory() {
        Category category = generateCategory();
        category.setUser(createUser());
        return category;
    }

    public static Category createCategoryFor(String email) {
        Category category = generateCategory();
        category.setUser(createUser(email));
        return category;
    }

    public static Category createCategoryFor(User user) {
        Category category = generateCategory();
        category.setUser(user);
        return category;
    }

    private static Category generateCategory() {
        Category category = new Category();
        category.setName("Some Category");
        category.setOperation(Operation.EXPENSE);
        return category;
    }

    public static Account createAccountFor(User user) {
        Account account = new Account();
        account.setName("Some account name");
        account.setBalance(BigDecimal.valueOf(12.34));
        account.setCurrency(Currency.getInstance("USD"));
        account.setUser(user);
        return account;
    }
}