package me.dolia.pmm.transaction;

import me.dolia.pmm.account.Account;
import me.dolia.pmm.domain.Operation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionTest {

    private static final BigDecimal AMOUNT = BigDecimal.valueOf(12.45);

    @Mock private Account fromAccount;
    @Mock private Account toAccount;
    @InjectMocks private Transaction transaction;

    @Test
    public void processExpense() throws Exception {
        transaction.setOperation(Operation.EXPENSE);
        transaction.setFromAmount(AMOUNT);

        transaction.process();

        verify(fromAccount, only()).charge(AMOUNT);
    }

    @Test
    public void processTransfer() throws Exception {
        transaction.setOperation(Operation.TRANSFER);
        transaction.setFromAmount(AMOUNT);
        transaction.setToAmount(AMOUNT);

        transaction.process();

        verify(fromAccount, times(1)).charge(AMOUNT);
        verify(toAccount, times(1)).deposit(AMOUNT);
    }

    @Test
    public void processIncome() throws Exception {
        transaction.setOperation(Operation.INCOME);
        transaction.setToAmount(AMOUNT);

        transaction.process();

        verify(toAccount, only()).deposit(AMOUNT);
    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionWhenProcessTransactionWithUndefinedOperation() throws Exception {
        transaction.process();
    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionWhenTryToProcessTransactionTwice() throws Exception {
        transaction.process();
        transaction.process();
    }

    @Test
    public void cancelExpense() throws Exception {
        transaction.setOperation(Operation.EXPENSE);
        transaction.setFromAmount(AMOUNT);

        transaction.cancel();

        verify(fromAccount, only()).deposit(AMOUNT);
    }

    @Test
    public void cancelTransfer() throws Exception {
        transaction.setOperation(Operation.TRANSFER);
        transaction.setFromAmount(AMOUNT);
        transaction.setToAmount(AMOUNT);

        transaction.cancel();

        verify(fromAccount, times(1)).deposit(AMOUNT);
        verify(toAccount, times(1)).charge(AMOUNT);
    }

    @Test
    public void cancelIncome() throws Exception {
        transaction.setOperation(Operation.INCOME);
        transaction.setToAmount(AMOUNT);

        transaction.cancel();

        verify(toAccount, times(1)).charge(AMOUNT);
    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionWhenCancelTransactionWithUndefinedOperation() throws Exception {
        transaction.process();
    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionWhenTryToCancelTransactionTwice() throws Exception {
        transaction.cancel();
        transaction.cancel();
    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionWhenProcessAndOperationIsNotSet() throws Exception {
        transaction.setOperation(null);
        transaction.process();
    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionWhenCancelAndOperationIsNotSet() throws Exception {
        transaction.setOperation(null);
        transaction.cancel();
    }
}