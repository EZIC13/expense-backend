package com.auth;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import java.time.Instant;

@Entity
@Table(name = "sessions", schema = "auth")
public class Session extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    public String id;

    @Column(name = "session_token")
    public String sessionToken;

    @Column(name = "session_expiry")
    public Instant sessionExpiry;

    @Column(name = "user_id")
    public String userId;

    public Session() {}

    public Session(String sessionToken, Instant expiry, String userId) {
        this.sessionToken = sessionToken;
        this.sessionExpiry = expiry;
        this.userId = userId;
    }
}