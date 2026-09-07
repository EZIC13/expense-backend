package com.expense;

public record TransactionRequest(
    String merchant,
    String category,
    boolean isIncome,
    int amountInCents,
    String transactionDate
) {}
