package com.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    @Column(name = "merchant")
    private String merchant;

    @Column(name = "category")
    private String category;

    @Column(name = "amount_in_cents")
    private int amountInCents;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Transaction() {}

    public Transaction(String merchant, String category, int amountInCents, LocalDate transactionDate, User user) {
        this.merchant = merchant;
        this.category = category;
        this.amountInCents = amountInCents;
        this.transactionDate = transactionDate;
        this.user = user;
    }

    public String getId() { return id; }
    public String getMerchant() { return merchant; }
    public String getCategory() { return category; }
    public int getAmountInCents() { return amountInCents; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public User getUser() { return user; }

    public void setMerchant(String merchant) { this.merchant = merchant; }
    public void setCategory(String category) { this.category = category; }
    public void setAmountInCents(int amountInCents) { this.amountInCents = amountInCents; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = this.transactionDate; }
    public void setUser(User user) { this.user = user; }
}
