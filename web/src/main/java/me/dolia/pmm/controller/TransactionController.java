package me.dolia.pmm.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dolia.pmm.controller.dto.AccountDto;
import me.dolia.pmm.controller.dto.CategoryDto;
import me.dolia.pmm.controller.dto.TransactionDto;
import me.dolia.pmm.form.ShowTransactionForm;
import me.dolia.pmm.persistence.entity.Account;
import me.dolia.pmm.persistence.entity.Category;
import me.dolia.pmm.persistence.entity.Operation;
import me.dolia.pmm.persistence.entity.Transaction;
import me.dolia.pmm.service.AccountService;
import me.dolia.pmm.service.CategoryService;
import me.dolia.pmm.service.TransactionService;
import me.dolia.pmm.support.AccountEditor;
import me.dolia.pmm.support.CategoryEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to handle requests about transactions.
 *
 * @author Maksym Dolia
 */
@Controller
@RequestMapping("/app/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private static final String TRANSACTIONS_EDIT_VIEW_NAME = "transactions_edit";
  private static final String REDIRECT_TO_TRANSACTIONS = "redirect:/app/transactions";
  private static final String TRANSACTIONS = "transactions";

  private final AccountService accountService;
  private final TransactionService transactionService;
  private final CategoryService categoryService;

  /**
   * Binds the model attribute with the corresponding java object.
   *
   * @return new instance of {@code Transaction} class
   */
  @ModelAttribute("transaction")
  public TransactionDto createTransaction() {
    return new TransactionDto();
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
    binder.registerCustomEditor(AccountDto.class, new AccountEditor(accountService));
    binder.registerCustomEditor(CategoryDto.class, new CategoryEditor(categoryService));
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
  }

  /**
   * Shows the transactions page.
   *
   * @param form data from web form
   * @param model model (MVC pattern)
   * @param principal authenticated user
   * @return view as string
   */
  @GetMapping
  public String transactions(@ModelAttribute("showTransactionForm") ShowTransactionForm form,
      Model model,
      Principal principal) {
    String email = principal.getName();
    List<Transaction> transactions = transactionService.findAllByForm(email, form);
    model.addAttribute(TRANSACTIONS, transactions);
    model.addAttribute("showTransactionForm", form);
    List<Account> accounts = accountService.findAllByUserEmail(email);
    model.addAttribute("accounts", accounts);

    List<Category> expenseCategories = categoryService
        .findAllByUserEmailAndOperation(email, Operation.EXPENSE);
    List<Category> incomeCategories = categoryService
        .findAllByUserEmailAndOperation(email, Operation.INCOME);
    model.addAttribute("expenseCategories", expenseCategories);
    model.addAttribute("incomeCategories", incomeCategories);
    return TRANSACTIONS;
  }

  /**
   * Saves new transaction.
   *
   * @param dto data from web form
   * @param referrer header referrer value
   * @param principal authenticated user
   * @param result form's binding result
   * @return view as string, where user will be redirected
   */
  @PostMapping(value = "/add_transaction")
  public String addTransaction(@Valid @ModelAttribute("transaction") TransactionDto dto,
      @RequestHeader(value = "referer", required = false) String referrer,
      Principal principal,
      BindingResult result) {
    if (result.hasErrors()) {
      return TRANSACTIONS_EDIT_VIEW_NAME;
    }
    String email = principal.getName();
    transactionService.save(dto.toTransaction(), email);
    if (referrer != null) {
      return "redirect:" + referrer;
    }
    return REDIRECT_TO_TRANSACTIONS;
  }

  /**
   * Deletes transaction.
   *
   * @param id transaction's id
   * @param referrer header referrer value
   * @return view as string, where user will be redirected
   */
  @PostMapping("/{id}/remove")
  public String removeTransaction(@PathVariable Long id,
      @RequestHeader(value = "referer", required = false) String referrer) {
    Transaction transaction = transactionService.findOne(id);
    transactionService.delete(transaction);
    if (referrer != null) {
      return "redirect:" + referrer;
    }
    return REDIRECT_TO_TRANSACTIONS;
  }

  /**
   * Show web page with form to edit existent transaction.
   *
   * @param id id of transaction be updated
   * @param model model (MVC pattern)
   * @return view as string
   */
  @GetMapping(value = "/{id}/edit")
  public String editTransaction(@PathVariable long id, Model model) {
    Transaction transaction = transactionService.findOne(id);
    String email = transaction.getUser().getEmail();
    List<Transaction> transactions = transactionService.findAllByUserEmail(email);
    model.addAttribute(TRANSACTIONS, transactions);
    model.addAttribute("transaction", transaction);
    List<Account> accounts = accountService.findAllByUserEmail(email);
    model.addAttribute("accounts", accounts);
    List<Category> expenseCategories = categoryService
        .findAllByUserEmailAndOperation(email, Operation.EXPENSE);
    List<Category> incomeCategories = categoryService
        .findAllByUserEmailAndOperation(email, Operation.INCOME);
    model.addAttribute("expenseCategories", expenseCategories);
    model.addAttribute("incomeCategories", incomeCategories);
    return TRANSACTIONS_EDIT_VIEW_NAME;
  }

  /**
   * Update transaction with given data.
   *
   * @param id transaction's id
   * @param dto data from web form
   * @param result form's binding result
   * @return view as string, where user will be redirected
   */
  @PostMapping(value = "/{id}/edit")
  public String editTransaction(@PathVariable long id,
      @Valid @ModelAttribute TransactionDto dto,
      BindingResult result) {
    if (result.hasErrors()) {
      return TRANSACTIONS_EDIT_VIEW_NAME;
    }
    Transaction existingTransaction = transactionService.findOne(id);
    transactionService.editAndSave(dto.toTransaction(), existingTransaction);
    return REDIRECT_TO_TRANSACTIONS;
  }
}
