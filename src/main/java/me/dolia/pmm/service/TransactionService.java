package me.dolia.pmm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.entity.User;
import me.dolia.pmm.form.ShowTransactionForm;
import me.dolia.pmm.repository.AccountRepository;
import me.dolia.pmm.repository.TransactionRepository;
import me.dolia.pmm.repository.UserRepository;

@Service
public class TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserRepository userRepository;

	@PreAuthorize("#transaction.user.email == authentication.name or hasRole('ADMIN')")
	public void delete(@P("transaction") Transaction transaction) {
		resetTransaction(transaction);
		transactionRepository.delete(transaction);
	}

	public Transaction findOne(Long id) {
		return transactionRepository.findOne(id);
	}

	public List<Transaction> findAllByUser(User user) {
		return transactionRepository.findAllByUser(user, new Sort(Direction.DESC, "date"));
	}

	public List<Transaction> findAllByCategory(Category category) {
		return transactionRepository.findAllByCategory(category);
	}

	public List<Transaction> findAllByAccount(Account account) {
		return transactionRepository.findAllByAccountOrTransferAccount(account, account);
	}

	public List<Transaction> findAllByForm(User user, ShowTransactionForm form) {
		String sortBy = form.getSortBy().toString().toLowerCase();
		Sort sort = new Sort(Direction.DESC, sortBy);

		if (form.getComment() != null && !form.getComment().isEmpty()) {
			return transactionRepository.findByUserAndDateBetweenAndCommentLikeIgnoreCase(user, form.getFromDate(),
					form.getToDate(), form.getComment(), sort);
		}
		return transactionRepository.findByUserAndDateBetween(user, form.getFromDate(), form.getToDate(), sort);
	}

	@PreAuthorize("#account.user.email == authentication.name or hasRole('ADMIN')")
	public List<Transaction> findAllByAccountAndForm(@P("account") Account account, ShowTransactionForm form) {
		String sortBy = form.getSortBy().toString().toLowerCase();
		Sort sort = new Sort(Direction.DESC, sortBy);

		if (form.getComment() != null && !form.getComment().isEmpty()) {
			return transactionRepository.findByAccountOrTransferAccountAndDateBetweenAndCommentLikeIgnoreCase(account,
					account, form.getFromDate(), form.getToDate(), form.getComment(), sort);
		}
		return transactionRepository.findByAccountOrTransferAccountAndDateBetween(account, account, form.getFromDate(),
				form.getToDate(), sort);
	}

	public long countTransactionsByCategory(Category category) {
		return transactionRepository.countByCategory(category);
	}

	public long countTransactionsByAccount(Account account) {
		return transactionRepository.countByAccountOrTransferAccount(account, account);
	}

	public void save(Transaction transaction, String email) {
		User user = userRepository.findOneByEmail(email);
		transaction.setUser(user);
		save(transaction);
		processTransaction(transaction);
	}

	public void save(Transaction transaction) {
		transactionRepository.save(transaction);
	}

	public void editAndSave(Transaction changedTransaction, Transaction originalTransaction) {
		resetTransaction(originalTransaction);
		originalTransaction.copy(changedTransaction);
		save(originalTransaction);
		processTransaction(originalTransaction);
	}

	private void processTransaction(Transaction transaction) {
		Account account = accountRepository.findOne(transaction.getAccount().getId());
		double amount = transaction.getAmount();
		Operation operation = transaction.getOperation();
		switch (operation) {
		case EXPENSE:
			account.setAmount(account.getAmount() - amount);
			break;
		case INCOME:
			account.setAmount(account.getAmount() + amount);
			break;
		case TRANSFER:
			Account accountTo = accountRepository.findOne(transaction.getTransferAccount().getId());
			account.setAmount(account.getAmount() - amount);
			accountTo.setAmount(accountTo.getAmount() + amount);
			accountRepository.save(accountTo);
			break;
		default:
			break;
		}
		accountRepository.save(account);
	}

	private void resetTransaction(Transaction transaction) {
		Account account = accountRepository.findOne(transaction.getAccount().getId());
		double amount = transaction.getAmount();
		Operation operation = transaction.getOperation();
		switch (operation) {
		case EXPENSE:
			account.setAmount(account.getAmount() + amount);
			break;
		case INCOME:
			account.setAmount(account.getAmount() - amount);
			break;
		case TRANSFER:
			Account accountTo = accountRepository.findOne(transaction.getTransferAccount().getId());
			account.setAmount(account.getAmount() + amount);
			accountTo.setAmount(accountTo.getAmount() - amount);
			accountRepository.save(accountTo);
			break;
		default:
			break;
		}
		accountRepository.save(account);
	}

}
