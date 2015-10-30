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
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    @Inject
    private TransactionRepository transactionRepository;

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private UserRepository userRepository;

    @PreAuthorize("#transaction.user.email == authentication.name or hasRole('ADMIN')")
    public void delete(@P("transaction") Transaction transaction) {
        resetTransaction(transaction);
        transactionRepository.delete(transaction);
    }

    @PostAuthorize("returnObject.user.email == authentication.name or hasRole('ADMIN')")
    public Transaction findOne(Long id) {
        return transactionRepository.findOne(id);
    }

    public List<Transaction> findAllByUserEmail(String email) {
        return transactionRepository.findAllByUserEmail(email, new Sort(Direction.DESC, "date"));
    }

    public List<Transaction> findAllByCategory(Category category) {
        return transactionRepository.findAllByCategory(category);
    }

    public List<Transaction> findAllByAccount(Account account) {
        return transactionRepository.findAllByAccountOrTransferAccount(account, account);
    }

    public List<Transaction> findAllByForm(String email, ShowTransactionForm form) {
        String sortBy = form.getSortBy().toString().toLowerCase();
        Sort sort = new Sort(Direction.DESC, sortBy);

        if (form.getComment() != null && !form.getComment().isEmpty()) {
            return transactionRepository.findByUserEmailAndDateBetweenAndCommentLikeIgnoreCase(email,
                    form.getFromDate(), form.getToDate(), form.getComment(), sort);
        }
        return transactionRepository.findByUserEmailAndDateBetween(email, form.getFromDate(), form.getToDate(), sort);
    }

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

    public long countTransactionsByCategoryId(int id) {
        return transactionRepository.countByCategoryId(id);
    }

    public long countTransactionsByAccount(Account account) {
        return transactionRepository.countByAccountOrTransferAccount(account, account);
    }

    public void save(Transaction transaction, String email) {
        User user = userRepository.findOneByEmail(email);
        transaction.setUser(user);
        save(transaction);
        processTransaction(transaction);
    }

    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public void editAndSave(Transaction changedTransaction, Transaction originalTransaction) {
        resetTransaction(originalTransaction);
        originalTransaction.copy(changedTransaction);
        save(originalTransaction);
        processTransaction(originalTransaction);
    }

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
