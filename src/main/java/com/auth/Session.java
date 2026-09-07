package com.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.Instant;

@Entity
@Table(name = "sessions", schema = "auth")
public class Session extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    public String id;

    @JsonIgnore
    @Column(name = "session_token")
    public String sessionToken;

    @Column(name = "session_expiry")
    public Instant sessionExpiry;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    public Session() {}

    public Session(String sessionToken, Instant expiry, User user) {
        this.sessionToken = sessionToken;
        this.sessionExpiry = expiry;
        this.user = user;
    }
}