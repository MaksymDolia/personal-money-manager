package me.dolia.pmm.controller;

import org.junit.Before;
import org.junit.Test;
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

/**
 * Test cases for Category Controller.
 *
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
public class CategoryControllerTest {

    private static final String ROOT_MAPPING = "/app/categories";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy filterChainProxy;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilter(filterChainProxy).build();
    }

    @Test
    public void testCategories() throws Exception {

    }

    @Test
    public void testAddCategory() throws Exception {

    }

    @Test
    public void testRemoveCategory() throws Exception {

    }

    @Test
    public void testEditCategory() throws Exception {

    }

    @Test
    public void testEditCategory1() throws Exception {

    }

    @Test
    public void testTransferTransactions() throws Exception {

    }
}