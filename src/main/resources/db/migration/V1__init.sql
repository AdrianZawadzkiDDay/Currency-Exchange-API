DROP TABLE IF EXISTS account;

CREATE TABLE account (
id UUID PRIMARY KEY,
user_id INTEGER,
currency VARCHAR(4),
balance DECIMAL(20, 2)
);
