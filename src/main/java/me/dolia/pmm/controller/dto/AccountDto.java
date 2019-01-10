package me.dolia.pmm.controller.dto;

import java.math.BigDecimal;
import java.util.Currency;
import lombok.Data;

@Data
public class AccountDto {

  private Integer id;
  private String name;
  private Currency currency;
  private BigDecimal amount;
}
