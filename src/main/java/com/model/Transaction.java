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

    @Column(name = "amount")
    private int amount;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Transaction() {}

    public Transaction(String merchant, String category, int amount, LocalDate date, User user) {
        this.merchant = merchant;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.user = user;
    }

    public String getId() { return id; }
    public String getMerchant() { return merchant; }
    public String getCategory() { return category; }
    public int getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public User getUser() { return user; }

    public void setMerchant(String merchant) { this.merchant = merchant; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setUser(User user) { this.user = user; }
}