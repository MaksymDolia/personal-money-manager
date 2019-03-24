package me.dolia.pmm.persistence.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entity class, represents user of application.
 *
 * @author Maksym Dolia
 */
@Entity
@Table(name = "app_user")
@Data
@EqualsAndHashCode(of = "email")
@ToString(of = "email")
public class User {

  @Email
  @Size(min = 1, max = 255)
  @Id
  private String email;

  @Size(min = 5, max = 255)
  @NotBlank
  private String password;

  private boolean enabled;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Account> accounts;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Transaction> transactions;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Category> categories;

  @ManyToMany
  @JoinTable
  private List<Role> roles;
}
