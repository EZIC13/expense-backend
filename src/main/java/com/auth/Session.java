package com.auth;

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
    private String id;

    @Column(name = "session_token")
    private String session_token;

    @Column(name = "session_expiry")
    private Instant session_expiry;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Session() {}

    public Session(String session_token, Instant expires, User user) {
        this.session_token = session_token;
        this.session_expiry = expires;
        this.user = user;
    }

    public String getId() { return id; }
    public String getToken() { return session_token; }
    public Instant getSessionExpiry() { return session_expiry; }
    public User getUser() { return user; }

    public void setSessionToken(String session_token) { this.session_token = session_token; }
    public void setSessionExpiry(Instant session_expiry) { this.session_expiry = session_expiry; }
    public void setUser(User user) { this.user = user; }
}