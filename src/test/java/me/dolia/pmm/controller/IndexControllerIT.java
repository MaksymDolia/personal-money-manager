package me.dolia.pmm.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.UUID;
import me.dolia.pmm.entity.User;
import me.dolia.pmm.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexControllerIT {

  private static final String URL_APP_DASHBOARD = "/app";

  private static final String EMAIL_USER_EXIST = "first.test@email.com";
  private static final String EMAIL_USER_NOT_EXIST = "second.test@email.com";
  private static final String SIGNIN_PATH = "/signin";
  public static final String EMAIL_PARAM = "email";

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Before
  public void setUp() {
    mockMvc = webAppContextSetup(wac).apply(springSecurity()).build();

    createAndSaveUser(EMAIL_USER_EXIST);
  }

  private void createAndSaveUser(String email) {
    User user = new User();
    user.setEmail(email);
    user.setPassword(UUID.randomUUID().toString());
    userRepo.save(user);
  }

  @After
  public void tearDown() {
    userRepo.deleteAll();
  }

  @Test
  public void shouldShowHomePageForAnonymousUser() throws Exception {
    mockMvc.perform(get("/index"))
        .andExpect(status().isOk())
        .andExpect(view().name("index"));
  }

  @Test
  @WithMockUser
  public void shouldRedirectFromHomePageToAppIfUserIsAuthenticated() throws Exception {
    mockMvc.perform(get("/index"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(URL_APP_DASHBOARD));
  }

  @Test
  public void shouldShowLoginPageForAnonymousUser() throws Exception {
    mockMvc.perform(get("/login"))
        .andExpect(status().isOk())
        .andExpect(view().name("login"));
  }

  @Test
  @WithMockUser
  public void shouldRedirectFromLoginPageToAppIfUserIsAuthenticated() throws Exception {
    mockMvc.perform(get("/login"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(URL_APP_DASHBOARD));
  }

  @Test
  public void shouldShowSigninPageForAnonymousUser() throws Exception {
    mockMvc.perform(get(SIGNIN_PATH))
        .andExpect(status().isOk())
        .andExpect(view().name("signin"));
  }

  @Test
  @WithMockUser
  public void shouldRedirectFromSignInPageToAppIfUserIsAuthenticated() throws Exception {
    mockMvc.perform(get(SIGNIN_PATH))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(URL_APP_DASHBOARD));
  }

  @Ignore
  @Test
  public void shouldNotRegisterUserWithNotValidData() throws Exception {
    String email = "";
    String testPswd = "";

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

    User storedUser = userRepo.findOneByEmail(email).get();
    assertNull(storedUser);
  }

  @Test
  public void shouldRegisterUserWithNotValidData() throws Exception {
    String email = EMAIL_USER_NOT_EXIST;
    String testPswd = "goodPassword";

    mockMvc.perform(post(SIGNIN_PATH)
        .with(csrf())
        .param(EMAIL_PARAM, email)
        .param("password", testPswd)
    )
        .andExpect(status().isFound())
        .andExpect(model().hasNoErrors())
        .andExpect(flash().attributeExists("success"))
        .andExpect(redirectedUrl(SIGNIN_PATH));

    User storedUser = userRepo.findOneByEmail(email).get();
    assertNotNull(storedUser);
  }

  @Test
  public void shouldShowProfilePageIfUserIsAuthenticated() throws Exception {
    mockMvc.perform(get("/profile")
        .with(user(EMAIL_USER_EXIST))
    )
        .andExpect(status().isOk())
        .andExpect(view().name("profile"));
  }

  @Test
  public void shouldRedirectFromProfilePageIfUserIsAnonymous() throws Exception {
    mockMvc.perform(get("/profile"))
        .andExpect(status().isFound());
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
