package me.dolia.pmm.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDto {

  private Integer id;

  @Size(max = 255)
  @NotBlank
  private String name;

  @NotNull
  private String operation;
}
