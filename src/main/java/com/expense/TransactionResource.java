package com.expense;

import com.auth.Authenticated;
import com.auth.CurrentUser;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Path("/transactions")
@Authenticated
public class TransactionResource {

    @Inject
    CurrentUser currentUser;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createTransaction(final TransactionRequest transactionRequest) {
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
            currentUser.getId()
        );
        newTransaction.persist();

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getAllTransactions() {
        final List<Transaction> transactions = Transaction.list("userId = ?1 order by transactionDate desc", currentUser.getId());

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
    public Response getTransaction(@PathParam("id") final String transactionId) {
        final Transaction transaction = Transaction.find("id = ?1 and userId = ?2", transactionId, currentUser.getId()).firstResult();

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
    public Response editTransaction(@PathParam("id") final String transactionId, final TransactionRequest transactionRequest) {
        final Transaction transaction = Transaction.find("id = ?1 and userId = ?2", transactionId, currentUser.getId()).firstResult();
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

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteTransaction(@PathParam("id") final String transactionId) {
        final Transaction transaction = Transaction.find("id = ?1 and userId = ?2", transactionId, currentUser.getId()).firstResult();
        if (transaction == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        transaction.delete();

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
