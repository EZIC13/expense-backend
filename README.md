# Expense Tracker Backend
This repository contains the backend API for the Expense Tracker application. This service provides authentication and expense tracking functionality for the frontend client.

Frontend repository [here](https://github.com/EZIC13/expense-frontend).

## Project Overview
This project is a full-stack web application that allows users to track and categorize personal expenses.

## Tech Stack

### API
- Java
- Quarkus
- PostgreSQL

### Deployment
- Docker Compose
- Caddy (Reverse Proxy)
- GitHub Actions

## Current Features
Authentication is the primary focus of the current development stage.
Expense tracking endpoints and related functionality will be added next.

### Authentication
Authentication is implemented using server-side sessions. Current authentication features include:
- Passwords hashed and salted with BCrypt
- Session tokens are generated with `SecureRandom`
- Tokens are stored in the database with an expiration timestamp
- Cookies are marked `Secure` in production with `HttpOnly`, and share the same expiration as the session tokens
- Session tokens are hashed (SHA-256) in the database
- Backend cleanup job to clear expired session tokens every 30 minutes

[//]: # (Some of these improvements are noted with `// TODO` comments in the codebase. This README will be updated as development progresses.)

## Running Locally
Run the development server using Quarkus: `./mvnw quarkus:dev`

The API will be available at http://localhost:8080