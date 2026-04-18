package com.resource;

import com.model.CreateTransactionRequest;
import com.model.User;
import com.model.Transaction;
import com.service.AuthService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Path("/transactions")
public class TransactionResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/create-transaction")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createTransaction(@CookieParam("budget_session") final String sessionToken, final CreateTransactionRequest transactionRequest) {
        if (sessionToken == null || sessionToken.isBlank()) {
            return authService.generateUnauthorizedResponse();
        }

        final User user = authService.getUserFromSessionToken(sessionToken);
        if (user == null) {
            return authService.generateUnauthorizedResponse();
        }

        if (transactionRequest == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        final LocalDate transactionDate;
        try {
            transactionDate = LocalDate.parse(transactionRequest.transactionDate());
        } catch (DateTimeParseException | NullPointerException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        final Transaction newTransaction = new Transaction(
            transactionRequest.merchant(),
            transactionRequest.category(),
            transactionRequest.isIncome(),
            transactionRequest.amountInCents(),
            transactionDate,
            user
        );
        newTransaction.persist();

        return Response.status(Response.Status.CREATED).build();
    }
}
