package me.dolia.pmm.controller.dto;

import java.math.BigDecimal;
import java.util.Currency;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import me.dolia.pmm.persistence.entity.Account;

@Data
public class AccountDto {

  private Integer id;

  @Size(max = 255)
  @NotBlank
  private String name;
  @NotNull
  private Currency currency;
  private BigDecimal amount;

  public Account toAccount() {
    Account account = new Account();
    account.setId(id);
    account.setName(name);
    account.setCurrency(currency);
    account.setAmount(amount);
    return account;
  }

  public static AccountDto fromAccount(Account account) {
    AccountDto dto = new AccountDto();
    dto.setId(account.getId());
    dto.setName(account.getName());
    dto.setCurrency(account.getCurrency());
    dto.setAmount(account.getAmount());
    return dto;
  }
}
