package me.dolia.mf.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import me.dolia.pmm.entity.User;
import me.dolia.pmm.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "file:src/main/webapp/WEB-INF/applicationContext.xml",
		"file:src/main/webapp/WEB-INF/dispatcher-servlet.xml", "file:src/main/webapp/WEB-INF/database-dev.xml" })
@TestExecutionListeners(listeners = { ServletTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, WithSecurityContextTestExecutionListener.class })
public class IndexControllerTest {

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	private UserService userService;

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).addFilters(springSecurityFilterChain).build();
	}

	@Test
	public void testIndex() throws Exception {
		this.mockMvc.perform(get("/index")).andExpect(status().isOk()).andExpect(view().name("index"));
	}

	@Test
	@WithMockUser
	public void testIndexWithAuthenticatedUser_shouldRedirectToDashboard() throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		this.mockMvc.perform(get("/index").with(authentication(authentication))).andExpect(status().isFound())
				.andExpect(redirectedUrl("/app"));
	}

	@Test
	public void testLogin() throws Exception {
		this.mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
	}

	@Test
	public void testSignin() throws Exception {
		this.mockMvc.perform(get("/signin")).andExpect(status().isOk()).andExpect(view().name("signin"));
	}
/*
	@Test
	public void testDoSigninWithValidationErrors() throws Exception {
		String email = "ahello@admin";
		String password = "1";

		this.mockMvc
				.perform(post("/signin").param("email", email).param("password", password).sessionAttr("user",
						new User()))
				.andExpect(status().isOk()).andExpect(view().name("signin"))
				.andExpect(model().attributeHasFieldErrors("user", "email"))
				.andExpect(model().attributeHasFieldErrors("user", "password"));
	}
*/
	@Test
	public void testDoSigninWithValidUserInput() throws Exception {
		String email = "good@email";
		String password = "goodPassword";

		this.mockMvc
				.perform(post("/signin").param("email", email).param("password", password).sessionAttr("user",
						new User()))
				.andExpect(status().isFound()).andExpect(flash().attributeExists("success"))
				.andExpect(redirectedUrl("/signin"));
	}

	@Test
	@WithMockUser("test@test")
	public void testProfile() throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		this.mockMvc.perform(get("/profile").with(authentication(authentication))).andExpect(status().isOk())
				.andExpect(view().name("profile"));
	}

	@Test
	@WithMockUser("test@test")
	public void testDeleteProfile() throws Exception {

		// create and save test user to database
		User user = new User();
		user.setEmail("test@test");
		user.setPassword("testAndTest");
		userService.save(user);

		assertNotNull("User should be created, so should return object (be not null)",
				userService.findOneByEmail(user.getEmail()));

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		this.mockMvc.perform(get("/profile/delete_profile").with(authentication(authentication)))
				.andExpect(status().isFound()).andExpect(flash().attributeExists("message"))
				.andExpect(redirectedUrl("/"));

		assertNull("User should be deleted, so should return null", userService.findOneByEmail(user.getEmail()));
	}

	@Test
	public void testAvailableEmail() throws Exception {
		MvcResult falseResult = this.mockMvc.perform(get("/signin/available_email").param("email", "admin@admin"))
				.andExpect(status().isOk()).andReturn();
		assertFalse("Email exists, should return false",
				Boolean.getBoolean(falseResult.getResponse().getContentAsString()));

		MvcResult trueResult = this.mockMvc.perform(get("/signin/available_email").param("email", "hello@hello"))
				.andExpect(status().isOk()).andReturn();
		assertTrue("Email does not exist, should return true",
				Boolean.parseBoolean(trueResult.getResponse().getContentAsString()));
	}

}
