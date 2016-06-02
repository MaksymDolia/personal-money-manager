package me.dolia.pmm.web;

import me.dolia.pmm.transaction.Transaction;
import me.dolia.pmm.transaction.TransactionService;
import me.dolia.pmm.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static me.dolia.pmm.web.TransactionController.TRANSACTIONS_API_URL;
import static org.springframework.web.bind.annotation.RequestMethod.*;


@RestController
@RequestMapping(TRANSACTIONS_API_URL)
public class TransactionController {

    static final String TRANSACTIONS_API_URL = "/transactions";

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @ModelAttribute
    public Transaction loadTransaction(@PathVariable Optional<Long> id) {
        if (id.isPresent()) return transactionService.findOne(id.get());
        return null;
    }

    @RequestMapping(method = GET)
    public List<Transaction> readTransactions(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        return transactionService.findAll(user, page, size);
    }

    @RequestMapping(value = "/{id}", method = GET)
    public Transaction readTransaction(@ModelAttribute Transaction transaction) {
        return transaction;
    }

    @RequestMapping(method = POST)
    public ResponseEntity createTransaction(@AuthenticationPrincipal User user,
                                            @RequestBody Transaction data) {
        data.setUser(user);
        Transaction transaction = transactionService.save(data);
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequestUri()
                        .path(transaction.getId().toString())
                        .build()
                        .toUri()
        ).body(transaction);
    }

    @RequestMapping(value = "/{id}", method = PUT)
    public void updateTransaction(@PathVariable long id,
                                  @RequestBody Transaction data) {
        data.setId(id);
        transactionService.update(data);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity deleteTransaction(@ModelAttribute Transaction transaction) {
        transactionService.delete(transaction);
        return ResponseEntity.noContent().build();
    }
}