package me.dolia.pmm.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entity class, represents user's role.
 *
 * @author Maksym Dolia
 */
@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Role {

  @Id
  @GeneratedValue
  private Integer id;

  private String name;

  @ManyToMany(mappedBy = "roles")
  private List<User> users = new ArrayList<>();

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users.clear();
    this.users.addAll(users);
  }
}
