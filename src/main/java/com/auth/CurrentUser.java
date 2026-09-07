package com.auth;

import jakarta.enterprise.context.RequestScoped;

/**
 * Holds the authenticated user for the current request, populated by
 * {@link AuthenticationFilter}. Only valid on requests that reach an
 * {@link Authenticated} resource method.
 */
@RequestScoped
public class CurrentUser {

    private String id;
    private String username;

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    void set(final String id, final String username) {
        this.id = id;
        this.username = username;
    }
}
