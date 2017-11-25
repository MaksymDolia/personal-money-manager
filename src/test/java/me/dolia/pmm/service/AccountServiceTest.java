package me.dolia.pmm.service;

import me.dolia.pmm.repository.AccountRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for {@code AccountService} class.
 *
 * @author Maksym Dolia
 * @since 29.11.2015.
 */
@Ignore("Not yet implemented")
@ActiveProfiles("dev")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext.xml",
})
public class AccountServiceTest {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private AccountService accountService;

  @Autowired
  private InitDbService initDbService;

  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void testSave() throws Exception {

  }

  @Test
  public void testSave1() throws Exception {

  }

  @Test
  public void testFindOne() throws Exception {

  }

  @Test
  public void testGetBalance() throws Exception {

  }

  @Test
  public void testDelete() throws Exception {

  }

  @Test
  public void testTransferTransactions() throws Exception {

  }

  @Test
  public void testEditAccount() throws Exception {

  }

  @Test
  public void testFindAllByUserEmail() throws Exception {

  }
}