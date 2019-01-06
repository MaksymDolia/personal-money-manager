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
import javax.transaction.Transactional;
import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.repository.AccountRepository;
import me.dolia.pmm.repository.TransactionRepository;
import me.dolia.pmm.repository.UserRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Test cases for AccountController class.
 *
 * @author Maksym Dolia
 * @since 01.12.2015.
 */
@ActiveProfiles("dev")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext.xml",
    "classpath:spring/dispatcher-servlet.xml"
})
@Transactional
public class AccountControllerIT {

    private static final String TEST_ADMIN_USERNAME = "admin@admin";
    private static final String ACCOUNTS_VIEW_NAME = "accounts";
    private static final String ROOT_MAPPING = "/app/accounts";
    private static final String REDIRECT_TO_LOGIN_PAGE_URL = "http://*/login";
    private static final String EDIT_ACCOUNT_1_PATH = "/1/edit";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy filterChainProxy;

    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilter(filterChainProxy).build();
    }

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

    @Test
    public void testAddAccountUserAuthorisedValidData() throws Exception {
        String name = "test account";
        String amount = "10";
        String currency = "UAH";

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

    @Ignore
    @Test
    public void testAddAccountUserAuthorisedNotValidData() throws Exception {
        String name = "";
        String amount = "12.123";
        String currency = "UAH";

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
        mockMvc.perform(get(ROOT_MAPPING + "/1").with(user(TEST_ADMIN_USERNAME)))
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
        Account account = new Account();
        account.setAmount(BigDecimal.TEN);
        account.setCurrency(Currency.getInstance("UAH"));
        account.setName("Test account");
        account.setUser(userRepository.findOneByEmail(TEST_ADMIN_USERNAME));
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
        Account account = new Account();
        account.setAmount(BigDecimal.TEN);
        account.setCurrency(Currency.getInstance("UAH"));
        account.setName("Test account");
        account.setUser(userRepository.findOneByEmail(TEST_ADMIN_USERNAME));
        account = accountRepository.save(account);

        /* add transaction to account */
        Transaction transaction = new Transaction();
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

    @Test
    public void testTransferTransactionsUserAuthorised() throws Exception {
        mockMvc.perform(post(ROOT_MAPPING + "/transfer")
            .with(user(TEST_ADMIN_USERNAME))
            .with(csrf())
            .param("fromAccount", "1")
            .param("toAccount", "2")
        )
            .andExpect(status().isFound())
            .andExpect(model().attributeExists("fromId"))
            .andExpect(redirectedUrl(ROOT_MAPPING + EDIT_ACCOUNT_1_PATH));
    }
}