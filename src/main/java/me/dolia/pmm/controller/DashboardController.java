package me.dolia.pmm.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.entity.User;
import me.dolia.pmm.service.AccountService;
import me.dolia.pmm.service.CategoryService;
import me.dolia.pmm.service.TransactionService;
import me.dolia.pmm.service.UserService;
import me.dolia.pmm.support.AccountEditor;
import me.dolia.pmm.support.CategoryEditor;

@Controller
@RequestMapping("/app")
public class DashboardController {

	@Autowired
	private UserService userService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
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
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Account.class, new AccountEditor(accountService));
		binder.registerCustomEditor(Category.class, new CategoryEditor(categoryService));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}

	@RequestMapping()
	public String app(Model model, Principal principal) {
		String email = principal.getName();
		User user = userService.findOneByEmail(email);
		List<Account> accounts = accountService.findAllByUser(user);
		model.addAttribute("accounts", accounts);
		List<Category> categories = categoryService.findAllByUser(user);
		model.addAttribute("categories", categories);
		List<Transaction> transactions = transactionService.findAllByUser(user);
		model.addAttribute("transactions", transactions);
		Double balance = accountService.getBalance(user);
		model.addAttribute("balance", balance);
		List<Category> expenseCategories = categoryService.findAllByUserAndOperation(user, Operation.EXPENSE);
		List<Category> incomeCategories = categoryService.findAllByUserAndOperation(user, Operation.INCOME);
		model.addAttribute("expenseCategories", expenseCategories);
		model.addAttribute("incomeCategories", incomeCategories);
		return "dashboard";
	}

}
