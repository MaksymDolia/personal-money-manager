package me.dolia.pmm.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 * Entity class, represents user's role.
 *
 * @author Maksym Dolia
 */
@Entity
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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Role role = (Role) o;

    return !(id != null ? !id.equals(role.id) : role.id != null);

  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
