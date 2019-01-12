package me.dolia.pmm.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import me.dolia.pmm.entity.Role;
import me.dolia.pmm.entity.User;
import me.dolia.pmm.repository.RoleRepository;
import me.dolia.pmm.repository.UserRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginIT {

  private static final String TEST_EMAIL = "test@test.com";
  private static final String LOGIN_PAGE = "/login";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @After
  public void tearDown() {
    if (userRepository.existsById(TEST_EMAIL)) {
      userRepository.deleteById(TEST_EMAIL);
    }
  }

  @Test
  @WithAnonymousUser
  public void loginAvailableForAll() throws Exception {
    mockMvc.perform(get(LOGIN_PAGE))
        .andExpect(status().isOk())
        .andExpect(view().name("login"))
        .andExpect(unauthenticated());
  }

  @Test
  @WithMockUser
  public void redirectAuthenticatedUserToApp() throws Exception {
    mockMvc.perform(get(LOGIN_PAGE))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/app"));
  }

  @Test
  @Transactional
  public void successfullyLogInUser() throws Exception {
    User user = new User();
    user.setEmail(TEST_EMAIL);
    user.setPassword(passwordEncoder.encode("password"));
    user.setRoles(Collections.singletonList(roleRepository.findByName("ROLE_USER").orElseGet(() -> {
      Role role = new Role();
      role.setName("ROLE_USER");
      return roleRepository.saveAndFlush(role);
    })));
    userRepository.saveAndFlush(user);

    mockMvc.perform(formLogin().userParameter("email").user(TEST_EMAIL))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/app"))
        .andExpect(authenticated());
  }

  @Test
  public void invalidLogin() throws Exception {
    mockMvc.perform(formLogin().password("invalid"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(LOGIN_PAGE))
        .andExpect(unauthenticated());
  }

  @Test
  @WithMockUser
  public void successfullyLogOut() throws Exception {
    mockMvc.perform(logout())
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/?message=logout_success"))
        .andExpect(unauthenticated());
  }

  @Test
  @WithMockUser
  public void testInvalidCSRF() throws Exception {
    mockMvc.perform(post("/logout").with(csrf().useInvalidToken()))
        .andExpect(status().isForbidden())
        .andExpect(authenticated());
  }
}
