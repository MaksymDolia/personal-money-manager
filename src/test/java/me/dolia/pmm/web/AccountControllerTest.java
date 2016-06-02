package me.dolia.pmm.web;

import me.dolia.pmm.account.Account;
import me.dolia.pmm.config.security.CustomUserDetails;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static me.dolia.pmm.util.TestUtils.createAccountFor;
import static me.dolia.pmm.web.AccountController.ACCOUNTS_API_URL;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AccountControllerTest extends AbstractWebIntegrationTest {

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
        prepareData();
    }

    @Test
    public void readAccounts() throws Exception {
        Account second = accountRepository.save(createAccountFor(user));

        mockMvc.perform(get(ACCOUNTS_API_URL)
                .with(user(new CustomUserDetails(user))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(account.getId().intValue()))
                .andExpect(jsonPath("$[0].name").value(account.getName()))
                .andExpect(jsonPath("$[0].balance").value(account.getBalance().doubleValue()))
                .andExpect(jsonPath("$[0].currency").value(account.getCurrency().toString()))
                .andExpect(jsonPath("$[0].user").doesNotExist())
                .andExpect(jsonPath("$[1].id").value(second.getId().intValue()))
                .andExpect(jsonPath("$[1].name").value(second.getName()))
                .andExpect(jsonPath("$[1].balance").value(second.getBalance().doubleValue()))
                .andExpect(jsonPath("$[1].currency").value(second.getCurrency().toString()))
                .andExpect(jsonPath("$[1].user").doesNotExist());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void readSingleAccount() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", ACCOUNTS_API_URL, account.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id").value(account.getId().intValue()))
                .andExpect(jsonPath("$.name").value(account.getName()))
                .andExpect(jsonPath("$.balance").value(account.getBalance().doubleValue()))
                .andExpect(jsonPath("$.currency").value(account.getCurrency().toString()))
                .andExpect(jsonPath("$.user").doesNotExist());
    }

    @Test
    @WithMockUser(BAD_GUY_EMAIL)
    public void readSingleAccountOfSomeoneElse() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", ACCOUNTS_API_URL, account.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createAccount() throws Exception {
        Account data = createAccountFor(user);
        String accountJson = json(data);

        mockMvc.perform(post(ACCOUNTS_API_URL)
                .with(user(new CustomUserDetails(user)))
                .contentType(contentType)
                .content(accountJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", notNullValue()))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(data.getName()))
                .andExpect(jsonPath("$.balance").value(data.getBalance().doubleValue()))
                .andExpect(jsonPath("$.currency").value(data.getCurrency().toString()))
                .andExpect(jsonPath("$.user").doesNotExist());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void updateAccount() throws Exception {
        Account accountData = createAccountFor(user);
        accountData.setName("Updated name");
        String json = json(accountData);

        mockMvc.perform(put(String.format("%s/%d", ACCOUNTS_API_URL, account.getId()))
                .contentType(contentType)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void updateNonexistentAccount() throws Exception {
        Account accountData = createAccountFor(user);
        accountData.setName("Updated name");
        String json = json(accountData);

        mockMvc.perform(put(String.format("%s/%d", ACCOUNTS_API_URL, NONEXISTENT_ENTITY_ID))
                .contentType(contentType)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(BAD_GUY_EMAIL)
    public void updateAccountOfSomeoneElse() throws Exception {
        Account accountData = createAccountFor(user);
        accountData.setName("Updated name");
        String json = json(accountData);

        mockMvc.perform(put(String.format("%s/%d", ACCOUNTS_API_URL, account.getId()))
                .contentType(contentType)
                .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void deleteSingleAccount() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", ACCOUNTS_API_URL, account.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(BAD_GUY_EMAIL)
    public void deleteSingleAccountOfSomeoneElse() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", ACCOUNTS_API_URL, account.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void deleteSingleNonexistentAccount() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", ACCOUNTS_API_URL, NONEXISTENT_ENTITY_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void readBalance() throws Exception {
        mockMvc.perform(get(String.format("%s/%s", ACCOUNTS_API_URL, "/balance"))
                .with(user(new CustomUserDetails(user))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.balance").isNumber());
    }
}