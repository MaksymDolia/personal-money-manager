package me.dolia.pmm.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.dolia.pmm.form.ShowTransactionForm;
import me.dolia.pmm.persistence.entity.Account;
import me.dolia.pmm.persistence.entity.Category;
import me.dolia.pmm.persistence.entity.Transaction;
import me.dolia.pmm.persistence.repository.AccountRepository;
import me.dolia.pmm.persistence.repository.TransactionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Service to deal with user's transactions.
 *
 * @author Maksym Dolia
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;
  private final UserService userService;

  /**
   * Deletes given transaction.
   *
   * @param transaction transaction to be deleted
   */
  @PreAuthorize("#transaction.user.email == authentication.name or hasRole('ADMIN')")
  public void delete(@P("transaction") Transaction transaction) {
    resetTransaction(transaction);
    transactionRepository.delete(transaction);
  }

  /**
   * Returns transaction by given id.
   *
   * @param id transaction's id
   * @return transaction
   */
  @PostAuthorize("returnObject.user.email == authentication.name or hasRole('ADMIN')")
  public Transaction findOne(Long id) {
    return transactionRepository.findById(id).orElseThrow(
        () -> new NotFoundException(String.format("Transaction with ID %d was not found.", id)));
  }

  /**
   * Looks for all transactions by given user's email.
   *
   * @param email user's email
   * @return list of transactions
   */
  public List<Transaction> findAllByUserEmail(String email) {
    return transactionRepository.findAllByUserEmail(email, new Sort(Direction.DESC, "date"));
  }

  /**
   * Looks for all transactions by given category.
   *
   * @param category category
   * @return list of categories
   */
  public List<Transaction> findAllByCategory(Category category) {
    return transactionRepository.findAllByCategory(category);
  }

  /**
   * Looks for all transactions by given account.
   *
   * @param account account
   * @return list of accounts
   */
  public List<Transaction> findAllByAccount(Account account) {
    return transactionRepository.findAllByAccountOrTransferAccount(account, account);
  }

  /**
   * Looks for all transactions by given user's email and data from 'ShowTransactions' web form.
   *
   * @param email user's email
   * @param form data from 'ShowTransactions' web form
   * @return list of transactions
   */
  public List<Transaction> findAllByForm(String email, ShowTransactionForm form) {
    var sortBy = form.getSortBy().toString().toLowerCase();
    var sort = new Sort(Direction.DESC, sortBy);

    var comment = form.getComment();
    var dateFrom = form.getFromDate();
    var dateTo = form.getToDate();
    if (!StringUtils.isEmpty(comment)) {
      return transactionRepository.findByUserEmailAndDateBetweenAndCommentLikeIgnoreCase(email,
          dateFrom, dateTo, comment, sort);
    }
    return transactionRepository
        .findByUserEmailAndDateBetween(email, dateFrom, dateTo, sort);
  }

  /**
   * Looks for all transactions by account and data from 'ShowTransactions' web form.
   *
   * @param account given account
   * @param form data from 'ShowTransactions' web form
   * @return list of transactions
   */
  @PreAuthorize("#account.user.email == authentication.name or hasRole('ADMIN')")
  public List<Transaction> findAllByAccountAndForm(@P("account") Account account,
      ShowTransactionForm form) {
    var sortBy = form.getSortBy().toString().toLowerCase();
    var sort = new Sort(Direction.DESC, sortBy);

    var comment = form.getComment();
    var dateFrom = form.getFromDate();
    var dateTo = form.getToDate();
    if (!StringUtils.isEmpty(comment)) {
      return transactionRepository
          .findByAccountOrTransferAccountAndDateBetweenAndCommentLikeIgnoreCase(account,
              account, dateFrom, dateTo, comment, sort);
    }
    return transactionRepository
        .findByAccountOrTransferAccountAndDateBetween(account, account, dateFrom, dateTo, sort);
  }

  /**
   * Count transactions with given category's id.
   *
   * @param id category's id
   * @return number of found transactions
   */
  public long countTransactionsByCategoryId(int id) {
    return transactionRepository.countByCategoryId(id);
  }

  /**
   * Count transactions by account.
   *
   * @param account given account
   * @return number of transactions
   */
  public long countTransactionsByAccount(Account account) {
    return transactionRepository.countByAccountOrTransferAccount(account, account);
  }

  /**
   * Stores the given transaction and bind it to user with given email.
   *
   * @param transaction transaction to be stored
   * @param email user's email
   */
  @Transactional
  public void save(Transaction transaction, String email) {
    var user = userService.findOneByEmail(email);
    transaction.setUser(user);
    save(transaction);
    processTransaction(transaction);
  }

  /**
   * Stores the given transaction in persistence layer.
   *
   * @param transaction transaction
   */
  public void save(Transaction transaction) {
    transactionRepository.save(transaction);
  }

  /**
   * Edits and saves transaction.
   *
   * @param changedTransaction new  transaction's data
   * @param originalTransaction transaction to be changed
   */
  @Transactional
  public void editAndSave(Transaction changedTransaction, Transaction originalTransaction) {
    this.resetTransaction(originalTransaction);
    originalTransaction = new Transaction(changedTransaction);
    save(originalTransaction);
    processTransaction(originalTransaction);
  }

  /* Performs all necessary operation to persist transaction */
  private void processTransaction(Transaction transaction) {
    var account = accountRepository.findById(transaction.getAccount().getId()).get();
    var amount = transaction.getAmount();
    var operation = transaction.getOperation();

    switch (operation) {

      case EXPENSE:
        account.setAmount(account.getAmount().subtract(amount));
        break;

      case INCOME:
        account.setAmount(account.getAmount().add(amount));
        break;

      case TRANSFER:
        Account accountTo = accountRepository.findById(transaction.getTransferAccount().getId()).get();
        if (accountTo.equals(account)) {
          break;
        }
        account.setAmount(account.getAmount().subtract(amount));
        accountTo.setAmount(accountTo.getAmount().add(amount));
        accountRepository.save(accountTo);
        break;

      default:
        break;
    }
    accountRepository.save(account);
  }

  /*
      Resets transaction. Similar to operation 'undo'.
   */
  private void resetTransaction(Transaction transaction) {
    var account = accountRepository.findById(transaction.getAccount().getId()).get();
    var amount = transaction.getAmount();
    var operation = transaction.getOperation();
    switch (operation) {

      case EXPENSE:
        account.setAmount(account.getAmount().add(amount));
        break;

      case INCOME:
        account.setAmount(account.getAmount().subtract(amount));
        break;

      case TRANSFER:
        Account accountTo = accountRepository.findById(transaction.getTransferAccount().getId()).get();
        if (accountTo.equals(account)) {
          break;
        }
        account.setAmount(account.getAmount().add(amount));
        accountTo.setAmount(accountTo.getAmount().subtract(amount));
        accountRepository.save(accountTo);
        break;

      default:
        break;
    }
    accountRepository.save(account);
  }

}
