package com.model;

public record CreateTransactionRequest (
    String merchant,
    String category,
    int amount,
    String date
) {}
