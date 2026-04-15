package com.service;

import com.model.Session;
import com.model.User;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    public String hashSessionToken(final String sessionToken) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hashedBytes = digest.digest(sessionToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
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
            .maxAge(8 * 60 * 60) //8 hours
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

    public NewCookie loginUserAndReturnCookie(final User user) {
        final String sessionToken = this.generateSessionToken();
        final String hashedSessionToken = this.hashSessionToken(sessionToken);

        Session session = new Session(
            hashedSessionToken,
            Instant.now().plus(8, ChronoUnit.HOURS),
            user
        );
        session.persist();

        return this.generateSessionCookie(sessionToken);
    }

    public Response generateUnauthorizedResponse() {
        final NewCookie deleteCookie = this.generateDeleteCookie();
        return Response.status(Response.Status.UNAUTHORIZED).cookie(deleteCookie).build();
    }

    public User getUserFromSessionToken(final String sessionToken) {
        final String hashedSessionToken = this.hashSessionToken(sessionToken);
        Session session = Session.find("token", hashedSessionToken).firstResult();

        if (session == null) {
            return null;
        }

        if (session.getExpires().isBefore(Instant.now())) {
            session.delete();
            return null;
        }

        return session.getUser();
    }

    @Scheduled(every = "30m")
    @Transactional
    void deleteExpiredSessions() {
        Session.delete("expires < ?1", Instant.now());
    }
}
