package me.dolia.pmm.account;

import me.dolia.pmm.service.NotFoundException;
import me.dolia.pmm.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service to manage {@link Account} instances.
 */
@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @PreAuthorize("#user.email == authentication.name")
    public List<Account> findAll(User user) {
        return accountRepository.findAllByUser(user);
    }

    @PostAuthorize("returnObject.user.email == authentication.name")
    public Account findBy(long id) {
        Account account = accountRepository.findOne(id);
        if (account == null) throw new NotFoundException(Account.class, "id", String.valueOf(id));
        return account;
    }

    @PostAuthorize("returnObject.user.email == authentication.name")
    public Account save(Account account) {
        if (account.getId() != null) {
            Account stored = findBy(account.getId());
            account.setUser(stored.getUser());
        }
        return accountRepository.save(account);
    }

    @PreAuthorize("#account.user.email == authentication.name")
    public void delete(Account account) {
        accountRepository.delete(account);
    }

    @PreAuthorize("#user.email == authentication.name")
    public BigDecimal calculateBalance(User user) {
        return accountRepository.calculateBalance(user);
    }
}