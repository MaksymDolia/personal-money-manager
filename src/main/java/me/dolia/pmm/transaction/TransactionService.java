package me.dolia.pmm.transaction;

import me.dolia.pmm.account.AccountService;
import me.dolia.pmm.domain.Operation;
import me.dolia.pmm.service.NotFoundException;
import me.dolia.pmm.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service to manage {@link Transaction} instances.
 */
@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    @PreAuthorize("#user.email == authentication.name")
    public List<Transaction> findAll(User user, int page, int size) {
        return transactionRepository.findAllByUser(user, new PageRequest(page, size, new Sort(Sort.Direction.DESC, "date")));
    }

    @PostAuthorize("returnObject.user.email == authentication.name")
    public Transaction findOne(long id) {
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction == null) throw new NotFoundException(Transaction.class, "id", String.valueOf(id));
        return transaction;
    }

    @PreAuthorize("#transaction.user.email == authentication.name")
    public Transaction save(Transaction transaction) {
        transaction.process();
        updateAccounts(transaction);
        return transactionRepository.save(transaction);
    }

    @PreAuthorize("#transaction.user.email == authentication.name")
    public void delete(Transaction transaction) {
        transaction.cancel();
        updateAccounts(transaction);
        transactionRepository.delete(transaction);
    }

    @PostAuthorize("returnObject.user.email == authentication.name")
    public Transaction update(Transaction data) {
        Transaction transaction = findOne(data.getId());
        transaction.cancel();
        updateAccounts(transaction);
        data.setUser(transaction.getUser());
        return save(data);
    }

    private void updateAccounts(Transaction transaction) {
        Operation operation = transaction.getOperation();
        switch (operation) {
            case EXPENSE:
                accountService.save(transaction.getFromAccount());
                break;
            case TRANSFER:
                accountService.save(transaction.getFromAccount());
                accountService.save(transaction.getToAccount());
                break;
            case INCOME:
                accountService.save(transaction.getToAccount());
                break;
            default:    // should never happen, but lets be more careful
                throw new RuntimeException("Error. Check in transaction::process does not work.");
        }
    }
}