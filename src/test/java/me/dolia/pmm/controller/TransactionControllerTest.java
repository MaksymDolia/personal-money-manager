package me.dolia.pmm.controller;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for Transaction Controller.
 *
 * @author Maksym Dolia
 * @since 02.12.2015.
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
public class TransactionControllerTest {

    private static String ROOT_MAPPING = "/app/transactions";

    @Inject
    private WebApplicationContext wac;

    @Inject
    private FilterChainProxy filterChainProxy;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilter(filterChainProxy).build();
    }

    @Test
    public void testTransactionsUserNotAuthorised() throws Exception {
        mockMvc.perform(get(ROOT_MAPPING))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }

    @Test
    public void testTransactionsUserAuthorised() throws Exception {
        mockMvc.perform(get(ROOT_MAPPING).with(user("admin@admin")))
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
    public void testAddTransactionUserNotAuthorised() throws Exception {
        mockMvc.perform(post(ROOT_MAPPING + "/add_transaction"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }

    @Test
    public void testAddTransactionWithValidDataUserAuthorised() throws Exception {
        mockMvc.perform(post(ROOT_MAPPING + "/add_transaction")
                .with(user("admin@admin"))
                .param("operation", "INCOME")
                .param("account", "1")
                .param("amount", "10")
                .param("currency", "UAH")
        )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(ROOT_MAPPING));
    }

    @Ignore("Need to check validation rules for Transaction entity.")
    @Test
    public void testAddTransactionWithNotValidDataUserAuthorised() throws Exception {
        mockMvc.perform(post(ROOT_MAPPING + "/add_transaction")
                .with(user("admin@admin"))
                .param("operation", "")
                .param("account", "1")
                .param("amount", "100")
                .param("currency", "UAH")
                .param("category", "")
                .param("date", "2015-12-02")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("transactions_edit"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("currency"));
    }

    @Test
    public void testRemoveTransactionUserNotAuthorised() throws Exception {
        mockMvc.perform(post(ROOT_MAPPING + "/1/remove"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }

    @Test
    public void testRemoveTransactionUserAuthorised() throws Exception {
        mockMvc.perform(post(ROOT_MAPPING + "/1/remove").with(user("admin@admin")))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(ROOT_MAPPING));
    }

    @Test
    public void testShowEditTransactionPageUserNotAuthorised() throws Exception {
        mockMvc.perform(get(ROOT_MAPPING + "/1/edit"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }

    @Test
    public void testShowEditTransactionPageUserAuthorised() throws Exception {
        mockMvc.perform(get(ROOT_MAPPING + "/1/edit").with(user("admin@admin")))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions_edit"));
    }

    @Test
    public void testDoEditTransactionUserNotAuthorised() throws Exception {
        mockMvc.perform(post(ROOT_MAPPING + "/1/edit"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }

    @Test
    public void testDoEditTransactionWithValidDataUserAuthorised() throws Exception {
        mockMvc.perform(post(ROOT_MAPPING + "/1/edit")
                .with(user("admin@admin"))
                .param("amount", "546")
                .param("operation", "INCOME")
                .param("account", "1")
        )
                .andExpect(model().hasNoErrors())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(ROOT_MAPPING));
    }

    @Test
    public void testDoEditTransactionWithNotValidDataUserAuthorised() throws Exception {
        mockMvc.perform(post(ROOT_MAPPING + "/1/edit")
                .with(user("admin@admin"))
                .param("amount", "")
        )
                .andExpect(model().hasErrors())
                .andExpect(status().isOk())
                .andExpect(view().name("transactions_edit"));
    }
}