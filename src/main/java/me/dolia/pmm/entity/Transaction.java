package me.dolia.pmm.entity;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Entity class, represents finance transaction.
 *
 * @author Maksym Dolia
 */
@Entity
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Transaction {

  @Id
  @GeneratedValue
  private long id;

  @NotNull
  private Operation operation;
  @Digits(fraction = 2, integer = 9)
  private BigDecimal amount;
  private Currency currency;

  @ManyToOne
  private Account account;

  // TRANSFER FIELDS
  @Digits(fraction = 2, integer = 9)
  private BigDecimal transferAmount;
  private Currency transferCurrency;

  @ManyToOne
  private Account transferAccount;

  @Temporal(TemporalType.DATE)
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @Builder.Default
  private Date date = new Date();
  private String comment;

  @OneToOne
  private Category category;

  @ManyToOne
  private User user;

  public Transaction() {
    //for JPA
  }
}
