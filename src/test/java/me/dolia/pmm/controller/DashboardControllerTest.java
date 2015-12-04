package me.dolia.pmm.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test cases for Dashboard Controller
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
public class DashboardControllerTest {

    private static final String ROOT_MAPPING = "/app";

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
    public void testAppUserNotAuthorised() throws Exception {
        mockMvc.perform(get(ROOT_MAPPING))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }

    @Test
    public void testAppUserAuthorised() throws Exception {
        mockMvc.perform(get(ROOT_MAPPING)
                .with(
                        user("admin@admin").roles("USER")
                ))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists(
                        "accounts",
                        "categories",
                        "transactions",
                        "balance",
                        "expenseCategories",
                        "incomeCategories"
                ));
    }
}