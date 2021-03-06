package me.dolia.pmm.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test cases for Dashboard Controller
 *
 * @author Maksym Dolia
 * @since 02.12.2015.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DashboardControllerIT {

  private static final String ROOT_MAPPING = "/app";

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testAppUserNotAuthorised() throws Exception {
    mockMvc.perform(get(ROOT_MAPPING))
        .andExpect(status().isFound())
        .andExpect(redirectedUrlPattern("http://*/login"));
  }

  @Ignore
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