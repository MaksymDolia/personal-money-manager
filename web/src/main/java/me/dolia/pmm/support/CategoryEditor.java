package me.dolia.pmm.support;

import java.beans.PropertyEditorSupport;
import lombok.RequiredArgsConstructor;
import me.dolia.pmm.service.CategoryService;
import org.springframework.util.StringUtils;

/**
 * Property editor for account.
 *
 * @author Maksym Dolia
 */
@RequiredArgsConstructor
public class CategoryEditor extends PropertyEditorSupport {

  private final CategoryService categoryService;

  @Override
  public void setAsText(String text) {
    if (StringUtils.isEmpty(text)) {
      setValue(null);
      return;
    }
    var category = categoryService.findOne(Integer.valueOf(text));
    setValue(category);
  }

}
