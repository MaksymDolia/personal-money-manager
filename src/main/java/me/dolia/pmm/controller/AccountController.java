package me.dolia.pmm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.ApplicationContext;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
 * Controller to deal with accounts.
 *
 * @author Maksym Dolia
 */
@Controller
@RequestMapping("/app/accounts")
public class AccountController {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CategoryService categoryService;

    /**
     * Binds the model attribute with the corresponding java object.
     *
     * @return new instance of {@code Account} class
     */
    @ModelAttribute("account")
    public Account createAccount() {
        return new Account();
    }

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
     * Shows the accounts page with user's accounts.
     *
     * @param model     mvc's model
     * @param principal authenticated user
     * @return view as string
     */
    @RequestMapping
    public String accounts(Model model, Principal principal) {
        String email = principal.getName();
        List<Account> accounts = accountService.findAllByUserEmail(email);
        model.addAttribute("accounts", accounts);
        double balance = accountService.getBalance(email);
        model.addAttribute("balance", balance);
        return "accounts";
    }

    /**
     * Adds new account according to users input.
     *
     * @param account   given account data
     * @param result    form's binding result
     * @param principal authenticated user
     * @param referrer  value of header's field 'refferer'
     * @param attr      redirect attributes
     * @return view as string, where user will be redirected
     */
    @RequestMapping(value = "/add_account", method = RequestMethod.POST)
    public String addAccount(@Valid @ModelAttribute Account account,
                             BindingResult result,
                             Principal principal,
                             @RequestHeader(value = "referer", required = false) String referrer,
                             RedirectAttributes attr) {

        if (result.hasErrors()) {
            attr.addFlashAttribute("account", account);
            attr.addFlashAttribute("org.springframework.validation.BindingResult.account", result);
            return "redirect:/app/accounts";
        }
        String email = principal.getName();
        accountService.save(account, email);
        if (referrer != null) {
            return "redirect:" + referrer;
        }
        return "redirect:/app/accounts";
    }

    /**
     * Shows page for account with given id.
     *
     * @param accountId account's id
     * @param form      data from web form
     * @param model     mvc's model
     * @return view as string
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String transactions(@PathVariable("id") int accountId,
                               @ModelAttribute("showTransactionForm") ShowTransactionForm form,
                               Model model) {

        Account account = accountService.findOne(accountId);
        String email = account.getUser().getEmail();
        List<Transaction> transactions = transactionService.findAllByAccountAndForm(account, form);
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
     * Deletes account by given id.
     *
     * @param id     account's id
     * @param attr   redirect attributes
     * @param locale locale
     * @return view as string, where user will be redirected
     */
    @RequestMapping("/{id}/remove")
    public String deleteAccount(@PathVariable int id,
                                RedirectAttributes attr,
                                Locale locale) {

        Account account = accountService.findOne(id);
        List<Transaction> transactions = transactionService.findAllByAccount(account);
        if (!transactions.isEmpty()) {
            attr.addAttribute("id", id);
            attr.addFlashAttribute("message", context.getMessage("NotEmpty.AccountController.message", null, locale));
            return "redirect:/app/accounts/{id}/edit";
        }
        accountService.delete(account);
        return "redirect:/app/accounts";
    }

    /**
     * Shows page with web form to edit the user's account with given id.
     *
     * @param id    account's id
     * @param model model
     * @return view as string
     */
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String editAccount(@PathVariable int id, Model model) {

        Account account = accountService.findOne(id);
        String email = account.getUser().getEmail();
        List<Account> accounts = accountService.findAllByUserEmail(email);
        model.addAttribute("account", account);
        model.addAttribute("accounts", accounts);
        double balance = accountService.getBalance(email);
        model.addAttribute("balance", balance);
        long quantityOfTransactions = transactionService.countTransactionsByAccount(account);
        model.addAttribute("quantityOfTransactions", quantityOfTransactions);
        return "accounts_edit";
    }

    /**
     * Performs saving edited account data.
     *
     * @param account account's model attributes
     * @param id      account's id
     * @param result  binding result from web form
     * @return view as string, where user will be redirected
     */
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST)
    public String editAccount(@Valid @ModelAttribute Account account,
                              @PathVariable int id,
                              BindingResult result) {

        if (result.hasErrors()) {
            return "accounts_edit";
        }
        Account existingAccount = accountService.findOne(id);
        accountService.editAccount(existingAccount, account);
        return "redirect:/app/accounts";
    }

    /**
     * Transfers transactions from one account to another.
     *
     * @param toId   id of account where transaction will be transferred
     * @param fromId id of account transaction will be transferred from
     * @param attr   redirect attributes
     * @return view as string, where user will be redirected
     */
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public String transferTransactions(@RequestParam("toAccount") int toId,
                                       @RequestParam("fromAccount") int fromId,
                                       RedirectAttributes attr) {

        accountService.transferTransactions(fromId, toId);
        attr.addAttribute("fromId", fromId);
        return "redirect:/app/accounts/{fromId}/edit";
    }

}
