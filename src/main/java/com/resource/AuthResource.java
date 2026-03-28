package com.resource;

import com.model.LoginRequest;
import com.model.Session;
import com.model.User;
import com.model.UserCreateRequest;
import com.service.AuthService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.mindrot.jbcrypt.BCrypt;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Path("/auth")
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response loginUser(final LoginRequest request) {
        User user = User.find("username", request.username()).firstResult();

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        if (!BCrypt.checkpw(request.password(), user.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        //todo hash the session token before storing in the database (also will need to hash before query in current-user route)
        final String sessionToken = authService.generateSessionToken();

        Session session = new Session(
            sessionToken,
            Instant.now().plus(1, ChronoUnit.DAYS),
            user
        );
        session.persist();

        final NewCookie sessionCookie = authService.generateSessionCookie(sessionToken);
        return Response.ok().cookie(sessionCookie).build();
    }

    @GET
    @Path("/current-user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@CookieParam("budget_session") final String sessionToken) {
        if (sessionToken == null || sessionToken.isBlank()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Session session = Session.find("token", sessionToken).firstResult();
        if (session == null || session.getExpires().isBefore(Instant.now())) {
            final NewCookie deleteCookie = authService.generateDeleteCookie();
            return Response.status(Response.Status.UNAUTHORIZED).cookie(deleteCookie).build();
        }

        String username = session.getUser().getUsername();
        return Response.ok(Map.of(
            "username", username
        )).build();
    }

    @POST
    @Path("/create-user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createUser(final UserCreateRequest request) {
        //todo refactor to enforce unique usernames
        User user = new User(
            request.username(),
            BCrypt.hashpw(request.password(), BCrypt.gensalt())
        );
        user.persist();

        return Response.ok().build();
    }

    @POST
    @Path("/logout")
    @Transactional
    public Response logoutUser(@CookieParam("budget_session") final String sessionToken) {
        if (sessionToken != null) {
            Session session = Session.find("token", sessionToken).firstResult();
            if (session != null) {
                session.delete();
            }
        }

        final NewCookie deleteCookie = authService.generateDeleteCookie();
        return Response.ok().cookie(deleteCookie).build();
    }
}