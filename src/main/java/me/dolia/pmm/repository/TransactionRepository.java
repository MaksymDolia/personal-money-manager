package me.dolia.pmm.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.entity.User;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	long countByCategory(Category category);
	
	long countByAccountOrTransferAccount(Account account, Account transferAccount);
	
	List<Transaction> findAllByUser(User user);

	List<Transaction> findAllByUser(User user, Sort sort);

	List<Transaction> findAllByCategory(Category category);

	List<Transaction> findAllByAccountOrTransferAccount(Account account, Account transferAccount);

	List<Transaction> findByUserAndDateBetween(User user, Date fromDate, Date toDate);

	List<Transaction> findByUserAndDateBetween(User user, Date fromDate, Date toDate, Sort sort);

	List<Transaction> findByUserAndDateBetweenAndCommentLikeIgnoreCase(User user, Date fromDate, Date toDate, String comment,
			Sort sort);

	List<Transaction> findByAccountOrTransferAccountAndDateBetweenAndCommentLikeIgnoreCase(Account account, Account transferAccount, Date fromDate, Date toDate,
			String comment, Sort sort);

	List<Transaction> findByAccountOrTransferAccountAndDateBetween(Account account, Account transferAccount, Date fromDate, Date toDate, Sort sort);



}
