package me.dolia.pmm.security;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
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
@Transactional
public class SecurityTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy filterChainProxy;

    private MockMvc mockMvc;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilter(filterChainProxy).build();

    }

    @Test
    public void testShouldAllowAccessToAppForUsers() throws Exception {
        mockMvc.perform(get("/app").with(user("test")))
                .andExpect(status().isOk())
                .andExpect(authenticated());
    }

    @Test
    public void testShouldAllowAccessToInnersOfAppForUsers() throws Exception {
        mockMvc.perform(get("/app/accounts").with(user("admin@admin")))
                .andExpect(status().isOk())
                .andExpect(authenticated());
    }

    @Test
    public void testValidCSRF() throws Exception {
        mockMvc.perform(post("/signin").with(csrf()))
                .andExpect(authenticated());
    }

    @Test
    public void testInvalidCSRF() throws Exception {
        mockMvc.perform(post("/signin").with(csrf().useInvalidToken()))
                .andExpect(unauthenticated());
    }
}
