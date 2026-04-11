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

        final NewCookie sessionCookie = authService.loginUserAndReturnCookie(user);
        return Response.ok().cookie(sessionCookie).build();
    }

    @GET
    @Path("/current-user")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getCurrentUser(@CookieParam("budget_session") final String sessionToken) {
        if (sessionToken == null || sessionToken.isBlank()) {
            return authService.generateUnauthorizedResponse();
        }

        final String hashedSessionToken = authService.hashSessionToken(sessionToken);
        Session session = Session.find("token", hashedSessionToken).firstResult();
        if (session == null) {
            return authService.generateUnauthorizedResponse();
        }

        if (session.getExpires().isBefore(Instant.now())) {
            session.delete();
            return authService.generateUnauthorizedResponse();
        }

        final String username = session.getUser().getUsername();
        return Response.ok(Map.of(
            "username", username
        )).build();
    }

    @POST
    @Path("/create-user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createUser(final UserCreateRequest request) {
        final String username = request.username();
        final User existingUser = User.find("username", username).firstResult();

        if (existingUser != null) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        User user = new User(
            request.username(),
            BCrypt.hashpw(request.password(), BCrypt.gensalt())
        );
        user.persist();

        final NewCookie sessionCookie = authService.loginUserAndReturnCookie(user);
        return Response.ok().cookie(sessionCookie).build();
    }

    @POST
    @Path("/logout")
    @Transactional
    public Response logoutUser(@CookieParam("budget_session") final String sessionToken) {
        if (sessionToken != null) {
            final String hashedSessionToken = authService.hashSessionToken(sessionToken);
            Session session = Session.find("token", hashedSessionToken).firstResult();
            if (session != null) {
                session.delete();
            }
        }

        final NewCookie deleteCookie = authService.generateDeleteCookie();
        return Response.ok().cookie(deleteCookie).build();
    }
}