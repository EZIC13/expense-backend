package com.model;

public record CreateTransactionRequest (
    String merchant,
    String category,
    boolean isIncome,
    int amountInCents,
    String transactionDate
) {}
