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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Entity class, represents finance transaction.
 *
 * @author Maksym Dolia
 */
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Transaction {

  @Id
  @GeneratedValue
  private Long id;

  private Operation operation;
  private BigDecimal amount;
  private Currency currency;

  @ManyToOne
  private Account account;

  // TRANSFER FIELDS
  private BigDecimal transferAmount;
  private Currency transferCurrency;

  @ManyToOne
  private Account transferAccount;

  @Temporal(TemporalType.DATE)
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date date = new Date();
  private String comment;

  @OneToOne
  private Category category;

  @ManyToOne
  private User user;

  public Transaction(Transaction t) {
    this.id = t.id;
    this.operation = t.operation;
    this.amount = t.amount;
    this.currency = t.currency;
    this.account = t.account;
    this.transferAmount = t.transferAmount;
    this.transferCurrency = t.transferCurrency;
    this.transferAccount = t.transferAccount;
    this.date = t.date;
    this.comment = t.comment;
    this.category = t.category;
    this.user = t.user;
  }
}
