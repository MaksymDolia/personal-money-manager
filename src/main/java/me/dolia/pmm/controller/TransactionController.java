package me.dolia.pmm.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

@Controller
@RequestMapping("/app/transactions")
public class TransactionController {
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private CategoryService categoryService;

	@ModelAttribute("transaction")
	public Transaction createTransaction() {
		return new Transaction();
	}

	@ModelAttribute("showTransactionForm")
	public ShowTransactionForm createShowTransactionForm() {
		return new ShowTransactionForm();
	}

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Account.class, new AccountEditor(accountService));
		binder.registerCustomEditor(Category.class, new CategoryEditor(categoryService));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}

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

	@RequestMapping(value = "/add_transaction", method = RequestMethod.POST)
	public String addTransaction(@Valid @ModelAttribute("transaction") Transaction transaction,
			@RequestHeader(value = "referer", required = false) String referrer, Principal principal,
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

	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public String editTransaction(@PathVariable long id, Model model, Principal principal) {
		Transaction transaction = transactionService.findOne(id);
		String email = principal.getName();
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
