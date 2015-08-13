package me.dolia.pmm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import me.dolia.pmm.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
	
	@Query("select sum(amount) from Account a where a.user.email = ?")
	Double getSumAmountByUserEmail(String email);

	List<Account> findAllByUserEmail(String email);

}
