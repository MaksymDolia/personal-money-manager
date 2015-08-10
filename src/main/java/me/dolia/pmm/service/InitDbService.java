package me.dolia.pmm.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Currency;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Role;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.entity.User;
import me.dolia.pmm.repository.AccountRepository;
import me.dolia.pmm.repository.CategoryRepository;
import me.dolia.pmm.repository.RoleRepository;
import me.dolia.pmm.repository.TransactionRepository;
import me.dolia.pmm.repository.UserRepository;

@Service
public class InitDbService {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@PostConstruct
	public void init() {
		if (roleRepository.findByName("ROLE_ADMIN") == null) {
			Role roleUser = new Role();
			roleUser.setName("ROLE_USER");
			roleRepository.save(roleUser);

			Role roleAdmin = new Role();
			roleAdmin.setName("ROLE_ADMIN");
			roleRepository.save(roleAdmin);

			User user = new User();
			user.setEnabled(true);
			user.setEmail("admin@admin");
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			user.setPassword(encoder.encode("admin"));
			List<Role> roles = new ArrayList<Role>();
			roles.add(roleAdmin);
			roles.add(roleUser);
			user.setRoles(roles);
			userRepository.save(user);

			// Create account for wallet
			Account walletAccount = new Account();
			walletAccount.setName(context.getMessage("Name.default.account", null, Locale.ENGLISH));
			walletAccount.setUser(user);
			walletAccount.setAmount(0);
			walletAccount.setCurrency(Currency.UAH);
			accountRepository.save(walletAccount);

			Account bankAccount = new Account();
			bankAccount.setName("Bank");
			bankAccount.setUser(user);
			bankAccount.setAmount(500);
			bankAccount.setCurrency(Currency.UAH);
			accountRepository.save(bankAccount);

			// Create categories for expenses
			for (int i = 1; i < 6; i++) {
				Category category = new Category();
				category.setName(context.getMessage("Name" + i + ".default.category", null, Locale.ENGLISH));
				category.setType(Operation.EXPENSE);
				category.setUser(user);
				categoryRepository.save(category);
			}

			// Create categories for incomes
			for (int i = 6; i < 8; i++) {
				Category category = new Category();
				category.setName(context.getMessage("Name" + i + ".default.category", null, Locale.ENGLISH));
				category.setType(Operation.INCOME);
				category.setUser(user);
				categoryRepository.save(category);
			}

			Transaction transaction1 = new Transaction();
			transaction1.setDate(new Date());
			transaction1.setAccount(walletAccount);
			transaction1.setAmount(50);
			transaction1.setCurrency(Currency.UAH);
			transaction1.setCategory(categoryRepository.findOne(3));
			transaction1.setType(Operation.EXPENSE);
			transaction1.setComment("McDonalds");
			transaction1.setUser(user);
			transactionRepository.save(transaction1);

			Transaction transaction2 = new Transaction();
			Calendar calendar = new GregorianCalendar();
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
			transaction2.setDate(calendar.getTime());
			transaction2.setAccount(bankAccount);
			transaction2.setAmount(45.69);
			transaction2.setCurrency(Currency.UAH);
			transaction2.setCategory(categoryRepository.findOne(7));
			transaction2.setType(Operation.INCOME);
			transaction2.setComment("Festo");
			transaction2.setUser(user);
			transactionRepository.save(transaction2);

			List<Transaction> transactions = new ArrayList<>();
			transactions.add(transaction1);
			user.setTransactions(transactions);
		}
	}
}
