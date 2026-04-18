package com.model;

import java.time.LocalDate;

public record TransactionResponse(
    String id,
    String merchant,
    String category,
    boolean isIncome,
    int amountInCents,
    LocalDate transactionDate
) {}
