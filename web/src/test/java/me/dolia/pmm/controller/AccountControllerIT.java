package me.dolia.pmm.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.Currency;
import me.dolia.pmm.persistence.entity.Account;
import me.dolia.pmm.persistence.entity.Operation;
import me.dolia.pmm.persistence.entity.Transaction;
import me.dolia.pmm.persistence.repository.AccountRepository;
import me.dolia.pmm.persistence.repository.TransactionRepository;
import me.dolia.pmm.persistence.repository.UserRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test cases for AccountController class.
 *
 * @author Maksym Dolia
 * @since 01.12.2015.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerIT {

  private static final String TEST_ADMIN_USERNAME = "admin@admin";
  private static final String ACCOUNTS_VIEW_NAME = "accounts";
  private static final String ROOT_MAPPING = "/app/accounts";
  private static final String REDIRECT_TO_LOGIN_PAGE_URL = "http://*/login";
  private static final String EDIT_ACCOUNT_1_PATH = "/1/edit";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TransactionRepository transactionRepository;

  @Ignore
  @Test
  public void testAccountsUserAuthorised() throws Exception {
    mockMvc.perform(get(ROOT_MAPPING).with(user(TEST_ADMIN_USERNAME)))
        .andExpect(status().isOk())
        .andExpect(view().name(ACCOUNTS_VIEW_NAME))
        .andExpect(model().attributeExists(ACCOUNTS_VIEW_NAME))
        .andExpect(model().attributeExists("balance"));
  }

  @Test
  public void testAccountsUserNotAuthorised() throws Exception {
    mockMvc.perform(get(ROOT_MAPPING))
        .andExpect(status().isFound())
        .andExpect(redirectedUrlPattern(REDIRECT_TO_LOGIN_PAGE_URL));
  }

  @Ignore
  @Test
  public void testAddAccountUserAuthorisedValidData() throws Exception {
    var name = "test account";
    var amount = "10";
    var currency = "UAH";

    mockMvc.perform(post(ROOT_MAPPING + "/add_account")
        .with(user(TEST_ADMIN_USERNAME)).with(csrf())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("name", name)
        .param("amount", amount)
        .param("currency", currency)
    )
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(ROOT_MAPPING));
  }

  @Test
  public void testAddAccountUserAuthorisedNotValidData() throws Exception {
    var name = "";
    var amount = "12.123";
    var currency = "UAH";

    mockMvc.perform(post(ROOT_MAPPING + "/add_account")
        .with(user(TEST_ADMIN_USERNAME))
        .with(csrf())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("name", name)
        .param("amount", amount)
        .param("currency", currency)
    )
        .andExpect(status().isFound())
        .andExpect(flash().attributeCount(2))
        .andExpect(flash()
            .attributeExists("account", "org.springframework.validation.BindingResult.account"))
        .andExpect(redirectedUrl(ROOT_MAPPING));
  }

  @Test
  public void testTransactionsUserNotAuthorised() throws Exception {
    mockMvc.perform(get(ROOT_MAPPING + "/1"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrlPattern(REDIRECT_TO_LOGIN_PAGE_URL));
  }

  @Ignore
  @Test
  public void testTransactionsUserAuthorised() throws Exception {
    var user = userRepository.findById(TEST_ADMIN_USERNAME).get();
    var transaction = user.getTransactions().get(0);
    mockMvc.perform(get(ROOT_MAPPING + "/" + transaction.getId()).with(user(TEST_ADMIN_USERNAME)))
        .andExpect(status().isOk())
        .andExpect(view().name("transactions"))
        .andExpect(model().attributeExists(
            "transactions",
            "showTransactionForm",
            ACCOUNTS_VIEW_NAME,
            "expenseCategories",
            "incomeCategories"));
  }

  @Test
  public void testDeleteAccountUserNotAuthorised() throws Exception {
    mockMvc.perform(get(ROOT_MAPPING + "/1/remove"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrlPattern(REDIRECT_TO_LOGIN_PAGE_URL));
  }

  @Ignore
  @Test
  public void testDeleteEmptyAccountUserAuthorised() throws Exception {
    var account = new Account();
    account.setAmount(BigDecimal.TEN);
    account.setCurrency(Currency.getInstance("UAH"));
    account.setName("Test account");
    account.setUser(userRepository.findById(TEST_ADMIN_USERNAME).get());
    account = accountRepository.save(account);

    mockMvc.perform(
        get(String.format("%s/%d/remove", ROOT_MAPPING, account.getId()))
            .with(user(TEST_ADMIN_USERNAME)))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(ROOT_MAPPING));
  }

  @Ignore
  @Test
  public void testDeleteNotEmptyAccountUserAuthorised() throws Exception {

    /* setting up new test account */
    var account = new Account();
    account.setAmount(BigDecimal.TEN);
    account.setCurrency(Currency.getInstance("UAH"));
    account.setName("Test account");
    account.setUser(userRepository.findById(TEST_ADMIN_USERNAME).get());
    account = accountRepository.save(account);

    /* add transaction to account */
    var transaction = new Transaction();
    transaction.setAccount(account);
    transaction.setAmount(BigDecimal.TEN);
    transaction.setCurrency(Currency.getInstance("UAH"));
    transaction.setOperation(Operation.INCOME);
    transactionRepository.save(transaction);

    mockMvc.perform(
        get(String.format("%s/%d/remove", ROOT_MAPPING, account.getId()))
            .with(user(TEST_ADMIN_USERNAME)))
        .andExpect(status().isFound())
        .andExpect(flash().attributeExists("message"))
        .andExpect(redirectedUrl(String.format("%s/%d/edit", ROOT_MAPPING, account.getId())));
  }

  @Test
  public void testEditAccountShowPageUserNotAuthorised() throws Exception {
    mockMvc.perform(get(ROOT_MAPPING + EDIT_ACCOUNT_1_PATH))
        .andExpect(status().isFound())
        .andExpect(redirectedUrlPattern(REDIRECT_TO_LOGIN_PAGE_URL));
  }

  @Ignore
  @Test
  public void testEditAccountShowPageUserAuthorised() throws Exception {
    mockMvc.perform(get(ROOT_MAPPING + EDIT_ACCOUNT_1_PATH).with(user(TEST_ADMIN_USERNAME)))
        .andExpect(status().isOk())
        .andExpect(view().name("accounts_edit"))
        .andExpect(model().attributeExists(
            "account",
            ACCOUNTS_VIEW_NAME,
            "balance",
            "quantityOfTransactions")
        );
  }

  @Test
  public void testDoEditAccountUserNotAuthorised() throws Exception {
    mockMvc.perform(post(ROOT_MAPPING + EDIT_ACCOUNT_1_PATH))
        .andExpect(status().isForbidden());
  }

  @Ignore
  @Test
  public void testDoEditAccountWithValidDataUserAuthorised() throws Exception {
    mockMvc.perform(post(ROOT_MAPPING + EDIT_ACCOUNT_1_PATH)
        .with(user(TEST_ADMIN_USERNAME))
        .with(csrf())
        .param("name", "Hello")
    )
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(ROOT_MAPPING));
  }

  @Test
  public void testTransferTransactionsUserNotAuthorised() throws Exception {
    mockMvc.perform(post(ROOT_MAPPING + "/transform"))
        .andExpect(status().isForbidden());
  }

  @Ignore
  @Test
  public void testTransferTransactionsUserAuthorised() throws Exception {
    var user = userRepository.findById(TEST_ADMIN_USERNAME).get();
    var accounts = user.getAccounts();

    mockMvc.perform(post(ROOT_MAPPING + "/transfer")
        .with(user(TEST_ADMIN_USERNAME))
        .with(csrf())
        .param("fromAccount", "" + accounts.get(0).getId())
        .param("toAccount", "" + accounts.get(1).getId())
    )
        .andExpect(status().isFound())
        .andExpect(model().attributeExists("fromId"))
        .andExpect(
            redirectedUrl(String.format("%s/%d/edit", ROOT_MAPPING, accounts.get(0).getId())));
  }
}