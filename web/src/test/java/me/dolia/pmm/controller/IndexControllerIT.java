package me.dolia.pmm.controller;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.UUID;
import me.dolia.pmm.persistence.entity.User;
import me.dolia.pmm.persistence.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class IndexControllerIT {

  private static final String EMAIL_USER_EXIST = "first.test@email.com";
  private static final String EMAIL_USER_NOT_EXIST = "second.test@email.com";
  private static final String SIGNIN_PATH = "/signin";
  private static final String EMAIL_PARAM = "email";

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private MockMvc mockMvc;

  @Before
  public void setUp() {
    createAndSaveUser(EMAIL_USER_EXIST);
  }

  private void createAndSaveUser(String email) {
    var user = new User();
    user.setEmail(email);
    user.setPassword(UUID.randomUUID().toString());
    userRepo.save(user);
  }

  @After
  public void tearDown() {
    userRepo.deleteAll();
  }

  @Ignore
  @Test
  public void shouldNotRegisterUserWithNotValidData() throws Exception {
    var email = "";
    var testPswd = "";

    mockMvc.perform(post(SIGNIN_PATH)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param(EMAIL_PARAM, email)
        .param("password", testPswd)
        .with(csrf())
    )
        .andExpect(status().isOk())
        .andExpect(view().name("signin"))
        .andExpect(model().hasErrors())
        .andExpect(model().attributeHasErrors("user"));

    assertThat(userRepo.findById(email).isPresent()).isTrue();
  }

  @Test
  public void shouldRegisterUserWithNotValidData() throws Exception {
    var email = EMAIL_USER_NOT_EXIST;
    var testPswd = "goodPassword";

    mockMvc.perform(post(SIGNIN_PATH)
        .with(csrf())
        .param(EMAIL_PARAM, email)
        .param("password", testPswd)
    )
        .andExpect(status().isFound())
        .andExpect(model().hasNoErrors())
        .andExpect(flash().attributeExists("success"))
        .andExpect(redirectedUrl(SIGNIN_PATH));

    assertThat(userRepo.findById(email).isPresent()).isTrue();
  }

  @Test
  public void shouldDeleteProfileIfUserExists() throws Exception {
    mockMvc.perform(get("/profile/delete_profile")
        .with(user(EMAIL_USER_EXIST)))
        .andExpect(status().isFound())
        .andExpect(flash().attributeExists("message"))
        .andExpect(redirectedUrl("/"));
  }

  @Test
  public void shouldReturnTrueIfEmailIsAvailable() throws Exception {
    mockMvc.perform(get("/signin/available_email").param(EMAIL_PARAM, EMAIL_USER_NOT_EXIST))
        .andExpect(status().isOk())
        .andExpect(content().string(Boolean.TRUE.toString()));
  }

  @Test
  public void shouldReturnFalseIfEmailIsNotAvailable() throws Exception {
    mockMvc.perform(get("/signin/available_email").param(EMAIL_PARAM, EMAIL_USER_EXIST))
        .andExpect(status().isOk())
        .andExpect(content().string(Boolean.FALSE.toString()));
  }
}
