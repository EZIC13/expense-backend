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
    public Response createTransaction(final Transaction transaction) {
        if (transaction == null || transaction.id == null || transaction.id.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (Transaction.findById(transaction.id) != null) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        transaction.userId = currentUser.getId();
        transaction.persist();

        return Response.status(Response.Status.CREATED).entity(transaction).build();
    }

    @GET
    @Transactional
    public Response getAllTransactions() {
        final List<Transaction> transactions = Transaction.list("userId = ?1 order by transactionDate desc", currentUser.getId());
        return Response.ok(transactions).build();
    }

    @GET
    @Path("/{id}")
    @Transactional
    public Response getTransaction(@PathParam("id") final String transactionId) {
        final Transaction transaction = Transaction.find("id = ?1 and userId = ?2", transactionId, currentUser.getId()).firstResult();

        if (transaction == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(transaction).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response editTransaction(@PathParam("id") final String transactionId, final Transaction updatedTransaction) {
        final Transaction transaction = Transaction.find("id = ?1 and userId = ?2", transactionId, currentUser.getId()).firstResult();

        if (transaction == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        transaction.merchant = updatedTransaction.merchant;
        transaction.category = updatedTransaction.category;
        transaction.isIncome = updatedTransaction.isIncome;
        transaction.amountInCents = updatedTransaction.amountInCents;
        transaction.transactionDate = updatedTransaction.transactionDate;

        return Response.ok(transaction).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteTransaction(@PathParam("id") final String transactionId) {
        final long deleted = Transaction.delete("id = ?1 and userId = ?2", transactionId, currentUser.getId());
        return deleted > 0 ? Response.status(Response.Status.NO_CONTENT).build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}
