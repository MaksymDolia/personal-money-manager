package me.dolia.pmm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.entity.User;
import me.dolia.pmm.repository.AccountRepository;
import me.dolia.pmm.repository.TransactionRepository;
import me.dolia.pmm.repository.UserRepository;

@Service
public class AccountService {
	
	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	public void save(Account account) {
		accountRepository.save(account);
	}

	public void save(Account account, String email) {
		User user = userRepository.findOneByEmail(email);
		account.setUser(user);
		accountRepository.save(account);
	}

	public Account findOne(int id) {
		return accountRepository.findOne(id);
	}

	public Double getBalance(User user) {
		return accountRepository.getSumAmountByUser(user);
	}

	public List<Account> findAllByUser(User user) {
		return accountRepository.findAllByUser(user);
	}

	@PreAuthorize("#account.user.email == authentication.name or hasRole('ADMIN')")
	public void delete(@P("account") Account account) {
		accountRepository.delete(account);
	}

	public void transferTransactions(int fromId, int toId) {
		Account fromAccount = findOne(fromId);
		Account toAccount = findOne(toId);
		if (fromAccount != null || toAccount != null) {
			transferTransactions(fromAccount, toAccount);
		}

	}
	
	public void editAccount(Account existingAccount, Account account) {
		existingAccount.setAmount(account.getAmount());
		existingAccount.setCurrency(account.getCurrency());
		existingAccount.setName(account.getName());
		save(existingAccount);
	}

	@PreAuthorize("#fromAccount.user.email == authentication.name or #toAccount.user.email == authentication.name or hasRole('ADMIN')")
	private void transferTransactions(@P("fromAccount") Account fromAccount, @P("toAccount") Account toAccount) {
		List<Transaction> transactions = transactionRepository.findAllByAccountOrTransferAccount(fromAccount, fromAccount);
		if (transactions != null && !transactions.isEmpty()) {
			for (Transaction transaction : transactions) {
				Operation operation = transaction.getOperation();
				double amount = transaction.getAmount();

				switch (operation) {
				case EXPENSE:
					fromAccount.setAmount(fromAccount.getAmount() + amount);
					toAccount.setAmount(toAccount.getAmount() - amount);
					transaction.setAccount(toAccount);
					break;
				case INCOME:
					fromAccount.setAmount(fromAccount.getAmount() - amount);
					toAccount.setAmount(toAccount.getAmount() + amount);
					transaction.setAccount(toAccount);
					break;
				case TRANSFER:
					if (transaction.getAccount().getId() == fromAccount.getId()) {
						fromAccount.setAmount(fromAccount.getAmount() + amount);
						toAccount.setAmount(toAccount.getAmount() - amount);
						transaction.setAccount(toAccount);
					} else if (transaction.getTransferAccount().getId() == fromAccount.getId()) {
						fromAccount.setAmount(fromAccount.getAmount() - amount);
						toAccount.setAmount(toAccount.getAmount() + amount);
						transaction.setTransferAccount(fromAccount);
					}
					break;
				default:
					break;
				}

				transactionRepository.save(transaction);
				accountRepository.save(fromAccount);
				accountRepository.save(toAccount);
			}
		}
	}

}
