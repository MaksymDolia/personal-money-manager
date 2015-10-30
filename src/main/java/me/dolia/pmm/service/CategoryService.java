package me.dolia.pmm.service;

import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.entity.User;
import me.dolia.pmm.repository.CategoryRepository;
import me.dolia.pmm.repository.TransactionRepository;
import me.dolia.pmm.repository.UserRepository;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class CategoryService {

	@Inject
	private CategoryRepository categoryRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private TransactionRepository transactionRepository;

	@PostAuthorize("returnObject.user.email == authentication.name or hasRole('ADMIN')")
	public Category findOne(Integer id) {
		return categoryRepository.findOne(id);
	}

	public List<Category> findAllByUserEmailAndOperation(String email, Operation operation) {
		return categoryRepository.findAllByUserEmailAndOperation(email, operation);
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
