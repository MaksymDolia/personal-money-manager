package me.dolia.pmm.service;

import me.dolia.pmm.entity.*;
import me.dolia.pmm.repository.AccountRepository;
import me.dolia.pmm.repository.CategoryRepository;
import me.dolia.pmm.repository.RoleRepository;
import me.dolia.pmm.repository.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

@Service
public class UserService {
	
	@Inject
	private ApplicationContext context;

	@Inject
	private UserRepository userRepository;

	@Inject
	private RoleRepository roleRepository;

	@Inject
	private AccountRepository accountRepository;
	
	@Inject
	private CategoryRepository categoryRepository;


	public void save(User user) {
		user.setEnabled(true);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		user.setPassword(encoder.encode(user.getPassword()));
		List<Role> roles = new ArrayList<>();
		roles.add(roleRepository.findByName("ROLE_USER"));
		user.setRoles(roles);
		userRepository.save(user);
		createDefaultAccountsAndCategoriesForNewUser(user);
	}

	public User findOneByEmail(String email) {
		return userRepository.findOneByEmail(email);
	}
	
	private void createDefaultAccountsAndCategoriesForNewUser(User user) {
		
		//Create account for wallet
		Account walletAccount = new Account();
		walletAccount.setName(context.getMessage("Name.default.account", null, Locale.ENGLISH));
		walletAccount.setUser(user);
		walletAccount.setAmount(new BigDecimal(0));
		walletAccount.setCurrency(Currency.getInstance("UAH"));
		accountRepository.save(walletAccount);
		
		//Create categories for expenses
		for (int i = 1; i < 6; i++) {
			Category category = new Category();
			category.setName(context.getMessage("Name" + i + ".default.category", null, Locale.ENGLISH));
			category.setType(Operation.EXPENSE);
			category.setUser(user);
			categoryRepository.save(category);
		}
		
		//Create categories for incomes
		for (int i = 6; i < 8; i++) {
			Category category = new Category();
			category.setName(context.getMessage("Name" + i + ".default.category", null, Locale.ENGLISH));
			category.setType(Operation.INCOME);
			category.setUser(user);
			categoryRepository.save(category);
		}
	}

	@Transactional
	@PreAuthorize("#email == authentication.name or hasRole('ADMIN')")
	public void deleteByEmail(@P("email") String email) {
		userRepository.deleteByEmail(email);
	}

}
