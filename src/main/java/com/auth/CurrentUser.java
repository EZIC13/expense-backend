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

    public String getId() {
        return id;
    }

    void set(final String id) {
        this.id = id;
    }
}
