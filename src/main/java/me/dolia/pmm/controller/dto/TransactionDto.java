package me.dolia.pmm.controller.dto;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import lombok.Data;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Transaction;

@Data
public class TransactionDto {

  private Long id;

  @NotNull
  private String operation;
  @Digits(fraction = 2, integer = 9)
  private BigDecimal amount;
  @NotNull
  private Currency currency;

  private AccountDto account;

  // TRANSFER FIELDS
  @NotNull
  private Currency transferCurrency;

  private AccountDto transferAccount;

  private Date date = new Date();
  private String comment;

  private CategoryDto category;

  public Transaction toTransaction() {
    Transaction t = new Transaction();
    t.setId(id);
    t.setOperation(Operation.valueOf(operation));
    t.setAmount(amount);
    t.setCurrency(currency);
    t.setAccount(account.toAccount());
    t.setTransferCurrency(transferCurrency);
    t.setTransferAccount(transferAccount.toAccount());
    t.setDate(date);
    t.setComment(comment);
    t.setCategory(category.toCategory());
    return t;
  }

  public static TransactionDto fromTransaction(Transaction transaction) {
    TransactionDto dto = new TransactionDto();
    dto.setId(transaction.getId());
    dto.setOperation(transaction.getOperation().name());
    dto.setAmount(transaction.getAmount());
    dto.setCurrency(transaction.getCurrency());
    dto.setAccount(AccountDto.fromAccount(transaction.getAccount()));
    dto.setTransferCurrency(transaction.getTransferCurrency());
    dto.setTransferAccount(AccountDto.fromAccount(transaction.getTransferAccount()));
    dto.setDate(transaction.getDate());
    dto.setComment(transaction.getComment());
    dto.setCategory(CategoryDto.fromCategory(transaction.getCategory()));
    return dto;
  }
}
