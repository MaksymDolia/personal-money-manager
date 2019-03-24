package me.dolia.pmm.service;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.dolia.pmm.persistence.entity.Account;
import me.dolia.pmm.persistence.entity.Operation;
import me.dolia.pmm.persistence.entity.Transaction;
import me.dolia.pmm.persistence.entity.User;
import me.dolia.pmm.persistence.repository.AccountRepository;
import me.dolia.pmm.persistence.repository.TransactionRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

/**
 * Service to deal with accounts.
 *
 * @author Maksym Dolia
 */
@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserService userService;
  private final TransactionRepository transactionRepository;

  /**
   * Saves given account in database.
   *
   * @param account account
   */
  public void save(Account account) {
    accountRepository.save(account);
  }

  /**
   * Saves given account in database and bind it to the user with given email.
   *
   * @param account account to store
   * @param email user's email
   */
  public void save(Account account, String email) {
    User user = userService.findOneByEmail(email);
    account.setUser(user);
    accountRepository.save(account);
  }

  /**
   * Looks and return the account by given id. Also checks if the account belongs to user who's
   * asking or the user has role 'ADMIN'.
   *
   * @param id account's id
   * @return account
   */
  @PostAuthorize("returnObject.user.email == authentication.name or hasRole('ADMIN')")
  public Account findOne(int id) {
    return accountRepository.findById(id).orElseThrow(
        () -> new NotFoundException(String.format("Account with ID %d was not found", id)));
  }

  /**
   * Returns total balance of all user's accounts.
   *
   * @param email user's email
   * @return total balance
   */
  public Double getBalance(String email) {
    return accountRepository.getSumAmountByUserEmail(email);
  }

  /**
   * Deletes account by given id. Also checks if the account belongs to user who's asking or the
   * user has role 'ADMIN'.
   *
   * @param account account to be deleted
   */
  @PreAuthorize("#account.user.email == authentication.name or hasRole('ADMIN')")
  public void delete(@P("account") Account account) {
    accountRepository.delete(account);
  }

  /**
   * Transfer transactions from one account to another.
   *
   * @param fromId id of account transaction will be moved from
   * @param toId id of account transaction will be moved to
   */
  public void transferTransactions(int fromId, int toId) {
    Account fromAccount = findOne(fromId);
    Account toAccount = findOne(toId);
    if (fromAccount != null && toAccount != null) {
      transferTransactions(fromAccount, toAccount);
    }

  }

  /**
   * Updates existing account with new data.
   *
   * @param existingAccount existing account
   * @param accountNewData new account data
   */
  public void editAccount(Account existingAccount, Account accountNewData) {
    existingAccount.setAmount(accountNewData.getAmount());
    existingAccount.setCurrency(accountNewData.getCurrency());
    existingAccount.setName(accountNewData.getName());
    save(existingAccount);
  }

  /**
   * Looks for all transactions by given user's email.
   *
   * @param email user's email
   * @return list of transactions
   */
  public List<Account> findAllByUserEmail(String email) {
    return accountRepository.findAllByUserEmail(email);
  }

  /* Helper method to performs all necessary actions to transfer transactions between accounts */
  @PreAuthorize("#fromAccount.user.email == authentication.name or #toAccount.user.email == authentication.name or hasRole('ADMIN')")
  private void transferTransactions(@P("fromAccount") Account fromAccount,
      @P("toAccount") Account toAccount) {
    List<Transaction> transactions = transactionRepository
        .findAllByAccountOrTransferAccount(fromAccount,
            fromAccount);
    if (transactions != null && !transactions.isEmpty()) {
      for (Transaction transaction : transactions) {
        Operation operation = transaction.getOperation();
        BigDecimal amount = transaction.getAmount();

        switch (operation) {
          case EXPENSE:
            fromAccount.setAmount(fromAccount.getAmount().add(amount));
            toAccount.setAmount(toAccount.getAmount().subtract(amount));
            transaction.setAccount(toAccount);
            break;
          case INCOME:
            fromAccount.setAmount(fromAccount.getAmount().subtract(amount));
            toAccount.setAmount(toAccount.getAmount().add(amount));
            transaction.setAccount(toAccount);
            break;
          case TRANSFER:
            if (transaction.getAccount().getId() == fromAccount.getId()) {
              fromAccount.setAmount(fromAccount.getAmount().add(amount));
              toAccount.setAmount(toAccount.getAmount().subtract(amount));
              transaction.setAccount(toAccount);
            } else if (transaction.getTransferAccount().getId() == fromAccount.getId()) {
              fromAccount.setAmount(fromAccount.getAmount().subtract(amount));
              toAccount.setAmount(toAccount.getAmount().add(amount));
              transaction.setTransferAccount(fromAccount);
            }
            break;
          default:
            break;
        }

        transactionRepository.save(transaction);
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
      }
    }
  }
}
