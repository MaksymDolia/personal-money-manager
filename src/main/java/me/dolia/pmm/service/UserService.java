package me.dolia.pmm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Currency;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Role;
import me.dolia.pmm.entity.User;
import me.dolia.pmm.repository.AccountRepository;
import me.dolia.pmm.repository.CategoryRepository;
import me.dolia.pmm.repository.RoleRepository;
import me.dolia.pmm.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private ApplicationContext context;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;


	public void save(User user) {
		user.setEnabled(true);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		user.setPassword(encoder.encode(user.getPassword()));
		List<Role> roles = new ArrayList<Role>();
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
		walletAccount.setAmount(0);
		walletAccount.setCurrency(Currency.UAH);
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
