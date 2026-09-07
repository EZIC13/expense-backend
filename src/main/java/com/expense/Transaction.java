package com.expense;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "transactions", schema = "expense")
@JsonIgnoreProperties(value = "userId", allowGetters = false, allowSetters = false)
public class Transaction extends PanacheEntityBase {

    /** Client-supplied. The backend stores it as given and rejects a duplicate with 409. */
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

    /** Server-controlled: set from the authenticated session, never read from the request body. */
    @JsonIgnore
    @Column(name = "user_id")
    public String userId;
}
