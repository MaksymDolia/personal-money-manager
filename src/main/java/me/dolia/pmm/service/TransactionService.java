package me.dolia.pmm.service;

import me.dolia.pmm.entity.*;
import me.dolia.pmm.form.ShowTransactionForm;
import me.dolia.pmm.repository.AccountRepository;
import me.dolia.pmm.repository.TransactionRepository;
import me.dolia.pmm.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service to deal with user's transactions.
 *
 * @author Maksym Dolia
 */
@Named
public class TransactionService {

    @Inject
    private TransactionRepository transactionRepository;

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private UserRepository userRepository;

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
        return transactionRepository.findOne(id);
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
     * @param form  data from 'ShowTransactions' web form
     * @return list of transactions
     */
    public List<Transaction> findAllByForm(String email, ShowTransactionForm form) {
        String sortBy = form.getSortBy().toString().toLowerCase();
        Sort sort = new Sort(Direction.DESC, sortBy);

        if (form.getComment() != null && !form.getComment().isEmpty()) {
            return transactionRepository.findByUserEmailAndDateBetweenAndCommentLikeIgnoreCase(email,
                    form.getFromDate(), form.getToDate(), form.getComment(), sort);
        }
        return transactionRepository.findByUserEmailAndDateBetween(email, form.getFromDate(), form.getToDate(), sort);
    }

    /**
     * Looks for all transactions by account and data from 'ShowTransactions' web form.
     *
     * @param account given account
     * @param form    data from 'ShowTransactions' web form
     * @return list of transactions
     */
    @PreAuthorize("#account.user.email == authentication.name or hasRole('ADMIN')")
    public List<Transaction> findAllByAccountAndForm(@P("account") Account account, ShowTransactionForm form) {
        String sortBy = form.getSortBy().toString().toLowerCase();
        Sort sort = new Sort(Direction.DESC, sortBy);

        if (form.getComment() != null && !form.getComment().isEmpty()) {
            return transactionRepository.findByAccountOrTransferAccountAndDateBetweenAndCommentLikeIgnoreCase(account,
                    account, form.getFromDate(), form.getToDate(), form.getComment(), sort);
        }
        return transactionRepository.findByAccountOrTransferAccountAndDateBetween(account, account, form.getFromDate(),
                form.getToDate(), sort);
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
     * @param email       user's email
     */
    public void save(Transaction transaction, String email) {
        User user = userRepository.findOneByEmail(email);
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
     * @param changedTransaction  new  transaction's data
     * @param originalTransaction transaction to be changed
     */
    public void editAndSave(Transaction changedTransaction, Transaction originalTransaction) {
        this.resetTransaction(originalTransaction);
        originalTransaction.copy(changedTransaction);
        save(originalTransaction);
        processTransaction(originalTransaction);
    }

    /* Performs all necessary operation to persist transaction */
    @Transactional
    private void processTransaction(Transaction transaction) {
        Account account = accountRepository.findOne(transaction.getAccount().getId());
        BigDecimal amount = transaction.getAmount();
        Operation operation = transaction.getOperation();

        switch (operation) {

            case EXPENSE:
                account.setAmount(account.getAmount().subtract(amount));
                break;

            case INCOME:
                account.setAmount(account.getAmount().add(amount));
                break;

            case TRANSFER:
                Account accountTo = accountRepository.findOne(transaction.getTransferAccount().getId());
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
        Account account = accountRepository.findOne(transaction.getAccount().getId());
        BigDecimal amount = transaction.getAmount();
        Operation operation = transaction.getOperation();
        switch (operation) {

            case EXPENSE:
                account.setAmount(account.getAmount().add(amount));
                break;

            case INCOME:
                account.setAmount(account.getAmount().subtract(amount));
                break;

            case TRANSFER:
                Account accountTo = accountRepository.findOne(transaction.getTransferAccount().getId());
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
