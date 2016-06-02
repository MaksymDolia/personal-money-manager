package me.dolia.pmm.web;

import me.dolia.pmm.category.Category;
import me.dolia.pmm.config.security.CustomUserDetails;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static me.dolia.pmm.util.TestUtils.createCategoryFor;
import static me.dolia.pmm.web.CategoryController.CATEGORIES_API_URL;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


public class CategoryControllerTest extends AbstractWebIntegrationTest {

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(wac).apply(springSecurity()).build();
        prepareData();
    }

    @Test
    public void readCategories() throws Exception {
        Category second = createCategoryFor(user);
        second = categoryRepository.save(second);

        mockMvc.perform(get(CATEGORIES_API_URL).with(user(new CustomUserDetails(user))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(category.getId().intValue()))
                .andExpect(jsonPath("$[0].name").value(category.getName()))
                .andExpect(jsonPath("$[0].operation").value(category.getOperation().toString()))
                .andExpect(jsonPath("$[1].id").value(second.getId().intValue()))
                .andExpect(jsonPath("$[1].name").value(second.getName()))
                .andExpect(jsonPath("$[1].operation").value(second.getOperation().toString()));
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void readSingleCategory() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", CATEGORIES_API_URL, category.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id").value(category.getId().intValue()))
                .andExpect(jsonPath("$.name").value(category.getName()))
                .andExpect(jsonPath("$.operation").value(category.getOperation().toString()))
                .andExpect(jsonPath("$.user").doesNotExist());
    }

    @Test
    @WithMockUser(BAD_GUY_EMAIL)
    public void readSingleCategoryOfSomeoneElse() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", CATEGORIES_API_URL, category.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void readSingleNonExistentCategory() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", CATEGORIES_API_URL, NONEXISTENT_ENTITY_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createCategory() throws Exception {
        Category newCategory = createCategoryFor(TEST_USER_EMAIL);
        String categoryJson = json(newCategory);

        mockMvc.perform(post(CATEGORIES_API_URL)
                .with(user(new CustomUserDetails(user)))
                .content(categoryJson)
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", notNullValue()))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(newCategory.getName()))
                .andExpect(jsonPath("$.operation").value(newCategory.getOperation().toString()))
                .andExpect(jsonPath("$.user").doesNotExist());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void updateCategory() throws Exception {
        Category data = createCategoryFor(TEST_USER_EMAIL);
        data.setName("Some new name");
        String categoryJson = json(data);

        mockMvc.perform(put(String.format("%s/%d", CATEGORIES_API_URL, category.getId()))
                .contentType(contentType)
                .content(categoryJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void updateNonexistentCategory() throws Exception {
        Category data = createCategoryFor(user);
        data.setName("Some new fancy name");
        String categoryJson = json(data);

        mockMvc.perform(put(String.format("%s/%d", CATEGORIES_API_URL, NONEXISTENT_ENTITY_ID))
                .contentType(contentType)
                .content(categoryJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(BAD_GUY_EMAIL)
    public void updateCategoryOfSomeoneElse() throws Exception {
        category.setName("Some new name");
        String categoryJson = json(category);

        mockMvc.perform(put(String.format("%s/%d", CATEGORIES_API_URL, category.getId()))
                .contentType(contentType)
                .content(categoryJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void deleteCategory() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", CATEGORIES_API_URL, category.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(TEST_USER_EMAIL)
    public void deleteNonExistentCategory() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", CATEGORIES_API_URL, NONEXISTENT_ENTITY_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(BAD_GUY_EMAIL)
    public void deleteCategoryOfSomeoneElse() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", CATEGORIES_API_URL, category.getId())))
                .andExpect(status().isForbidden());
    }
}