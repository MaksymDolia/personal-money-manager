package me.dolia.pmm.entity;

import java.math.BigDecimal;
import java.util.Currency;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Entity class, represents user's finance account, like wallet, deposit etc.
 *
 * @author Maksym Dolia
 */
@Entity
public class Account {

  @Id
  @GeneratedValue
  private int id;

  @Size(max = 255)
  @NotBlank
  private String name;
  private Currency currency;

  //    @Digits(fraction = 2, integer = 9)    // issue with binding error result
  private BigDecimal amount;

  @ManyToOne
  @JoinColumn(name = "User_id", referencedColumnName = "id")
  private User user;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User param) {
    this.user = param;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Account other = (Account) obj;
    return id == other.id;
  }
}
