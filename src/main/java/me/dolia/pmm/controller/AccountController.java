package me.dolia.pmm.controller;

import static java.util.stream.Collectors.toList;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dolia.pmm.controller.dto.AccountDto;
import me.dolia.pmm.controller.dto.CategoryDto;
import me.dolia.pmm.controller.dto.TransactionDto;
import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.form.ShowTransactionForm;
import me.dolia.pmm.service.AccountService;
import me.dolia.pmm.service.CategoryService;
import me.dolia.pmm.service.TransactionService;
import me.dolia.pmm.support.AccountEditor;
import me.dolia.pmm.support.CategoryEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.ApplicationContext;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller to deal with accounts.
 *
 * @author Maksym Dolia
 */
@Controller
@RequestMapping("/app/accounts")
@RequiredArgsConstructor
public class AccountController {

  private static final String REDIRECT_TO_ACCOUNTS = "redirect:/app/accounts";
  private static final String ACCOUNTS = "accounts";

  private final ApplicationContext context;
  private final AccountService accountService;
  private final TransactionService transactionService;
  private final CategoryService categoryService;

  /**
   * Binds the model attribute with the corresponding java object.
   *
   * @return new instance of {@code Account} class
   */
  @ModelAttribute("account")
  public AccountDto createAccount() {
    return new AccountDto();
  }

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
   * Shows the accounts page with user's accounts.
   *
   * @param model mvc's model
   * @param principal authenticated user
   * @return view as string
   */
  @RequestMapping
  public String accounts(Model model, Principal principal) {
    String email = principal.getName();
    List<AccountDto> accounts = accountService.findAllByUserEmail(email)
        .stream()
        .map(AccountDto::fromAccount)
        .collect(toList());
    model.addAttribute(ACCOUNTS, accounts);
    double balance = accountService.getBalance(email);
    model.addAttribute("balance", balance);
    return ACCOUNTS;
  }

  /**
   * Adds new account according to users input.
   *
   * @param dto given account data
   * @param result form's binding result
   * @param principal authenticated user
   * @param referrer value of header's field 'refferer'
   * @param attr redirect attributes
   * @return view as string, where user will be redirected
   */
  @PostMapping(value = "/add_account")
  public String addAccount(@Valid @ModelAttribute AccountDto dto,
      BindingResult result,
      Principal principal,
      @RequestHeader(value = "referer", required = false) String referrer,
      RedirectAttributes attr) {

    if (result.hasErrors()) {
      attr.addFlashAttribute("account", dto);
      attr.addFlashAttribute("org.springframework.validation.BindingResult.account", result);
      return REDIRECT_TO_ACCOUNTS;
    }
    String email = principal.getName();
    accountService.save(dto.toAccount(), email);
    if (referrer != null) {
      return "redirect:" + referrer;
    }
    return REDIRECT_TO_ACCOUNTS;
  }

  /**
   * Shows page for account with given id.
   *
   * @param accountId account's id
   * @param form data from web form
   * @param model mvc's model
   * @return view as string
   */
  @GetMapping(value = "/{id}")
  public String transactions(@PathVariable("id") int accountId,
      @ModelAttribute("showTransactionForm") ShowTransactionForm form,
      Model model) {

    Account account = accountService.findOne(accountId);
    String email = account.getUser().getEmail();
    List<TransactionDto> transactions = transactionService.findAllByAccountAndForm(account, form)
        .stream()
        .map(TransactionDto::fromTransaction)
        .collect(toList());
    model.addAttribute("transactions", transactions);
    model.addAttribute("showTransactionForm", form);
    List<AccountDto> accounts = accountService.findAllByUserEmail(email)
        .stream()
        .map(AccountDto::fromAccount)
        .collect(toList());
    model.addAttribute(ACCOUNTS, accounts);

    List<CategoryDto> expenseCategories = categoryService
        .findAllByUserEmailAndOperation(email, Operation.EXPENSE)
        .stream()
        .map(CategoryDto::fromCategory)
        .collect(toList());
    List<CategoryDto> incomeCategories = categoryService
        .findAllByUserEmailAndOperation(email, Operation.INCOME)
        .stream()
        .map(CategoryDto::fromCategory)
        .collect(toList());
    model.addAttribute("expenseCategories", expenseCategories);
    model.addAttribute("incomeCategories", incomeCategories);
    return "transactions";
  }

  /**
   * Deletes account by given id.
   *
   * @param id account's id
   * @param attr redirect attributes
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
      attr.addFlashAttribute("message",
          context.getMessage("NotEmpty.AccountController.message", null, locale));
      return "redirect:/app/accounts/{id}/edit";
    }
    accountService.delete(account);
    return REDIRECT_TO_ACCOUNTS;
  }

  /**
   * Shows page with web form to edit the user's account with given id.
   *
   * @param id account's id
   * @param model model
   * @return view as string
   */
  @GetMapping(value = "/{id}/edit")
  public String editAccount(@PathVariable int id, Model model) {

    Account account = accountService.findOne(id);
    AccountDto accountDto = AccountDto.fromAccount(account);
    String email = account.getUser().getEmail();
    List<AccountDto> accounts = accountService.findAllByUserEmail(email)
        .stream()
        .map(AccountDto::fromAccount)
        .collect(toList());
    model.addAttribute("account", accountDto);
    model.addAttribute(ACCOUNTS, accounts);
    double balance = accountService.getBalance(email);
    model.addAttribute("balance", balance);
    long quantityOfTransactions = transactionService.countTransactionsByAccount(account);
    model.addAttribute("quantityOfTransactions", quantityOfTransactions);
    return "accounts_edit";
  }

  /**
   * Performs saving edited account data.
   *
   * @param dto account's model attributes
   * @param id account's id
   * @param result binding result from web form
   * @return view as string, where user will be redirected
   */
  @PostMapping(value = "/{id}/edit")
  public String editAccount(@Valid @ModelAttribute AccountDto dto,
      @PathVariable int id,
      BindingResult result) {

    if (result.hasErrors()) {
      return "accounts_edit";
    }
    Account existingAccount = accountService.findOne(id);
    accountService.editAccount(existingAccount, dto.toAccount());
    return REDIRECT_TO_ACCOUNTS;
  }

  /**
   * Transfers transactions from one account to another.
   *
   * @param toId id of account where transaction will be transferred
   * @param fromId id of account transaction will be transferred from
   * @param attr redirect attributes
   * @return view as string, where user will be redirected
   */
  @PostMapping(value = "/transfer")
  public String transferTransactions(@RequestParam("toAccount") int toId,
      @RequestParam("fromAccount") int fromId,
      RedirectAttributes attr) {

    accountService.transferTransactions(fromId, toId);
    attr.addAttribute("fromId", fromId);
    return "redirect:/app/accounts/{fromId}/edit";
  }

}
