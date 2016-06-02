package me.dolia.pmm.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.dolia.pmm.account.Account;
import me.dolia.pmm.account.AccountRepository;
import me.dolia.pmm.config.security.CustomUserDetails;
import me.dolia.pmm.domain.Operation;
import me.dolia.pmm.transaction.Transaction;
import me.dolia.pmm.transaction.TransactionRepository;
import me.dolia.pmm.user.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Currency;

import static me.dolia.pmm.util.TestUtils.createAccountFor;
import static me.dolia.pmm.web.TransactionController.TRANSACTIONS_API_URL;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TransactionControllerTest extends AbstractWebIntegrationTest {

    private static final BigDecimal AMOUNT = BigDecimal.valueOf(15.65);

    @Autowired private AccountRepository accountRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private ObjectMapper mapper;
    private Transaction transaction;
    private Account fromAccount;
    private Account toAccount;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

        prepareData();
        transaction = createTransactionForTest(user);
        setUpAccountsFor(user);
        transaction.setCategory(category);
        transaction = transactionRepository.save(transaction);
    }

    private void setUpAccountsFor(User user) {
        fromAccount = accountRepository.save(createAccountFor(user));
        transaction.setFromAccount(fromAccount);
        toAccount = accountRepository.save(createAccountFor(user));
        transaction.setToAccount(toAccount);
    }

    private Transaction createTransactionForTest(User user) {
        transaction = new Transaction();
        transaction.setUser(user);
        transaction.setDate(LocalDateTime.now());
        transaction.setComment("Some comment here");
        Currency usd = Currency.getInstance("USD");
        transaction.setFromCurrency(usd);
        transaction.setToCurrency(usd);
        transaction.setToAmount(AMOUNT);
        transaction.setFromAmount(AMOUNT);
        transaction.setOperation(Operation.TRANSFER);
        return transaction;
    }

    @Test
    public void readTransactions() throws Exception {
        Transaction second = createTransactionForTest(user);
        second.setCategory(category);
        second = transactionRepository.save(second);

        mockMvc.perform(get(TRANSACTIONS_API_URL)
                .with(user(new CustomUserDetails(user))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id").value(second.getId().intValue()))
//                .andExpect(jsonPath("$[0].fromAccount").value(second.getFromAccount()))
//                .andExpect(jsonPath("$[0].fromAmount").value(second.getFromAmount().doubleValue()))
//                .andExpect(jsonPath("$[0].fromCurrency").value(second.getFromCurrency().toString()))
//                .andExpect(jsonPath("$[0].operation").value(second.getOperation().toString()))
//                .andExpect(jsonPath("$[0].category").value(second.getCategory()))
//                .andExpect(jsonPath("$[0].user").doesNotExist())
//                .andExpect(jsonPath("$[0].toAccount").value(second.getToAccount()))
//                .andExpect(jsonPath("$[0].toAmount").value(second.getToAmount().doubleValue()))
//                .andExpect(jsonPath("$[0].toCurrency").value(second.getToCurrency().toString()))
//                .andExpect(jsonPath("$[1].id").value(transaction.getId().intValue()))
//                .andExpect(jsonPath("$[1].fromAccount").value(transaction.getFromAccount()))
//                .andExpect(jsonPath("$[1].fromAmount").value(transaction.getFromAmount().doubleValue()))
//                .andExpect(jsonPath("$[1].fromCurrency").value(transaction.getFromCurrency().toString()))
//                .andExpect(jsonPath("$[1].operation").value(transaction.getOperation().toString()))
//                .andExpect(jsonPath("$[1].category").value(transaction.getCategory()))
//                .andExpect(jsonPath("$[1].user").doesNotExist())
//                .andExpect(jsonPath("$[1].toAccount").value(transaction.getToAccount()))
//                .andExpect(jsonPath("$[1].toAmount").value(transaction.getToAmount().doubleValue()))
                .andExpect(jsonPath("$[1].toCurrency").value(transaction.getToCurrency().toString()));
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void readSingleTransaction() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", TRANSACTIONS_API_URL, transaction.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
//                .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
//                .andExpect(jsonPath("$.fromAccount").value(transaction.getFromAccount()))
//                .andExpect(jsonPath("$.fromAmount").value(transaction.getFromAmount().doubleValue()))
//                .andExpect(jsonPath("$.fromCurrency").value(transaction.getFromCurrency().toString()))
//                .andExpect(jsonPath("$.operation").value(transaction.getOperation().toString()))
//                .andExpect(jsonPath("$.category").value(transaction.getCategory()))
//                .andExpect(jsonPath("$.user").doesNotExist())
//                .andExpect(jsonPath("$.toAccount").value(transaction.getToAccount()))
//                .andExpect(jsonPath("$.toAmount").value(transaction.getToAmount().doubleValue()))
                .andExpect(jsonPath("$.toCurrency").value(transaction.getToCurrency().toString()));
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void readSingleNonexistentTransaction() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", TRANSACTIONS_API_URL, NONEXISTENT_ENTITY_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(BAD_GUY_EMAIL)
    public void readSingleTransactionOfSomeoneElse() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", TRANSACTIONS_API_URL, transaction.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createSingleTransaction() throws Exception {
        transactionRepository.delete(transaction);
        String transactionAsJson = mapper.writeValueAsString(transaction);

        mockMvc.perform(post(TRANSACTIONS_API_URL)
                .with(user(new CustomUserDetails(user)))
                .contentType(contentType)
                .content(transactionAsJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", notNullValue()));
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void updateTransaction() throws Exception {
        transaction.setDate(LocalDateTime.of(2016, Month.APRIL, 23, 11, 15));
        transaction.setComment("An updated text");
        String transactionAsJson = json(transaction);

        mockMvc.perform(put(String.format("%s/%d", TRANSACTIONS_API_URL, transaction.getId()))
                .contentType(contentType)
                .content(transactionAsJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(BAD_GUY_EMAIL)
    public void updateTransactionOfSomeoneElse() throws Exception {
        transaction.setDate(LocalDateTime.of(2016, Month.APRIL, 23, 11, 15));
        transaction.setComment("An updated text");
        String transactionAsJson = json(transaction);

        mockMvc.perform(put(String.format("%s/%d", TRANSACTIONS_API_URL, transaction.getId()))
                .contentType(contentType)
                .content(transactionAsJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void updateNonexistentTransaction() throws Exception {
        transaction.setDate(LocalDateTime.of(2016, Month.APRIL, 23, 11, 15));
        transaction.setComment("An updated text");
        String transactionAsJson = json(transaction);

        mockMvc.perform(put(String.format("%s/%d", TRANSACTIONS_API_URL, NONEXISTENT_ENTITY_ID))
                .contentType(contentType)
                .content(transactionAsJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void deleteSingleTransaction() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", TRANSACTIONS_API_URL, transaction.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(BAD_GUY_EMAIL)
    public void deleteSingleTransactionOfSomeoneElse() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", TRANSACTIONS_API_URL, transaction.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void deleteSingleNonexistentTransaction() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", TRANSACTIONS_API_URL, NONEXISTENT_ENTITY_ID)))
                .andExpect(status().isNotFound());
    }
}