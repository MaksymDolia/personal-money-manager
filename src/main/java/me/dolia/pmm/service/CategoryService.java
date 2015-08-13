package me.dolia.pmm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.entity.User;
import me.dolia.pmm.repository.CategoryRepository;
import me.dolia.pmm.repository.TransactionRepository;
import me.dolia.pmm.repository.UserRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	public Category findOne(Integer id) {
		return categoryRepository.findOne(id);
	}

	public List<Category> findAllByUserAndOperation(User user, Operation operation) {
		return categoryRepository.findAllByUserAndOperation(user, operation);
	}

	public void save(Category category, String email) {
		User user = userRepository.findOneByEmail(email);
		category.setUser(user);
		categoryRepository.save(category);
	}

	@PreAuthorize("#category.user.email == authentication.name or hasRole('ADMIN')")
	public void delete(@P("category") Category category) {
		categoryRepository.delete(category);
	}

	public void save(Category category) {
		categoryRepository.save(category);
	}
	
	public void editCategory(Category existingCategory, Category category) {
		existingCategory.setName(category.getName());
		existingCategory.setOperation(category.getOperation());
		save(existingCategory);
	}

	public void transferTransactions(int fromId, int toId) {
		Category fromCategory = findOne(fromId);
		Category toCategory = findOne(toId);
		if (fromCategory != null || toCategory != null) {
			transferTransactions(fromCategory, toCategory);
		}

	}

	@PreAuthorize("#fromCategory.user.email == authentication.name or #toCategory.user.email == authentication.name or hasRole('ADMIN')")
	private void transferTransactions(@P("fromCategory") Category fromCategory, @P("toCategory") Category toCategory) {
		List<Transaction> transactions = transactionRepository.findAllByCategory(fromCategory);
		if (!transactions.isEmpty()) {
			for (Transaction transaction : transactions) {
				transaction.setCategory(toCategory);
				transactionRepository.save(transaction);
			}
		}
	}

	public List<Category> findAllByUserEmail(String email) {
		return categoryRepository.findAllByUserEmail(email);
	}

}
