package me.dolia.pmm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.User;

public interface AccountRepository extends JpaRepository<Account, Integer> {

	@Query("select sum(amount) from Account where user_id = ?")
	Double getSumAmountByUser(User user);

	List<Account> findAllByUserEmail(String email);

}
