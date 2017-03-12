package me.dolia.pmm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.form.ShowTransactionForm;
import me.dolia.pmm.service.AccountService;
import me.dolia.pmm.service.CategoryService;
import me.dolia.pmm.service.TransactionService;
import me.dolia.pmm.support.AccountEditor;
import me.dolia.pmm.support.CategoryEditor;

/**
 * Controller to handle requests about transactions.
 *
 * @author Maksym Dolia
 */
@Controller
@RequestMapping("/app/transactions")
public class TransactionController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CategoryService categoryService;

    /**
     * Binds the model attribute with the corresponding java object.
     *
     * @return new instance of {@code Transaction} class
     */
    @ModelAttribute("transaction")
    public Transaction createTransaction() {
        return new Transaction();
    }

    /**
     * Binds the model attribute with the corresponding java object.
     *
     * @return new instance of {@code ShowTransactionForm} class
     */
    @ModelAttribute("showTransactionForm")
    public ShowTransactionForm createShowTransactionForm() {
        return new ShowTransactionForm();
    }

    /**
     * Please refer to {@code InitBinder} annotation javadoc.
     *
     * @param binder Links: {@link InitBinder}
     */
    @InitBinder
    protected void initBinder(ServletRequestDataBinder binder) {
        binder.registerCustomEditor(Account.class, new AccountEditor(accountService));
        binder.registerCustomEditor(Category.class, new CategoryEditor(categoryService));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    /**
     * Shows the transactions page.
     *
     * @param form      data from web form
     * @param model     model (MVC pattern)
     * @param principal authenticated user
     * @return view as string
     */
    @RequestMapping(method = RequestMethod.GET)
    public String transactions(@ModelAttribute("showTransactionForm") ShowTransactionForm form, Model model,
                               Principal principal) {
        String email = principal.getName();
        List<Transaction> transactions = transactionService.findAllByForm(email, form);
        model.addAttribute("transactions", transactions);
        model.addAttribute("showTransactionForm", form);
        List<Account> accounts = accountService.findAllByUserEmail(email);
        model.addAttribute("accounts", accounts);

        List<Category> expenseCategories = categoryService.findAllByUserEmailAndOperation(email, Operation.EXPENSE);
        List<Category> incomeCategories = categoryService.findAllByUserEmailAndOperation(email, Operation.INCOME);
        model.addAttribute("expenseCategories", expenseCategories);
        model.addAttribute("incomeCategories", incomeCategories);
        return "transactions";
    }

    /**
     * Saves new transaction.
     *
     * @param transaction data from web form
     * @param referrer    header referrer value
     * @param principal   authenticated user
     * @param result      form's binding result
     * @return view as string, where user will be redirected
     */
    @RequestMapping(value = "/add_transaction", method = RequestMethod.POST)
    public String addTransaction(@Valid @ModelAttribute("transaction") Transaction transaction,
                                 @RequestHeader(value = "referer", required = false) String referrer,
                                 Principal principal,
                                 BindingResult result) {
        if (result.hasErrors()) {
            return "transactions_edit";
        }
        String email = principal.getName();
        transactionService.save(transaction, email);
        if (referrer != null) {
            return "redirect:" + referrer;
        }
        return "redirect:/app/transactions";
    }

    /**
     * Deletes transaction.
     *
     * @param id       transaction's id
     * @param referrer header referrer value
     * @return view as string, where user will be redirected
     */
    @RequestMapping("/{id}/remove")
    public String removeTransaction(@PathVariable Long id,
                                    @RequestHeader(value = "referer", required = false) String referrer) {
        Transaction transaction = transactionService.findOne(id);
        transactionService.delete(transaction);
        if (referrer != null) {
            return "redirect:" + referrer;
        }
        return "redirect:/app/transactions";
    }

    /**
     * Show web page with form to edit existent transaction.
     *
     * @param id    id of transaction be updated
     * @param model model (MVC pattern)
     * @return view as string
     */
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String editTransaction(@PathVariable long id, Model model) {
        Transaction transaction = transactionService.findOne(id);
        String email = transaction.getUser().getEmail();
        List<Transaction> transactions = transactionService.findAllByUserEmail(email);
        model.addAttribute("transactions", transactions);
        model.addAttribute("transaction", transaction);
        List<Account> accounts = accountService.findAllByUserEmail(email);
        model.addAttribute("accounts", accounts);
        List<Category> expenseCategories = categoryService.findAllByUserEmailAndOperation(email, Operation.EXPENSE);
        List<Category> incomeCategories = categoryService.findAllByUserEmailAndOperation(email, Operation.INCOME);
        model.addAttribute("expenseCategories", expenseCategories);
        model.addAttribute("incomeCategories", incomeCategories);
        return "transactions_edit";
    }

    /**
     * Update transaction with given data.
     *
     * @param id          transaction's id
     * @param transaction data from web form
     * @param result      form's binding result
     * @return view as string, where user will be redirected
     */
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST)
    public String editTransaction(@PathVariable long id, @Valid @ModelAttribute Transaction transaction,
                                  BindingResult result) {
        if (result.hasErrors()) {
            return "transactions_edit";
        }
        Transaction existingTransaction = transactionService.findOne(id);
        transactionService.editAndSave(transaction, existingTransaction);
        return "redirect:/app/transactions";
    }

}
