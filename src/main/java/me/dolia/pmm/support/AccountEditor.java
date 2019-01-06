package me.dolia.pmm.support;

import java.beans.PropertyEditorSupport;
import lombok.RequiredArgsConstructor;
import me.dolia.pmm.entity.Account;
import me.dolia.pmm.service.AccountService;

/**
 * Property editor for account.
 *
 * @author Maksym Dolia
 */
@RequiredArgsConstructor
public class AccountEditor extends PropertyEditorSupport {

  private final AccountService accountService;

  @Override
  public void setAsText(String text) {
    if (text == null || text.isEmpty()) {
      setValue(null);
      return;
    }
    Account account = accountService.findOne(Integer.parseInt(text));
    setValue(account);
  }

}
