package me.dolia.pmm.web;

import me.dolia.pmm.Application;
import me.dolia.pmm.account.Account;
import me.dolia.pmm.account.AccountRepository;
import me.dolia.pmm.category.Category;
import me.dolia.pmm.category.CategoryRepository;
import me.dolia.pmm.config.security.SecurityConfig;
import me.dolia.pmm.role.Role;
import me.dolia.pmm.role.RoleRepository;
import me.dolia.pmm.user.User;
import me.dolia.pmm.user.UserRepository;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;

import static me.dolia.pmm.util.TestUtils.createAccountFor;
import static me.dolia.pmm.util.TestUtils.createCategoryFor;

/**
 * @author Maksym Dolia
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration({
        Application.class,
        SecurityConfig.class
})
@Transactional
abstract class AbstractWebIntegrationTest {

    protected static final String TEST_USER_EMAIL = "some_test@email.com";
    protected static final String BAD_GUY_EMAIL = "some_bad_guy@email.com";
    protected static final Long NONEXISTENT_ENTITY_ID = 999L;
    protected static final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    protected HttpMessageConverter mappingJackson2HttpMessageConverter;
    @Autowired protected WebApplicationContext wac;
    protected MockMvc mockMvc;

    @Autowired protected RoleRepository roleRepository;
    @Autowired protected UserRepository userRepository;
    @Autowired protected CategoryRepository categoryRepository;
    @Autowired protected AccountRepository accountRepository;

    protected User user;
    protected Category category;
    protected Account account;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    public void prepareData() throws Exception {
        Role role = new Role();
        role.setName("USER");
        roleRepository.save(role);

        user = new User();
        user.setEmail(TEST_USER_EMAIL);
        user.setRoles(Collections.singleton(role));
        user.setPassword("somePassword");
        user.setEnable(true);
        userRepository.save(user);

        category = categoryRepository.save(createCategoryFor(user));

        account = accountRepository.save(createAccountFor(user));
    }

    @SuppressWarnings("unchecked")
    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}