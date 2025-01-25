# Bank X Application

This project is a Spring Boot application developed to address the requirements of Bank X's new Banking Application. The application supports customer onboarding, account management, transactions, and notification services.

## Features

1. **Customer Onboarding**:
    - Allows new customers to register.
    - Automatically creates both a Current Account and a Savings Account for each customer.
    - Credits the Savings Account with a joining bonus of R500.00 upon onboarding.

2. **Account Transactions**:
    - Supports moving money between Current and Savings accounts.
    - Only the Current Account can make payments to external accounts.
    - Payments to the Savings Account earn an interest of 0.5% of the current balance.
    - Payments from any account are charged a fee of 0.05% of the transaction amount.

3. **Transaction History**:
    - Keeps track of all transactions performed on the accounts.
    - Allows retrieval of transaction records.


## Technology Stack

- **Java 17**: Programming language.
- **Spring Boot 3.3.7**: Framework for building the application.
- **H2 Database**: Embedded database for local development.
- **Maven**: Build and dependency management tool.
- **Postman**: For testing API endpoints (optional).

## Prerequisites

1. JDK 17 or higher installed.
2. Maven installed.
3. Any IDE (e.g., IntelliJ IDEA, Eclipse) or terminal to run the application.

## Getting Started

### Clone the Repository
```bash
git clone [repository-url]
cd bankx-application
```

### Build the Application
Run the following Maven command to build the project:
```bash
mvn clean package
```

### Run the Application
Execute the JAR file generated in the `target` directory:
```bash
java -jar target/bankx-application.jar
```

The application will start on `http://localhost:8080` by default.

## API Endpoints

### 1. Customer Onboarding
**POST** `/bankx/customers/onboard`
- **Input**:
  ```json
  {
      "name": "Sreeraj",
      "email": "sree@example.com",
      "phoneNumber": "1234567890",
      "address": "sreedeepam kannur",
      "user": {
          "username": "sree",
          "password": "heySree"
      }
  }
  ```

### 2. Transfer Money Between Accounts
**POST** `/bankx/customers/transfer`
- **Input**:
  ```json
  {
      "amount": "2",
      "fromAccountId": "1",
      "toAccountId": "1"
  }
  ```

### 3. Get Account Details
**POST** `/bankx/customers/getAccount`
- **Input(Account number)**:
  ```
  1
  ```

## Project Structure

- `src/main/java`: Contains the application source code.
- `src/main/resources`: Includes configuration files like `application.properties`.
- `pom.xml`: Maven build configuration.

## TODO

1. Notifications needs to be implemented
2. Security implementations(currently disabled)
3. some validations needs to be added


---

For any questions or clarifications, please contact:
Sreeraj K
Sreerajrajeevan7@gmail.com
