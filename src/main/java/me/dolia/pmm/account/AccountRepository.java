package me.dolia.pmm.account;

import me.dolia.pmm.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository to manage {@link Account} instances.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByUser(User user);

    @Query("SELECT sum(a.balance) from Account a where a.user = :user")
    BigDecimal calculateBalance(@Param("user") User user);
}