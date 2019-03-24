package me.dolia.pmm.persistence.entity;

import java.math.BigDecimal;
import java.util.Currency;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entity class, represents user's finance account, like wallet, deposit etc.
 *
 * @author Maksym Dolia
 */
@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
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
  @JoinColumn(name = "User_email", referencedColumnName = "email")
  private User user;
}
