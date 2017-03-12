package me.dolia.pmm.controller;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Currency;

import javax.transaction.Transactional;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.repository.AccountRepository;
import me.dolia.pmm.repository.TransactionRepository;
import me.dolia.pmm.repository.UserRepository;

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
@Rollback
public class AccountControllerTest {

    private static String ROOT_MAPPING = "/app/accounts";

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
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilter(filterChainProxy).build();
    }

    @Test
    public void testAccountsUserAuthorised() throws Exception {
        mockMvc.perform(get(ROOT_MAPPING).with(user("admin@admin")))
                .andExpect(status().isOk())
                .andExpect(view().name("accounts"))
                .andExpect(model().attributeExists("accounts"))
                .andExpect(model().attributeExists("balance"));
    }

    @Test
    public void testAccountsUserNotAuthorised() throws Exception {
        mockMvc.perform(get(ROOT_MAPPING))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }

    @Test
    public void testAddAccountUserAuthorisedValidData() throws Exception {
        String name = "test account";
        String amount = "10";
        String currency = "UAH";

        mockMvc.perform(post(ROOT_MAPPING + "/add_account")
                .with(user("admin@admin")).with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", name)
                .param("amount", amount)
                .param("currency", currency)
        )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/app/accounts"));
    }

    @Test
    public void testAddAccountUserAuthorisedNotValidData() throws Exception {
        String name = "";
        String amount = "12.123";
        String currency = "UAH";

        mockMvc.perform(post(ROOT_MAPPING + "/add_account")
                .with(user("admin@admin"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", name)
                .param("amount", amount)
                .param("currency", currency)
        )
                .andExpect(status().isFound())
                .andExpect(flash().attributeCount(2))
                .andExpect(flash().attributeExists("account", "org.springframework.validation.BindingResult.account"))
                .andExpect(redirectedUrl(ROOT_MAPPING));
    }

    @Test
    public void testTransactionsUserNotAuthorised() throws Exception {
        mockMvc.perform(get(ROOT_MAPPING + "/1"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }

    @Test
    public void testTransactionsUserAuthorised() throws Exception {
        mockMvc.perform(get(ROOT_MAPPING + "/1").with(user("admin@admin")))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions"))
                .andExpect(model().attributeExists(
                        "transactions",
                        "showTransactionForm",
                        "accounts",
                        "expenseCategories",
                        "incomeCategories"));
    }

    @Test
    public void testDeleteAccountUserNotAuthorised() throws Exception {
        mockMvc.perform(get(ROOT_MAPPING + "/1/remove"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }

    @Test
    public void testDeleteEmptyAccountUserAuthorised() throws Exception {
        Account account = new Account();
        account.setAmount(BigDecimal.TEN);
        account.setCurrency(Currency.getInstance("UAH"));
        account.setName("Test account");
        account.setUser(userRepository.findOneByEmail("admin@admin"));
        account = accountRepository.save(account);

        mockMvc.perform(get(String.format(ROOT_MAPPING + "/%d/remove", account.getId())).with(user("admin@admin")))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(ROOT_MAPPING));
    }

    @Test
    public void testDeleteNotEmptyAccountUserAuthorised() throws Exception {

        /* setting up new test account */
        Account account = new Account();
        account.setAmount(BigDecimal.TEN);
        account.setCurrency(Currency.getInstance("UAH"));
        account.setName("Test account");
        account.setUser(userRepository.findOneByEmail("admin@admin"));
        account = accountRepository.save(account);

        /* add transaction to account */
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setCurrency(Currency.getInstance("UAH"));
        transaction.setOperation(Operation.INCOME);
        transactionRepository.save(transaction);

        mockMvc.perform(get(String.format(ROOT_MAPPING + "/%d/remove", account.getId())).with(user("admin@admin")))
                .andExpect(status().isFound())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl(String.format(ROOT_MAPPING + "/%d/edit", account.getId())));
    }

    @Test
    public void testEditAccountShowPageUserNotAuthorised() throws Exception {
        mockMvc.perform(get(ROOT_MAPPING + "/1/edit"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }

    @Test
    public void testEditAccountShowPageUserAuthorised() throws Exception {
        mockMvc.perform(get(ROOT_MAPPING + "/1/edit").with(user("admin@admin")))
                .andExpect(status().isOk())
                .andExpect(view().name("accounts_edit"))
                .andExpect(model().attributeExists(
                        "account",
                        "accounts",
                        "balance",
                        "quantityOfTransactions")
                );
    }

    @Test
    public void testDoEditAccountUserNotAuthorised() throws Exception {
        mockMvc.perform(post(ROOT_MAPPING + "/1/edit"))
                .andExpect(status().isForbidden());
    }

    @Ignore
    @Test
    public void testDoEditAccountWithNotValidDataUserAuthorised() throws Exception {
        mockMvc.perform(post(ROOT_MAPPING + "/1/edit")
                .with(user("admin@admin"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "")
                .param("amount", "100")
                .param("currency", "UAH")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("accounts_edit"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("name"));
    }

    @Test
    public void testDoEditAccountWithValidDataUserAuthorised() throws Exception {
        mockMvc.perform(post(ROOT_MAPPING + "/1/edit")
                .with(user("admin@admin"))
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
                .with(user("admin@admin"))
                .with(csrf())
                .param("fromAccount", "1")
                .param("toAccount", "2")
        )
                .andExpect(status().isFound())
                .andExpect(model().attributeExists("fromId"))
                .andExpect(redirectedUrl(ROOT_MAPPING + "/1/edit"));
    }
}