package me.dolia.pmm.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.transaction.Transactional;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
public class TransactionControllerIT {

  private static final String TEST_ADMIN_USERNAME = "admin@admin";
  private static final String ROOT_MAPPING = "/app/transactions";
  private static final String ADD_TRANSACTION_PATH = "/add_transaction";
  private static final String OPERATION_PARAM = "operation";
  private static final String ACCOUNT_PARAM = "account";
  private static final String AMOUNT_PARAM = "amount";
  private static final String CURRENCY_PARAM = "currency";
  private static final String TRANSACTIONS_EDIT_VIEW_NAME = "transactions_edit";
  private static final String EDIT_TRANSACTION_1_PATH = "/1/edit";

  @Autowired
  private WebApplicationContext wac;

  @Autowired
  private FilterChainProxy filterChainProxy;

  private MockMvc mockMvc;

  @Before
  public void setUp() {
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
    mockMvc.perform(get(ROOT_MAPPING).with(user(TEST_ADMIN_USERNAME)))
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
    mockMvc.perform(post(ROOT_MAPPING + ADD_TRANSACTION_PATH))
        .andExpect(status().isForbidden());
  }

  @Ignore
  @Test
  public void testAddTransactionWithValidDataUserAuthorised() throws Exception {
    mockMvc.perform(post(ROOT_MAPPING + ADD_TRANSACTION_PATH)
        .with(user(TEST_ADMIN_USERNAME))
        .with(csrf())
        .param(OPERATION_PARAM, "INCOME")
        .param(ACCOUNT_PARAM, "1")
        .param(AMOUNT_PARAM, "10")
        .param(CURRENCY_PARAM, "UAH")
    )
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(ROOT_MAPPING));
  }

  @Ignore("Need to check validation rules for Transaction entity.")
  @Test
  public void testAddTransactionWithNotValidDataUserAuthorised() throws Exception {
    mockMvc.perform(post(ROOT_MAPPING + ADD_TRANSACTION_PATH)
        .with(user(TEST_ADMIN_USERNAME))
        .with(csrf())
        .param(OPERATION_PARAM, "")
        .param(ACCOUNT_PARAM, "1")
        .param(AMOUNT_PARAM, "100")
        .param(CURRENCY_PARAM, "UAH")
        .param("category", "")
        .param("date", "2015-12-02")
    )
        .andExpect(status().isOk())
        .andExpect(view().name(TRANSACTIONS_EDIT_VIEW_NAME))
        .andExpect(model().hasErrors())
        .andExpect(model().errorCount(1))
        .andExpect(model().attributeHasFieldErrors(CURRENCY_PARAM));
  }

  @Test
  public void testRemoveTransactionUserNotAuthorised() throws Exception {
    mockMvc.perform(post(ROOT_MAPPING + "/1/remove"))
        .andExpect(status().isForbidden());
  }

  @Ignore
  @Test
  public void testRemoveTransactionUserAuthorised() throws Exception {
    mockMvc.perform(post(ROOT_MAPPING + "/1/remove")
        .with(user(TEST_ADMIN_USERNAME))
        .with(csrf())
    )
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(ROOT_MAPPING));
  }

  @Test
  public void testShowEditTransactionPageUserNotAuthorised() throws Exception {
    mockMvc.perform(get(ROOT_MAPPING + EDIT_TRANSACTION_1_PATH))
        .andExpect(status().isFound())
        .andExpect(redirectedUrlPattern("http://*/login"));
  }

  @Ignore
  @Test
  public void testShowEditTransactionPageUserAuthorised() throws Exception {
    mockMvc.perform(get(ROOT_MAPPING + EDIT_TRANSACTION_1_PATH).with(user(TEST_ADMIN_USERNAME)))
        .andExpect(status().isOk())
        .andExpect(view().name(TRANSACTIONS_EDIT_VIEW_NAME));
  }

  @Test
  public void testDoEditTransactionUserNotAuthorised() throws Exception {
    mockMvc.perform(post(ROOT_MAPPING + EDIT_TRANSACTION_1_PATH))
        .andExpect(status().isForbidden());
  }

  @Ignore
  @Test
  public void testDoEditTransactionWithValidDataUserAuthorised() throws Exception {
    mockMvc.perform(post(ROOT_MAPPING + EDIT_TRANSACTION_1_PATH)
        .with(user(TEST_ADMIN_USERNAME))
        .with(csrf())
        .param(AMOUNT_PARAM, "546")
        .param(OPERATION_PARAM, "INCOME")
        .param(ACCOUNT_PARAM, "1")
    )
        .andExpect(model().hasNoErrors())
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(ROOT_MAPPING));
  }

  @Ignore
  @Test
  public void testDoEditTransactionWithNotValidDataUserAuthorised() throws Exception {
    mockMvc.perform(post(ROOT_MAPPING + EDIT_TRANSACTION_1_PATH)
        .with(user(TEST_ADMIN_USERNAME))
        .with(csrf())
        .param(AMOUNT_PARAM, "")
    )
        .andExpect(model().hasErrors())
        .andExpect(status().isOk())
        .andExpect(view().name(TRANSACTIONS_EDIT_VIEW_NAME));
  }
}