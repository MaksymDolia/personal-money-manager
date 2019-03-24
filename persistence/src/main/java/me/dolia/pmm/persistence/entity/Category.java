package me.dolia.pmm.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
  private Integer id;
  private String name;
  private Operation operation;
  @ManyToOne
  private User user;
}
