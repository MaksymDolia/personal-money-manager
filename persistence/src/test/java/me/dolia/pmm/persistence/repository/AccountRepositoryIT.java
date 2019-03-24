package me.dolia.pmm.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import me.dolia.pmm.persistence.entity.Account;
import me.dolia.pmm.persistence.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AccountRepositoryIT {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    cleanupRepositories();
  }

  private void cleanupRepositories() {
    accountRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void calculateSumOfUsersAccount() {
    User firstUser = createTestUser("first@user.com");
    createTestAccount(BigDecimal.TEN, firstUser);
    createTestAccount(BigDecimal.valueOf(23.15), firstUser);
    User secondUser = createTestUser("second@user.com");
    createTestAccount(BigDecimal.ONE, secondUser);

    assertThat(accountRepository.getSumAmountByUserEmail(firstUser.getEmail())).isEqualTo(33.15);
  }

  @Test
  void calculatedSumIsZeroWhenUserHasNoAccounts() {
    assertThat(accountRepository.getSumAmountByUserEmail("test@test.com")).isEqualTo(0);
  }

  @Test
  void calculatedSumIsZeroWhenUserHasSingleEmptyAccount() {
    User user = createTestUser("first@user.com");
    createTestAccount(BigDecimal.ZERO, user);

    Double result = accountRepository.getSumAmountByUserEmail(user.getEmail());

    assertThat(result).isEqualTo(0);
  }

  @Test
  void findAllUsersAccounts() {
    User firstUser = createTestUser("first@user.com");
    Account acc1 = createTestAccount(BigDecimal.TEN, firstUser);
    Account acc2 = createTestAccount(BigDecimal.valueOf(23.15), firstUser);
    User secondUser = createTestUser("second@user.com");
    createTestAccount(BigDecimal.ONE, secondUser);

    List<Account> firstUserAccounts = accountRepository.findAllByUserEmail(firstUser.getEmail());

    assertThat(firstUserAccounts).hasSize(2).contains(acc1, acc2);
  }

  @Test
  void findAllByUserEmailReturnsEmptyList() {
    List<Account> accounts = accountRepository.findAllByUserEmail("test");

    assertThat(accounts).isEmpty();
  }

  private User createTestUser(String email) {
    User user = new User();
    user.setEmail(email);
    user.setPassword("letmein");
    return userRepository.save(user);
  }

  private Account createTestAccount(BigDecimal amount, User owner) {
    Account account = new Account();
    account.setName("Account " + amount);
    account.setAmount(amount);
    account.setUser(owner);
    return accountRepository.save(account);
  }
}
