package me.dolia.pmm.controller;

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
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/app/transactions")
public class TransactionController {
	
	@Inject
	private AccountService accountService;
	
	@Inject
	private TransactionService transactionService;
	
	@Inject
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
	protected void initBinder(ServletRequestDataBinder binder) {
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
