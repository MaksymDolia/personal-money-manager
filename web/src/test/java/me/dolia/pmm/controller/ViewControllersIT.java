package me.dolia.pmm.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ViewControllersIT {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void showHomepage() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("index"));
  }

  @Test
  @WithMockUser
  public void showProfile() throws Exception {
    mockMvc.perform(get("/profile"))
        .andExpect(status().isOk())
        .andExpect(view().name("profile"));
  }

  @Test
  public void show404() throws Exception {
    mockMvc.perform(get("/404"))
        .andExpect(status().isOk())
        .andExpect(view().name("404"));
  }
}
