package me.dolia.pmm.category;

import me.dolia.pmm.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static me.dolia.pmm.util.TestUtils.createCategoryFor;
import static me.dolia.pmm.util.TestUtils.createUser;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceTest {

    private static final String EMAIL = "some_test@email.com";
    private static final User USER = createUser(EMAIL);
    private static final long ID = 15L;

    @Mock private CategoryRepository repository;
    @InjectMocks private CategoryService categoryService;
    private Category category;

    @Before
    public void setUp() throws Exception {
        category = createCategoryFor(USER);
    }

    @Test
    public void readAllCategories() throws Exception {
        categoryService.findAll(USER);

        verify(repository, only()).findAllByUser(USER);
    }

    @Test
    public void readSingleCategory() throws Exception {
        when(repository.findOne(ID)).thenReturn(category);

        categoryService.find(ID);

        verify(repository).findOne(ID);
    }

    @Test
    public void saveSingleCategory() throws Exception {
        Category savedCategory = createCategoryFor(USER);
        savedCategory.setId(ID);
        when(repository.save(category)).thenReturn(savedCategory);

        Category result = categoryService.save(category);

        assertThat(result, is(savedCategory));
    }

    @Test
    public void updateSingleCategory() throws Exception {
        Category data = createCategoryFor(USER);
        data.setId(ID);
        when(repository.findOne(ID)).thenReturn(category);
        when(repository.save(category)).thenReturn(category);

        Category result = categoryService.update(data);

        assertThat(result, is(category));
        assertThat(result.getUser(), is(category.getUser()));
        assertThat(result.getName(), is(data.getName()));
        assertThat(result.getOperation(), is(data.getOperation()));
    }

    @Test
    public void deleteSingleCategory() throws Exception {
        categoryService.delete(category);

        verify(repository, only()).delete(category);
    }
}