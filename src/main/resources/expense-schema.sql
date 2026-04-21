DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY UNIQUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS sessions (
    id VARCHAR(255) PRIMARY KEY UNIQUE,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    session_expiry TIMESTAMP NOT NULL,
    user_id VARCHAR(255) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(255) PRIMARY KEY UNIQUE,
    merchant VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    is_income BOOLEAN NOT NULL DEFAULT FALSE,
    amount_in_cents INTEGER NOT NULL,
    transaction_date DATE NOT NULL,
    user_id VARCHAR(255) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert test user (test/test)
INSERT INTO users (id, username, password) VALUES ('32da5c15-4b87-43cd-b5a2-af19102e6354', 'test', '$2a$10$xJcZl1Tur1ms9OenFitwf.X6xbI/B/FhCSWNW.Za8GV0DiU.Hwgb.');