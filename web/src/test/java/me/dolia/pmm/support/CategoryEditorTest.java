package me.dolia.pmm.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import me.dolia.pmm.persistence.entity.Category;
import me.dolia.pmm.service.CategoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CategoryEditorTest {

  @Mock
  private CategoryService categoryService;

  @InjectMocks
  private CategoryEditor editor;

  @Test
  public void setCategoryAsText() {
    var category = new Category();
    category.setId(1);
    when(categoryService.findOne(category.getId())).thenReturn(category);

    editor.setAsText("1");

    assertThat(editor.getValue()).isSameAs(category);
  }

  @Test
  public void setNullIfTextIsEmpty() {
    editor.setAsText(null);

    assertThat(editor.getAsText()).isNull();

    editor.setAsText("");

    assertThat(editor.getAsText()).isNull();
  }
}