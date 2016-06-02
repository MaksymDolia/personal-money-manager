package me.dolia.pmm.common.web;

import me.dolia.pmm.Application;
import me.dolia.pmm.config.security.SecurityConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration({
        Application.class,
        SecurityConfig.class
})
public class BaseControllerTest {

    private static final String HOME_PAGE_VIEW_NAME = "index";
    private static final String LOGIN_PAGE_VIEW_NAME = "login";
    private static final String HOME_URL = "/";
    private static final String LOGIN_URL = "/login";
    private static final String APP_URL = "/app";
    private static final String REGISTER_URL = "/register";
    private static final String REGISTER_PAGE_VIEW_NAME = "register";
    private static final String APP_FORWARD_URL = "/app/index.html";
    private static final String LOGIN_URL_PATTERN = "http:/*/login";

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(wac).apply(springSecurity()).build();
    }

    @Test
    public void testShouldShowLoginPageWhenUserIsNotLoggedIn() throws Exception {
        mockMvc.perform(get(LOGIN_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(LOGIN_PAGE_VIEW_NAME));
    }

    @Test
    @WithMockUser
    public void testShouldRedirectWhenRequestToLoginPageAndUserIsLoggedIn() throws Exception {
        mockMvc.perform(get(LOGIN_URL))
                .andExpect(redirectedUrl(APP_URL));
    }

    @Test
    public void testShowShouldRegisterPageWhenUserIsNotLoggedIn() throws Exception {
        mockMvc.perform(get(REGISTER_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(REGISTER_PAGE_VIEW_NAME));
    }

    @Test
    @WithMockUser
    public void testShouldRedirectWhenRequestToRegisterPageAndUserIsLoggedIn() throws Exception {
        mockMvc.perform(get(REGISTER_URL))
                .andExpect(redirectedUrl(APP_URL));
    }

    /* View controllers's tests */

    @Test
    public void testShouldShowHomePage() throws Exception {
        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(HOME_PAGE_VIEW_NAME));
    }

    @Test
    @WithMockUser
    public void testShouldShowAppPage() throws Exception {
        mockMvc.perform(get(APP_URL))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl(APP_FORWARD_URL));
    }

    @Test
    public void testShouldForwardToLoginPageWhenShowAppPageByNotLoggedInUser() throws Exception {
        mockMvc.perform(get(APP_URL))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern(LOGIN_URL_PATTERN));
    }
}