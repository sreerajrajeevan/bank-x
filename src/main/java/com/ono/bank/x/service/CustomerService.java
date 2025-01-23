package com.ono.bank.x.service;

import com.ono.bank.x.enums.AccountType;
import com.ono.bank.x.enums.TransactionType;
import com.ono.bank.x.exception.CustomerAlreadyExistsException;
import com.ono.bank.x.model.Account;
import com.ono.bank.x.model.Customer;
import com.ono.bank.x.model.CustomerResponse;
import com.ono.bank.x.model.Transaction;
import com.ono.bank.x.repository.AccountRepository;
import com.ono.bank.x.repository.CustomerRepository;
import com.ono.bank.x.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Method to onboard a new customer with Current and Savings accounts
    public CustomerResponse onboardCustomer(Customer customer) {
        CustomerResponse customerResponse = new CustomerResponse();
        Optional<Customer> existingCustomer = customerRepository.findByEmail(customer.getEmail());
        if (existingCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("A customer with this email already exists: " + customer.getEmail());
        }
        // Save customer first
        customer = customerRepository.save(customer);

        // Create Current and Savings accounts
        Account currentAccount = new Account();
        currentAccount.setAccountType(AccountType.CURRENT);
        currentAccount.setBalance(BigDecimal.ZERO);
        currentAccount.setCustomer(customer);
        accountRepository.save(currentAccount);

        Account savingsAccount = new Account();
        savingsAccount.setAccountType(AccountType.SAVINGS);
        savingsAccount.setBalance(BigDecimal.valueOf(500));  // Joining bonus
        savingsAccount.setCustomer(customer);
        accountRepository.save(savingsAccount);

        // Add accounts to customer
        //customer.setAccounts(List.of(currentAccount, savingsAccount));
        customer.setAccounts(new ArrayList<>(List.of(currentAccount, savingsAccount)));  // Mutable List
        customerRepository.save(customer);
        customerResponse.setName(customer.getName());
        customerResponse.setEmail(customer.getEmail());
        customerResponse.setPhoneNumber(customer.getPhoneNumber());
        customerResponse.setAddress(customer.getAddress());
        customerResponse.setAccounts(customer.getAccounts());
        //customerResponse.setCurrentAccount(customer.getAccounts().get(0));
        //customerResponse.setSavingsAccount(customer.getAccounts().get(1));
        return customerResponse;
    }

    // Transfer money between accounts
    public Transaction transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Account fromAccount = accountRepository.findById(fromAccountId).orElseThrow();
        Account toAccount = accountRepository.findById(toAccountId).orElseThrow();

        // Apply transaction fees and transfer
        BigDecimal fee = amount.multiply(BigDecimal.valueOf(0.0005));  // 0.05% fee
        BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount).subtract(fee);
        fromAccount.setBalance(newFromBalance);

        BigDecimal newToBalance = toAccount.getBalance().add(amount);
        toAccount.setBalance(newToBalance);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Record the transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setFee(fee);
        transaction.setAccount(fromAccount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        return transaction;
    }

    // Method to apply interest to Savings Account deposits
    public void applyInterestToSavings() {
        List<Account> savingsAccounts = accountRepository.findByAccountType(AccountType.SAVINGS);
        for (Account account : savingsAccounts) {
            BigDecimal interest = account.getBalance().multiply(BigDecimal.valueOf(0.005));  // 0.5% interest
            account.setBalance(account.getBalance().add(interest));
            accountRepository.save(account);
        }
    }

}

