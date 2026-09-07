package com.auth;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.ext.Provider;

/**
 * Resolves the {@code budget_session} cookie into a {@link CurrentUser} for
 * any resource annotated {@link Authenticated}, or aborts the request with
 * 401 if the cookie is missing, invalid, or expired.
 */
@Provider
@Authenticated
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Inject
    AuthService authService;

    @Inject
    CurrentUser currentUser;

    @Override
    @Transactional
    public void filter(final ContainerRequestContext requestContext) {
        final Cookie cookie = requestContext.getCookies().get("budget_session");
        final String sessionToken = cookie != null ? cookie.getValue() : null;

        if (sessionToken == null || sessionToken.isBlank()) {
            requestContext.abortWith(authService.generateUnauthorizedResponse());
            return;
        }

        final User user = authService.getUserFromSessionToken(sessionToken);
        if (user == null) {
            requestContext.abortWith(authService.generateUnauthorizedResponse());
            return;
        }

        currentUser.set(user.getId(), user.getUsername());
    }
}
