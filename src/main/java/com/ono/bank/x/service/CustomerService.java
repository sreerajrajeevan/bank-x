package com.ono.bank.x.service;
import com.ono.bank.x.model.Account;
import com.ono.bank.x.model.Customer;
import com.ono.bank.x.repository.AccountRepository;
import com.ono.bank.x.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Create a new customer and accounts (Current + Savings)
    public Customer onboardCustomer(Customer customer) {
        // Saving customer information
        customer = customerRepository.save(customer);

        // Creating Current Account
        Account currentAccount = new Account();
        currentAccount.setAccountType("Current");
        currentAccount.setBalance(BigDecimal.ZERO);  // Initial bal
        currentAccount.setCustomer(customer);
        accountRepository.save(currentAccount);

        // Creating Savings Account with a R500 joining bonus
        Account savingsAccount = new Account();
        savingsAccount.setAccountType("Savings");
        savingsAccount.setBalance(BigDecimal.valueOf(500));
        savingsAccount.setCustomer(customer);
        accountRepository.save(savingsAccount);

        // Add accounts to customer
        customer.setAccounts(List.of(currentAccount, savingsAccount));
        return customerRepository.save(customer);
    }
}

