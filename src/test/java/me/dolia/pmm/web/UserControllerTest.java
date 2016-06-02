package me.dolia.pmm.web;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.dolia.pmm.config.security.CustomUserDetails;
import me.dolia.pmm.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static me.dolia.pmm.web.UserController.USERS_API_URL;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


public class UserControllerTest extends AbstractWebIntegrationTest {

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(wac).apply(springSecurity()).build();
        prepareData();
    }

    @Test
    public void createUser() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.USE_ANNOTATIONS);
        String userJson = mapper.writeValueAsString(TestUtils.createUser(BAD_GUY_EMAIL));

        mockMvc.perform(post(USERS_API_URL)
                .contentType(contentType)
                .content(userJson)
        )
                .andExpect(status().isCreated())
                .andExpect(header().string("location", notNullValue()));
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void readUser() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", USERS_API_URL, user.getId()))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.created", is(user.getCreated())))
                .andExpect(jsonPath("$.enable", is(user.getEnable())))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.roles").doesNotExist());
    }

    @Test
    public void readAuthenticatedUser() throws Exception {
        mockMvc.perform(get(USERS_API_URL + "/me")
                .with(user(new CustomUserDetails(user))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    @WithMockUser(BAD_GUY_EMAIL)
    public void readUserProfileOfSomeoneElse() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", USERS_API_URL, user.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", USERS_API_URL, user.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void testDeleteNonExistentUser() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", USERS_API_URL, NONEXISTENT_ENTITY_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void checkEmailWithAvailableEmail() throws Exception {
        String email = "free@email.com";

        mockMvc.perform(get(USERS_API_URL + "/available").param("email", email))
                .andExpect(content().string(Boolean.TRUE.toString()));
    }

    @Test
    public void checkEmailWithNotAvailableEmail() throws Exception {
        mockMvc.perform(get(USERS_API_URL + "/available").param("email", TEST_USER_EMAIL))
                .andExpect(content().string(Boolean.FALSE.toString()));
    }
}