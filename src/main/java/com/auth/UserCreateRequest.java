package com.auth;

public record UserCreateRequest (
    String username,
    String password
) {}