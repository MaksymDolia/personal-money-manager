package me.dolia.pmm.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.entity.User;
import me.dolia.pmm.repository.AccountRepository;
import me.dolia.pmm.repository.TransactionRepository;
import me.dolia.pmm.repository.UserRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public void save(Account account) {
        accountRepository.save(account);
    }

    public void save(Account account, String email) {
        User user = userRepository.findOneByEmail(email);
        account.setUser(user);
        accountRepository.save(account);
    }

    @PostAuthorize("returnObject.user.email == authentication.name or hasRole('ADMIN')")
    public Account findOne(int id) {
        return accountRepository.findOne(id);
    }

    public Double getBalance(String email) {
        return accountRepository.getSumAmountByUserEmail(email);
    }

    @PreAuthorize("#account.user.email == authentication.name or hasRole('ADMIN')")
    public void delete(@P("account") Account account) {
        accountRepository.delete(account);
    }

    public void transferTransactions(int fromId, int toId) {
        Account fromAccount = findOne(fromId);
        Account toAccount = findOne(toId);
        if (fromAccount != null || toAccount != null) {
            transferTransactions(fromAccount, toAccount);
        }

    }

    public void editAccount(Account existingAccount, Account account) {
        existingAccount.setAmount(account.getAmount());
        existingAccount.setCurrency(account.getCurrency());
        existingAccount.setName(account.getName());
        save(existingAccount);
    }

    @PreAuthorize("#fromAccount.user.email == authentication.name or #toAccount.user.email == authentication.name or hasRole('ADMIN')")
    private void transferTransactions(@P("fromAccount") Account fromAccount, @P("toAccount") Account toAccount) {
        List<Transaction> transactions = transactionRepository.findAllByAccountOrTransferAccount(fromAccount,
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

    public List<Account> findAllByUserEmail(String email) {
        return accountRepository.findAllByUserEmail(email);
    }

}
