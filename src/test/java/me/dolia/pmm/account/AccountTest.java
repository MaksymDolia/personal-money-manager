package me.dolia.pmm.account;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class AccountTest {

    private static final BigDecimal INITIAL_BALANCE = BigDecimal.TEN;
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(3.45);
    private static final BigDecimal BALANCE_AFTER_CHARGE = BigDecimal.valueOf(6.55);
    private static final BigDecimal BALANCE_AFTER_DEPOSIT = BigDecimal.valueOf(13.45);

    private Account account;

    @Before
    public void setUp() throws Exception {
        account = new Account();
        account.setBalance(INITIAL_BALANCE);
    }

    @Test
    public void testCharge() throws Exception {
        account.charge(AMOUNT);
        BigDecimal balance = account.getBalance();

        assertThat(balance, is(BALANCE_AFTER_CHARGE));
    }

    @Test
    public void testDeposit() throws Exception {
        account.deposit(AMOUNT);
        BigDecimal balance = account.getBalance();

        assertThat(balance, is(BALANCE_AFTER_DEPOSIT));
    }
}