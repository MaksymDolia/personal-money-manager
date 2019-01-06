package me.dolia.pmm.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Entity class, represents category of user's operations, like 'Food', 'Health' etc.
 *
 * @author Maksym Dolia
 */
@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Category {

  @Id
  @GeneratedValue
  private int id;

  @Size(max = 255)
  @NotBlank
  private String name;

  @NotNull
  private Operation operation;

  @ManyToOne
  private User user;
}
