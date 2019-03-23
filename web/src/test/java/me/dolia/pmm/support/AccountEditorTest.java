package me.dolia.pmm.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountEditorTest {

  @Mock
  private AccountService accountService;

  @InjectMocks
  private AccountEditor editor;

  @Test
  public void setAccountAsText() {
    Account account = new Account();
    account.setId(1);
    when(accountService.findOne(account.getId())).thenReturn(account);

    editor.setAsText("1");

    assertThat(editor.getValue()).isSameAs(account);
  }

  @Test
  public void setNullIfTextIsEmpty() {
    editor.setAsText(null);

    assertThat(editor.getAsText()).isNull();

    editor.setAsText("");

    assertThat(editor.getAsText()).isNull();
  }
}