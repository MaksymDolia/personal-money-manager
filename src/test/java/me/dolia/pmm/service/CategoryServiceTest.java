package me.dolia.pmm.service;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.repository.CategoryRepository;
import me.dolia.pmm.repository.TransactionRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private UserService userService;

  @Mock
  private TransactionRepository transactionRepository;

  @InjectMocks
  private CategoryService categoryService;

  @Test
  public void getCategory() {
    int id = 1;
    Category category = Category.builder().id(id).name("test category").build();
    when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

    Category result = categoryService.getCategory(id);

    assertThat(result).isSameAs(category);
  }

  @Test
  public void throwExceptionIfCategoryDoesNotExist() {
    thrown.expect(NotFoundException.class);
    when(categoryRepository.findById(1)).thenReturn(Optional.empty());

    categoryService.getCategory(1);
  }
}