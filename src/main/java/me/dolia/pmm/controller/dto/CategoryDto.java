package me.dolia.pmm.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Operation;

@Data
public class CategoryDto {

  private Integer id;

  @Size(max = 255)
  @NotBlank
  private String name;

  @NotNull
  private String operation;

  public Category toCategory() {
    Category category = new Category();
    category.setId(id);
    category.setName(name);
    category.setOperation(Operation.valueOf(operation));
    return category;
  }

  public static CategoryDto fromCategory(Category category) {
    CategoryDto dto = new CategoryDto();
    dto.setId(category.getId());
    dto.setName(category.getName());
    dto.setOperation(category.getOperation().name());
    return dto;
  }
}
