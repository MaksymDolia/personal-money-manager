package me.dolia.pmm.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import me.dolia.pmm.entity.User;
import me.dolia.pmm.service.UserService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ActiveProfiles("dev")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {
        "classpath:spring/applicationContext.xml",
        "classpath:spring/dispatcher-servlet.xml"
})
@Transactional
@Rollback
public class IndexControllerTest {

    private static final String URL_APP_DASHBOARD = "/app";

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(wac).addFilters(springSecurityFilterChain).build();
    }

    @Test
    public void testIndexUserNotAuthenticated() throws Exception {
        mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void testIndexUserAuthenticated() throws Exception {
        mockMvc.perform(get("/index")
                .with(user("admin@admin"))
        )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(URL_APP_DASHBOARD));
    }

    @Test
    public void testLoginUserNotAuthenticated() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void testLoginUserAuthenticated() throws Exception {
        mockMvc.perform(get("/login").with(user("admin@admin")))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(URL_APP_DASHBOARD));
    }

    @Test
    public void testSigninUserNotAuthenticated() throws Exception {
        mockMvc.perform(get("/signin"))
                .andExpect(status().isOk())
                .andExpect(view().name("signin"));
    }

    @Test
    public void testSigninUserAuthenticated() throws Exception {
        mockMvc.perform(get("/signin")
                .with(user("admin@admin"))
        )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(URL_APP_DASHBOARD));
    }

    @Test
    public void testDoSigninWithNotValidData() throws Exception {
        String email = "ahello@admin";
        String password = "";

        mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .with(csrf())
                .param("email", email)
                .param("password", password)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("signin"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasErrors("user"));
    }

    @Test
    public void testDoSigninWithValidData() throws Exception {
        String email = "good@email";
        String password = "goodPassword";

        mockMvc.perform(post("/signin")
                .with(csrf())
                .param("email", email)
                .param("password", password)
        )
                .andExpect(status().isFound())
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("success"))
                .andExpect(redirectedUrl("/signin"));
    }

    @Test
    public void testProfileUserAuthenticated() throws Exception {
        mockMvc.perform(get("/profile")
                .with(user("admin@admin"))
        )
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(view().name("profile"));
    }

    @Test
    public void testProfileUserNotAuthenticated() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isFound())
                .andExpect(unauthenticated());
    }

    @Test
    public void testDeleteProfileUserAuthenticated() throws Exception {

        // create and save test user to database
        User user = new User();
        user.setEmail("test@test");
        user.setPassword("testAndTest");
        userService.save(user);

        mockMvc.perform(get("/profile/delete_profile")
                .with(user(user.getEmail()))
        )
                .andExpect(status().isFound())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void testDeleteProfileUserNotAuthenticated() throws Exception {
        mockMvc.perform(get("/profile/delete_profile"))
                .andExpect(status().isFound())
                .andExpect(unauthenticated());
    }

    @Test
    public void testAvailableEmailWithValidEmail() throws Exception {
        mockMvc.perform(get("/signin/available_email").param("email", "hello@hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void testAvailableEmailWithNotValidEmail() throws Exception {
        mockMvc.perform(get("/signin/available_email").param("email", "admin@admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
