package com.model;

public record CreateTransactionRequest (
    String merchant,
    String category,
    int amountInCents,
    String transactionDate
) {}
