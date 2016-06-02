package me.dolia.pmm.account;

import me.dolia.pmm.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;

import static me.dolia.pmm.util.TestUtils.createUser;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    private static final String EMAIL = "some_test@email.com";
    private static final User USER = createUser(EMAIL);
    private static final long ID = 15L;

    @Mock private AccountRepository repository;
    @InjectMocks @Spy private AccountService accountService;
    private Account account;

    @Before
    public void setUp() throws Exception {
        account = new Account();
        account.setBalance(BigDecimal.TEN);
        account.setName("Some account");
        account.setCurrency(Currency.getInstance("USD"));
    }

    @Test
    public void readAllAccounts() throws Exception {
        accountService.findAll(USER);

        verify(repository, only()).findAllByUser(USER);
    }

    @Test
    public void readSingleAccount() throws Exception {
        when(repository.findOne(ID)).thenReturn(account);

        Account result = accountService.findBy(ID);

        assertThat(result, is(account));
    }

    @Test
    public void saveAccount() throws Exception {
        Account savedAccount = new Account();
        savedAccount.setCurrency(account.getCurrency());
        savedAccount.setName(account.getName());
        savedAccount.setBalance(account.getBalance());
        savedAccount.setId(ID);
        when(repository.save(account)).thenReturn(savedAccount);

        Account result = accountService.save(account);

        assertThat(result, is(savedAccount));
    }

    @Test
    public void updateAccount() throws Exception {
        Account data = new Account();
        data.setName("Updated name");
        data.setId(ID);
        account.setId(ID);
        doReturn(account).when(accountService).findBy(ID);

        accountService.save(data);

        assertThat(account.getUser(), is(data.getUser()));
        verify(accountService, times(1)).findBy(ID);
        verify(accountService, times(1)).save(data);
    }

    @Test
    public void deleteAccount() throws Exception {
        accountService.delete(account);

        verify(repository, only()).delete(account);
    }

//    @Test
//    public void calculateBalance() throws Exception {
//        BigDecimal balance = BigDecimal.valueOf(12.45);
//        when(repository.calculateBalance(EMAIL)).thenReturn(balance);
//
//        BigDecimal result = accountService.calculateBalance(EMAIL);
//
//        assertThat(result, is(balance));
//    }
}