package me.dolia.pmm.support;

import java.beans.PropertyEditorSupport;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.service.CategoryService;

/**
 * Property editor for account.
 *
 * @author Maksym Dolia
 */
public class CategoryEditor extends PropertyEditorSupport {

  private final CategoryService categoryService;

  public CategoryEditor(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    if (text == null || text.isEmpty()) {
      setValue(null);
      return;
    }
    Category category = categoryService.findOne(Integer.valueOf(text));
    setValue(category);
  }

}
