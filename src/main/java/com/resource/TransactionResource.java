package com.resource;

import com.model.TransactionRequest;
import com.model.Transaction;
import com.model.TransactionResponse;
import com.model.User;
import com.service.AuthService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Path("/transactions")
public class TransactionResource {

    @Inject
    AuthService authService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createTransaction(@CookieParam("budget_session") final String sessionToken, final TransactionRequest transactionRequest) {
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getAllTransactions(@CookieParam("budget_session") final String sessionToken) {
        if (sessionToken == null || sessionToken.isBlank()) {
            return authService.generateUnauthorizedResponse();
        }

        final User user = authService.getUserFromSessionToken(sessionToken);
        if (user == null) {
            return authService.generateUnauthorizedResponse();
        }

        final List<Transaction> transactions = Transaction.list("user = ?1 order by transactionDate desc", user);

        final List<TransactionResponse> response = transactions.stream()
            .map(transaction -> new TransactionResponse(
                transaction.getId(),
                transaction.getMerchant(),
                transaction.getCategory(),
                transaction.getIsIncome(),
                transaction.getAmountInCents(),
                transaction.getTransactionDate()
            ))
            .toList();

        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getTransaction(@CookieParam("budget_session") final String sessionToken, @PathParam("id") final String transactionId) {
        if (sessionToken == null || sessionToken.isBlank()) {
            return authService.generateUnauthorizedResponse();
        }

        final User user = authService.getUserFromSessionToken(sessionToken);
        if (user == null) {
            return authService.generateUnauthorizedResponse();
        }

        final Transaction transaction = Transaction.find("id = ?1 and user = ?2", transactionId, user).firstResult();

        if (transaction == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final TransactionResponse response = new TransactionResponse(
            transaction.getId(),
            transaction.getMerchant(),
            transaction.getCategory(),
            transaction.getIsIncome(),
            transaction.getAmountInCents(),
            transaction.getTransactionDate()
        );

        return Response.ok(response).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response editTransaction(@CookieParam("budget_session") final String sessionToken, @PathParam("id") final String transactionId, final TransactionRequest transactionRequest) {
        if (sessionToken == null || sessionToken.isBlank()) {
            return authService.generateUnauthorizedResponse();
        }

        final User user = authService.getUserFromSessionToken(sessionToken);
        if (user == null) {
            return authService.generateUnauthorizedResponse();
        }

        final Transaction transaction = Transaction.find("id = ?1 and user = ?2", transactionId, user).firstResult();
        if (transaction == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final LocalDate transactionDate;
        try {
            transactionDate = LocalDate.parse(transactionRequest.transactionDate());
        } catch (DateTimeParseException | NullPointerException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        transaction.setMerchant(transactionRequest.merchant());
        transaction.setCategory(transactionRequest.category());
        transaction.setIsIncome(transactionRequest.isIncome());
        transaction.setAmountInCents(transactionRequest.amountInCents());
        transaction.setTransactionDate(transactionDate);

        return Response.ok().build();
    }
}