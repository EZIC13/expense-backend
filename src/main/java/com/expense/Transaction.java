package com.expense;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "transactions", schema = "expense")
public class Transaction extends PanacheEntityBase {

    @Id
    @Column(name = "id")
    public String id;

    @Column(name = "merchant")
    public String merchant;

    @Column(name = "category")
    public String category;

    @Column(name = "is_income")
    public boolean isIncome;

    @Column(name = "amount_in_cents")
    public int amountInCents;

    @Column(name = "transaction_date")
    public LocalDate transactionDate;

    @Column(name = "user_id")
    public String userId;

    public Transaction() {}

    public Transaction(String id, String merchant, String category, boolean isIncome, int amountInCents, LocalDate transactionDate, String userId) {
        this.id = id;
        this.merchant = merchant;
        this.category = category;
        this.isIncome = isIncome;
        this.amountInCents = amountInCents;
        this.transactionDate = transactionDate;
        this.userId = userId;
    }
}
