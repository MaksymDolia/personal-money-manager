package me.dolia.pmm.controller;

import static java.util.stream.Collectors.toList;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.dolia.pmm.controller.dto.AccountDto;
import me.dolia.pmm.controller.dto.CategoryDto;
import me.dolia.pmm.controller.dto.TransactionDto;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.service.AccountService;
import me.dolia.pmm.service.CategoryService;
import me.dolia.pmm.service.TransactionService;
import me.dolia.pmm.support.AccountEditor;
import me.dolia.pmm.support.CategoryEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller, handles requests to user's dashboard.
 *
 * @author Maksym Dolia
 */
@Controller
@RequestMapping("/app")
@RequiredArgsConstructor
public class DashboardController {

  private final AccountService accountService;
  private final CategoryService categoryService;
  private final TransactionService transactionService;

  @ModelAttribute("account")
  public AccountDto createAccount() {
    return new AccountDto();
  }

  @ModelAttribute("transaction")
  public TransactionDto createTransaction() {
    return new TransactionDto();
  }

  @InitBinder
  protected void initBinder(ServletRequestDataBinder binder) {
    binder.registerCustomEditor(AccountDto.class, new AccountEditor(accountService));
    binder.registerCustomEditor(CategoryDto.class, new CategoryEditor(categoryService));
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
  }

  @GetMapping
  public String app(Model model, Principal principal) {
    String email = principal.getName();
    List<AccountDto> accounts = accountService.findAllByUserEmail(email)
        .stream()
        .map(AccountDto::fromAccount)
        .collect(toList());
    model.addAttribute("accounts", accounts);
    List<CategoryDto> categories = categoryService.findAllByUserEmail(email)
        .stream()
        .map(CategoryDto::fromCategory)
        .collect(toList());
    model.addAttribute("categories", categories);
    List<TransactionDto> transactions = transactionService.findAllByUserEmail(email)
        .stream()
        .map(TransactionDto::fromTransaction)
        .collect(toList());
    model.addAttribute("transactions", transactions);
    Double balance = accountService.getBalance(email);
    model.addAttribute("balance", balance);
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
    return "dashboard";
  }

}
