package me.dolia.pmm.web;

import me.dolia.pmm.account.Account;
import me.dolia.pmm.account.AccountService;
import me.dolia.pmm.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.dolia.pmm.web.AccountController.ACCOUNTS_API_URL;
import static org.springframework.web.bind.annotation.RequestMethod.*;


@RestController
@RequestMapping(ACCOUNTS_API_URL)
public class AccountController {

    static final String ACCOUNTS_API_URL = "/accounts";

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @ModelAttribute
    public Account loadAccount(@PathVariable Optional<Long> id) {
        if (id.isPresent()) return accountService.findBy(id.get());
        return null;
    }

    @RequestMapping(method = GET)
    public List<Account> readAccounts(@AuthenticationPrincipal User user) {
        return accountService.findAll(user);
    }

    @RequestMapping(value = "/{id}", method = GET)
    public Account readAccount(@ModelAttribute Account account) {
        return account;
    }

    @RequestMapping(method = POST)
    public ResponseEntity createAccount(@AuthenticationPrincipal User user, @RequestBody Account data) {
        data.setUser(user);
        Account account = accountService.save(data);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path(account.getId().toString())
                .build()
                .toUri()
        ).body(account);
    }

    @RequestMapping(value = "/{id}", method = PUT)
    public void updateAccount(@RequestBody Account data, @PathVariable long id) {
        data.setId(id);
//        accountService.update(data);
        accountService.save(data);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity deleteAccount(@ModelAttribute Account account) {
        accountService.delete(account);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/balance", method = GET)
    public Map<String, BigDecimal> balance(@AuthenticationPrincipal User user) {
        BigDecimal balance = accountService.calculateBalance(user);
        Map<String, BigDecimal> map = new HashMap<>();
        map.put("balance", balance);
        return map;
    }
}