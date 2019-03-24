package me.dolia.pmm.security;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import me.dolia.pmm.persistence.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SigninIT {

  private static final String SIGNIN_PAGE = "/signin";
  private static final String TEST_EMAIL = "test@test.com";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void registrationPageAvailableForAll() throws Exception {
    mockMvc.perform(get(SIGNIN_PAGE))
        .andExpect(status().isOk())
        .andExpect(view().name("signin"))
        .andExpect(unauthenticated());
  }

  @Test
  @WithMockUser
  public void redirectExistingUserToApp() throws Exception {
    mockMvc.perform(get(SIGNIN_PAGE))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/app"))
        .andExpect(authenticated());
  }

  @Test
  @WithMockUser
  public void existingUserCannotCreateNewUser() throws Exception {
    mockMvc.perform(post(SIGNIN_PAGE).with(csrf())
        .param("email", TEST_EMAIL)
        .param("password", "password"))
        .andExpect(status().isForbidden())
        .andExpect(authenticated());

    assertThat(userRepository.findById(TEST_EMAIL).isPresent()).isFalse();
  }

  @Test
  @WithAnonymousUser
  public void unauthenticatedCanRegisterUser() throws Exception {
    mockMvc.perform(post(SIGNIN_PAGE).with(csrf())
        .param("email", TEST_EMAIL)
        .param("password", "password"))
        .andExpect(status().isFound())
        .andExpect(flash().attribute("success", true))
        .andExpect(redirectedUrl("/signin"))
        .andExpect(unauthenticated());
  }
}
