package com.resource;

import com.model.LoginRequest;
import com.model.Session;
import com.model.User;
import com.model.UserCreateRequest;
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
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.mindrot.jbcrypt.BCrypt;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Map;

@Path("/auth")
public class AuthResource {

    @ConfigProperty(name = "api_env")
    String api_env;

    @ConfigProperty(name = "domain")
    String domain;

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

        //todo new method in AuthService to generate a session token
        //todo store hashed token in database (also will need to hash before query in current-user route)
        byte[] randombytes = new byte[32];
        new SecureRandom().nextBytes(randombytes);
        final String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randombytes);

        //todo make an AuthService to handle session crud ops
        Session session = new Session(
            token,
            Instant.now().plus(1, ChronoUnit.DAYS),
            user
        );
        session.persist();

        //todo this will also be in AuthService
        boolean isSecureCookie = !api_env.equals("dev");
        NewCookie.SameSite sameSite = api_env.equals("dev") ? NewCookie.SameSite.NONE : NewCookie.SameSite.LAX;

        NewCookie cookie = new NewCookie.Builder("budget_session")
            .value(token)
            .path("/")
            .domain(domain)
            .httpOnly(true)
            .secure(isSecureCookie)
            .sameSite(sameSite)
            .maxAge(24 * 60 * 60) //one day
            .build();

        return Response.ok().cookie(cookie).build();
    }

    @GET
    @Path("/current-user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@CookieParam("budget_session") String token) {
        if (token == null || token.isBlank()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Session session = Session.find("token", token).firstResult();
        if (session == null || session.getExpires().isBefore(Instant.now())) {
            boolean isSecureCookie = !api_env.equals("dev");
            NewCookie.SameSite sameSite = api_env.equals("dev") ? NewCookie.SameSite.NONE : NewCookie.SameSite.LAX;

            NewCookie deleteCookie = new NewCookie.Builder("budget_session")
                    .value("")
                    .path("/")
                    .domain(domain)
                    .httpOnly(true)
                    .secure(isSecureCookie)
                    .sameSite(sameSite)
                    .maxAge(0)
                    .build();

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
        //todo make a UserService to handle user crud ops
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
    public Response logoutUser(@CookieParam("budget_session") String token) {
        boolean isSecureCookie = !api_env.equals("dev");
        NewCookie.SameSite sameSite = api_env.equals("dev") ? NewCookie.SameSite.NONE : NewCookie.SameSite.LAX;

        NewCookie deleteCookie = new NewCookie.Builder("budget_session")
                .value("")
                .path("/")
                .domain(domain)
                .httpOnly(true)
                .secure(isSecureCookie)
                .sameSite(sameSite)
                .maxAge(0)
                .build();

        if (token != null) {
            Session session = Session.find("token", token).firstResult();
            if (session != null) {
                session.delete();
            }
        }

        return Response.ok().cookie(deleteCookie).build();
    }
}