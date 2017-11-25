package me.dolia.pmm.support;

import java.beans.PropertyEditorSupport;
import me.dolia.pmm.entity.Account;
import me.dolia.pmm.service.AccountService;

/**
 * Property editor for account.
 *
 * @author Maksym Dolia
 */
public class AccountEditor extends PropertyEditorSupport {

  private final AccountService accountService;

  public AccountEditor(AccountService accountService) {
    this.accountService = accountService;
  }

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    if (text == null || text.isEmpty()) {
      setValue(null);
      return;
    }
    Account account = accountService.findOne(Integer.parseInt(text));
    setValue(account);
  }

}
