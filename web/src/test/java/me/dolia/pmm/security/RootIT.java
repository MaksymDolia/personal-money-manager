package me.dolia.pmm.security;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
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
public class RootIT {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void rootPageAvailableForAll() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("index"));
  }

  @Test
  @WithMockUser
  public void redirectUserFromHomepageToApp() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/app"))
        .andExpect(authenticated());
  }

  @Test
  @WithMockUser
  public void profilePageAvailableForAuthenticated() throws Exception {
    mockMvc.perform(get("/profile"))
        .andExpect(status().isOk())
        .andExpect(view().name("profile"))
        .andExpect(authenticated());
  }

  @Test
  public void anonymousDoesNotHaveProfile() throws Exception {
    mockMvc.perform(get("/profile"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrlPattern("**/login"))
        .andExpect(unauthenticated());
  }

  @Test
  public void emailCheckAvailableForAll() throws Exception {
    mockMvc.perform(get("/signin/available_email").param("email", "test@test.com"))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }
}
