package me.dolia.pmm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.User;

public interface AccountRepository extends JpaRepository<Account, Integer> {

	List<Account> findByUser(User user);

	List<Account> findAllByUser(User user);

	@Query("select sum(amount) from Account where user_id = ?")
	Double getSumAmountByUser(User user);

}
