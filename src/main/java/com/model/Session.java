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
import java.time.Instant;

@Entity
@Table(name = "sessions")
public class Session extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "token")
    private String token;

    @Column(name = "expires")
    private Instant expires;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Session() {}

    public Session(String token, Instant expires, User user) {
        this.token = token;
        this.expires = expires;
        this.user = user;
    }

    public String getId() { return id; }
    public String getToken() { return token; }
    public Instant getExpires() { return expires; }
    public User getUser() { return user; }

    public void setToken(String token) { this.token = token; }
    public void setExpires(Instant expires) { this.expires = expires; }
    public void setUser(User user) { this.user = user; }
}