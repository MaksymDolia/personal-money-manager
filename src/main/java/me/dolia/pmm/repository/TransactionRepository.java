package me.dolia.pmm.repository;

import me.dolia.pmm.entity.Account;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Transaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * Jpa repository to performs database operations with {@code Transaction} entity.
 *
 * @author Maksym Dolia
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Counts transaction by given category id.
     *
     * @param id category's id
     * @return number of transactions
     */
    long countByCategoryId(int id);

    /**
     * Counts transactions by given accounts.
     *
     * @param account         transaction's account
     * @param transferAccount transfer account in a case if transaction has operation TRANSFER
     * @return number of transactions
     */
    long countByAccountOrTransferAccount(Account account, Account transferAccount);

    /**
     * Returns all transactions by given user's email.
     *
     * @param email user's email
     * @param sort  sorting
     * @return list of transactions
     */
    List<Transaction> findAllByUserEmail(String email, Sort sort);

    /**
     * Returns all transactions by given category.
     *
     * @param category category
     * @return list of transactions
     */
    List<Transaction> findAllByCategory(Category category);

    /**
     * Returns all transactions by given account.
     *
     * @param account         transaction's account
     * @param transferAccount transfer account in a case if transaction has operation TRANSFER
     * @return list of transactions
     */
    List<Transaction> findAllByAccountOrTransferAccount(Account account, Account transferAccount);

    /**
     * Looks for transactions by user's email, which were created between given dates.
     *
     * @param email    user's email
     * @param fromDate date 'from'
     * @param toDate   date 'to'
     * @param sort     sorting
     * @return list of transactions
     */
    List<Transaction> findByUserEmailAndDateBetween(String email, Date fromDate, Date toDate, Sort sort);

    /**
     * Looks for transactions by user's email, which were created between given dates, and theirs 'comment' field
     * contains given string.
     *
     * @param email    user's email
     * @param fromDate date 'from'
     * @param toDate   date 'to'
     * @param comment  string, which 'comment' field has to contain, case ignored
     * @param sort     sorting
     * @return list of transactions
     */
    List<Transaction> findByUserEmailAndDateBetweenAndCommentLikeIgnoreCase(String email, Date fromDate, Date toDate,
                                                                            String comment, Sort sort);

    /**
     * Looks for transactions by given accounts, which were created between given dates, and theirs 'comment' field
     * contains given string.
     *
     * @param account         transaction's account
     * @param transferAccount transfer account in a case if transaction has operation TRANSFER
     * @param fromDate        date 'from'
     * @param toDate          date 'to'
     * @param comment         string, which 'comment' field has to contain, case ignored
     * @param sort            sorting
     * @return list of transactions
     */
    List<Transaction> findByAccountOrTransferAccountAndDateBetweenAndCommentLikeIgnoreCase(Account account,
                                                                                           Account transferAccount,
                                                                                           Date fromDate, Date toDate,
                                                                                           String comment, Sort sort);

    /**
     * Looks for transactions by given accounts, which were created between given dates.
     *
     * @param account         transaction's account
     * @param transferAccount transfer account in a case if transaction has operation TRANSFER
     * @param fromDate        date 'from'
     * @param toDate          date 'to'
     * @param sort            sorting
     * @return list of transactions
     */
    List<Transaction> findByAccountOrTransferAccountAndDateBetween(Account account, Account transferAccount,
                                                                   Date fromDate, Date toDate, Sort sort);
}
