package com.expense;

import java.time.LocalDate;

public record TransactionDto(
    String id, //Client supplied (for now, will want the backend to supply this I think)
    String merchant,
    String category,
    boolean isIncome,
    int amountInCents,
    LocalDate transactionDate
) {
    static TransactionDto from(Transaction transaction) {
        return new TransactionDto(
            transaction.id,
            transaction.merchant,
            transaction.category,
            transaction.isIncome,
            transaction.amountInCents,
            transaction.transactionDate
        );
    }
}