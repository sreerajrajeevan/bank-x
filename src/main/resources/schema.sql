-- Create the Customer table
CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the Account table
CREATE TABLE account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    account_type VARCHAR(255) NOT NULL, -- 'CURRENT' or 'SAVINGS'
    balance DECIMAL(15, 2) DEFAULT 0.00,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

-- Create the Transaction table
CREATE TABLE transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_account_id BIGINT,
    to_account_id BIGINT,
    amount DECIMAL(15, 2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transaction_type VARCHAR(255) NOT NULL, -- 'DEPOSIT', 'WITHDRAWAL', 'TRANSFER'
    fee DECIMAL(15, 2) DEFAULT 0.00,
    FOREIGN KEY (from_account_id) REFERENCES account(id),
    FOREIGN KEY (to_account_id) REFERENCES account(id)
);

-- Create the Notification table
CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

-- Insert a sample customer with initial accounts
INSERT INTO customer (first_name, last_name, email, phone_number) 
VALUES ('John', 'Doe', 'john.doe@example.com', '123-456-7890');

-- Create Current and Savings accounts for the new customer
INSERT INTO account (customer_id, account_type, balance) 
VALUES (1, 'CURRENT', 0.00), (1, 'SAVINGS', 500.00);

-- Insert a transaction (e.g., joining bonus to Savings account)
INSERT INTO transaction (from_account_id, to_account_id, amount, transaction_type, fee)
VALUES (null, 2, 500.00, 'DEPOSIT', 0.00);  -- Joining bonus for Savings account

-- Optionally, insert a notification for the customer about the joining bonus
INSERT INTO notification (customer_id, message) 
VALUES (1, 'Your Savings account has been credited with a joining bonus of R500.00.');
