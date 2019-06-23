package me.dolia.pmm.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
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
    var firstUser = createTestUser("first@user.com");
    createTestAccount(BigDecimal.TEN, firstUser);
    createTestAccount(BigDecimal.valueOf(23.15), firstUser);
    var secondUser = createTestUser("second@user.com");
    createTestAccount(BigDecimal.ONE, secondUser);

    assertThat(accountRepository.getSumAmountByUserEmail(firstUser.getEmail())).isEqualTo(33.15);
  }

  @Test
  void calculatedSumIsZeroWhenUserHasNoAccounts() {
    assertThat(accountRepository.getSumAmountByUserEmail("test@test.com")).isEqualTo(0);
  }

  @Test
  void calculatedSumIsZeroWhenUserHasSingleEmptyAccount() {
    var user = createTestUser("first@user.com");
    createTestAccount(BigDecimal.ZERO, user);

    var result = accountRepository.getSumAmountByUserEmail(user.getEmail());

    assertThat(result).isEqualTo(0);
  }

  @Test
  void findAllUsersAccounts() {
    var firstUser = createTestUser("first@user.com");
    var acc1 = createTestAccount(BigDecimal.TEN, firstUser);
    var acc2 = createTestAccount(BigDecimal.valueOf(23.15), firstUser);
    var secondUser = createTestUser("second@user.com");
    createTestAccount(BigDecimal.ONE, secondUser);

    var firstUserAccounts = accountRepository.findAllByUserEmail(firstUser.getEmail());

    assertThat(firstUserAccounts).hasSize(2).contains(acc1, acc2);
  }

  @Test
  void findAllByUserEmailReturnsEmptyList() {
    var accounts = accountRepository.findAllByUserEmail("test");

    assertThat(accounts).isEmpty();
  }

  private User createTestUser(String email) {
    var user = new User();
    user.setEmail(email);
    user.setPassword("letmein");
    return userRepository.save(user);
  }

  private Account createTestAccount(BigDecimal amount, User owner) {
    var account = new Account();
    account.setName("Account " + amount);
    account.setAmount(amount);
    account.setUser(owner);
    return accountRepository.save(account);
  }
}
