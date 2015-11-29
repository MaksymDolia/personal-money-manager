package me.dolia.pmm.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

/**
 * Entity class, represents finance transaction.
 *
 * @author Maksym Dolia
 */
@Entity
public class Transaction {

    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private Operation operation;
    @Digits(fraction = 2, integer = 9)
    private BigDecimal amount;
    private Currency currency;

    @ManyToOne
    private Account account;

    // TRANSFER FIELDS
    @Digits(fraction = 2, integer = 9)
    private BigDecimal transferAmount;
    private Currency transferCurrency;

    @ManyToOne
    private Account transferAccount;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date = new Date();
    private String comment;

    @OneToOne
    private Category category;

    @ManyToOne
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Operation getType() {
        return operation;
    }

    public void setType(Operation operation) {
        this.operation = operation;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal ammountFrom) {
        this.amount = ammountFrom;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currencyFrom) {
        this.currency = currencyFrom;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal ammountTo) {
        this.transferAmount = ammountTo;
    }

    public Currency getTransferCurrency() {
        return transferCurrency;
    }

    public void setTransferCurrency(Currency currencyTo) {
        this.transferCurrency = currencyTo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getTransferAccount() {
        return transferAccount;
    }

    public void setTransferAccount(Account transferAccount) {
        this.transferAccount = transferAccount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void copy(Transaction transaction) {
        this.setAccount(transaction.getAccount());
        this.setAmount(transaction.getAmount());
        this.setCategory(transaction.getCategory());
        this.setComment(transaction.getComment());
        this.setCurrency(transaction.getCurrency());
        this.setDate(transaction.getDate());
        this.setOperation(transaction.getOperation());
        this.setTransferAccount(transaction.getTransferAccount());
        this.setTransferAmount(transaction.getTransferAmount());
        this.setTransferCurrency(transaction.getTransferCurrency());
    }

}
