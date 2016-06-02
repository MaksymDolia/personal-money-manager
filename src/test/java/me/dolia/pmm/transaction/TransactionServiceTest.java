package me.dolia.pmm.transaction;

import me.dolia.pmm.account.Account;
import me.dolia.pmm.account.AccountService;
import me.dolia.pmm.domain.Operation;
import me.dolia.pmm.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Currency;

import static me.dolia.pmm.util.TestUtils.createUser;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

    private static final String EMAIL = "some_test@email.com";
    private static final User USER = createUser(EMAIL);
    private static final long ID = 15L;

    @Mock private Account toAccount;
    @Mock private Account fromAccount;
    @Spy private Transaction transaction;
    @Mock private TransactionRepository repository;
    @Mock private AccountService accountService;
    @InjectMocks private TransactionService transactionService;

    @Before
    public void setUp() throws Exception {
        Currency usd = Currency.getInstance("USD");
        transaction.setId(ID);
        transaction.setFromCurrency(usd);
        transaction.setToCurrency(usd);
        transaction.setToAccount(toAccount);
        transaction.setFromAccount(fromAccount);
        transaction.setOperation(Operation.TRANSFER);
    }

    @Test
    public void readAllTransactions() throws Exception {
        int page = 0;
        int size = 20;

        transactionService.findAll(USER, page, size);

        verify(repository, only()).findAllByUser(eq(USER), any());
    }

    @Test
    public void readSingleTransaction() throws Exception {
        when(repository.findOne(ID)).thenReturn(transaction);

        Transaction result = transactionService.findOne(ID);

        assertThat(result, is(transaction));
    }

    @Test
    public void saveTransaction() throws Exception {
        Transaction saved = Mockito.mock(Transaction.class);
        when(repository.save(transaction)).thenReturn(saved);

        Transaction result = transactionService.save(transaction);

        assertThat(result, is(saved));
        verify(transaction, times(1)).process();
    }

    @Test
    public void delete() throws Exception {
        transactionService.delete(transaction);

        verify(transaction, times(1)).cancel();
        verify(repository, times(1)).delete(transaction);
    }
}