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

@Entity
@Table(name = "transactions")
public class Transaction extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    @Column(name = "merchant")
    private String merchant;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Transaction() {}

    public Transaction(String merchant, User user) {
        this.merchant = merchant;
        this.user = user;
    }

    public String getId() { return id; }
    public String getMerchant() { return merchant; }
    public User getUser() { return user; }

    public void setMerchant(String merchant) { this.merchant = merchant; }
    public void setUser(User user) { this.user = user; }
}