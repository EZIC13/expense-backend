package com.auth;

public record UserCreate(
    String username,
    String password
) {}