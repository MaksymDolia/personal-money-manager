package me.dolia.pmm.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import me.dolia.pmm.account.Account;
import me.dolia.pmm.category.Category;
import me.dolia.pmm.domain.Operation;
import me.dolia.pmm.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;


@Entity
@Table(name = "transactions")
@Data
@EqualsAndHashCode(of = "id")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @Column(name = "from_amount")
    private BigDecimal fromAmount;

    @Column(name = "from_currency", length = 3)
    private Currency fromCurrency;

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private Operation operation;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Column(name = "to_amount")
    private BigDecimal toAmount;

    @Column(name = "to_currency", length = 3)
    private Currency toCurrency;

    @Column(nullable = false)
    private LocalDateTime date;

    private String comment;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient boolean processed;

    public void process() {
        if (processed) throw new RuntimeException("The transaction is already processed.");
        switch (operation) {
            case EXPENSE:
                fromAccount.charge(fromAmount);
                break;
            case TRANSFER:
                fromAccount.charge(fromAmount);
                toAccount.deposit(toAmount);
                break;
            case INCOME:
                toAccount.deposit(toAmount);
                break;
            default:
                throw new RuntimeException("Error: not enough data to process transaction - operation missing");
        }
        processed = true;
    }

    public void cancel() {
        if (processed) throw new RuntimeException("The transaction is already processed.");
        switch (operation) {
            case EXPENSE:
                fromAccount.deposit(fromAmount);
                break;
            case TRANSFER:
                fromAccount.deposit(fromAmount);
                toAccount.charge(toAmount);
                break;
            case INCOME:
                toAccount.charge(toAmount);
                break;
            default:
                throw new RuntimeException("Error: not enough data to process transaction - operation missing");
        }
        processed = true;
    }
}