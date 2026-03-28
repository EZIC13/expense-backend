package com.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.NewCookie;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.SecureRandom;
import java.util.Base64;

@ApplicationScoped
public class AuthService {

    @ConfigProperty(name = "api_env")
    String api_env;

    @ConfigProperty(name = "domain")
    String domain;

    public String generateSessionToken() {
        byte[] randombytes = new byte[32];
        new SecureRandom().nextBytes(randombytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randombytes);
    }

    public NewCookie generateSessionCookie(final String sessionToken) {
        final boolean isSecureCookie = !api_env.equals("dev");
        final NewCookie.SameSite sameSite = api_env.equals("dev") ? NewCookie.SameSite.NONE : NewCookie.SameSite.LAX;

        return new NewCookie.Builder("budget_session")
            .value(sessionToken)
            .path("/")
            .domain(domain)
            .httpOnly(true)
            .secure(isSecureCookie)
            .sameSite(sameSite)
            .maxAge(24 * 60 * 60) //one day
            .build();
    }

    public NewCookie generateDeleteCookie() {
        final boolean isSecureCookie = !api_env.equals("dev");
        final NewCookie.SameSite sameSite = api_env.equals("dev") ? NewCookie.SameSite.NONE : NewCookie.SameSite.LAX;

        return new NewCookie.Builder("budget_session")
            .value("")
            .path("/")
            .domain(domain)
            .httpOnly(true)
            .secure(isSecureCookie)
            .sameSite(sameSite)
            .maxAge(0)
            .build();
    }
}
