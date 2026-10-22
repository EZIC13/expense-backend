package com.expense;

import com.auth.Authenticated;
import com.auth.CurrentUser;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/transactions")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @Inject
    CurrentUser currentUser;

    @POST
    @Transactional
    public Response createTransaction(final TransactionDto transactionDto) {
        if (transactionDto == null || transactionDto.id() == null || transactionDto.id().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (Transaction.findById(transactionDto.id()) != null) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        final Transaction transaction = new Transaction(
            transactionDto.id(),
            transactionDto.merchant(),
            transactionDto.category(),
            transactionDto.isIncome(),
            transactionDto.amountInCents(),
            transactionDto.transactionDate(),
            currentUser.getId()
        );

        transaction.persist();

        return Response.status(Response.Status.CREATED).entity(TransactionDto.from(transaction)).build();
    }

    @GET
    @Transactional
    public Response getAllTransactions() {
        final List<Transaction> transactions = Transaction.list("userId = ?1 order by transactionDate desc", currentUser.getId());
        final List<TransactionDto> transactionDtos = transactions.stream().map(TransactionDto::from).toList();
        return Response.ok(transactionDtos).build();
    }

    @GET
    @Path("/{id}")
    @Transactional
    public Response getTransaction(@PathParam("id") final String transactionId) {
        final Transaction transaction = Transaction.find("id = ?1 and userId = ?2", transactionId, currentUser.getId()).firstResult();

        if (transaction == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(TransactionDto.from(transaction)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response editTransaction(@PathParam("id") final String transactionId, final TransactionDto transactionDto) {
        final Transaction transaction = Transaction.find("id = ?1 and userId = ?2", transactionId, currentUser.getId()).firstResult();

        if (transaction == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        transaction.merchant = transactionDto.merchant();
        transaction.category = transactionDto.category();
        transaction.isIncome = transactionDto.isIncome();
        transaction.amountInCents = transactionDto.amountInCents();
        transaction.transactionDate = transactionDto.transactionDate();

        return Response.ok(TransactionDto.from(transaction)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteTransaction(@PathParam("id") final String transactionId) {
        final long deleted = Transaction.delete("id = ?1 and userId = ?2", transactionId, currentUser.getId());
        return deleted > 0 ? Response.status(Response.Status.NO_CONTENT).build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}
