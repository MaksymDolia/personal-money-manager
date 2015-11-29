package me.dolia.pmm.controller;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.service.AccountService;
import me.dolia.pmm.service.CategoryService;
import me.dolia.pmm.service.TransactionService;
import me.dolia.pmm.support.AccountEditor;
import me.dolia.pmm.support.CategoryEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Controller, handles requests to user's dashboard.
 *
 * @author Maksym Dolia
 */
@Controller
@RequestMapping("/app")
public class DashboardController {

    @Inject
    private AccountService accountService;

    @Inject
    private CategoryService categoryService;

    @Inject
    private TransactionService transactionService;

    @ModelAttribute("account")
    public Account createAccount() {
        return new Account();
    }

    @ModelAttribute("transaction")
    public Transaction createTransaction() {
        return new Transaction();
    }

    @InitBinder
    protected void initBinder(ServletRequestDataBinder binder) {
        binder.registerCustomEditor(Account.class, new AccountEditor(accountService));
        binder.registerCustomEditor(Category.class, new CategoryEditor(categoryService));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping()
    public String app(Model model, Principal principal) {
        String email = principal.getName();
        List<Account> accounts = accountService.findAllByUserEmail(email);
        model.addAttribute("accounts", accounts);
        List<Category> categories = categoryService.findAllByUserEmail(email);
        model.addAttribute("categories", categories);
        List<Transaction> transactions = transactionService.findAllByUserEmail(email);
        model.addAttribute("transactions", transactions);
        Double balance = accountService.getBalance(email);
        model.addAttribute("balance", balance);
        List<Category> expenseCategories = categoryService.findAllByUserEmailAndOperation(email, Operation.EXPENSE);
        List<Category> incomeCategories = categoryService.findAllByUserEmailAndOperation(email, Operation.INCOME);
        model.addAttribute("expenseCategories", expenseCategories);
        model.addAttribute("incomeCategories", incomeCategories);
        return "dashboard";
    }

}
