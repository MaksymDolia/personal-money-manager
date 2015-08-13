package me.dolia.pmm.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	long countByCategoryId(int id);
	
	long countByAccountOrTransferAccount(Account account, Account transferAccount);
	
	List<Transaction> findAllByUserEmail(String email, Sort sort);

	List<Transaction> findAllByCategory(Category category);

	List<Transaction> findAllByAccountOrTransferAccount(Account account, Account transferAccount);

	List<Transaction> findByUserEmailAndDateBetween(String email, Date fromDate, Date toDate, Sort sort);

	List<Transaction> findByUserEmailAndDateBetweenAndCommentLikeIgnoreCase(String email, Date fromDate, Date toDate, String comment,
			Sort sort);

	List<Transaction> findByAccountOrTransferAccountAndDateBetweenAndCommentLikeIgnoreCase(Account account, Account transferAccount, Date fromDate, Date toDate,
			String comment, Sort sort);

	List<Transaction> findByAccountOrTransferAccountAndDateBetween(Account account, Account transferAccount, Date fromDate, Date toDate, Sort sort);
}
