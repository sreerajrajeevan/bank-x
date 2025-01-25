package com.ono.bank.x.service;

import com.ono.bank.x.dto.TransferRequest;
import com.ono.bank.x.enums.AccountType;
import com.ono.bank.x.enums.TransactionType;
import com.ono.bank.x.exception.CustomerException;
import com.ono.bank.x.exception.TransactionException;
import com.ono.bank.x.model.Account;
import com.ono.bank.x.model.Customer;
import com.ono.bank.x.dto.CustomerResponse;
import com.ono.bank.x.model.Transaction;
import com.ono.bank.x.repository.AccountRepository;
import com.ono.bank.x.repository.CustomerRepository;
import com.ono.bank.x.repository.TransactionRepository;
import com.ono.bank.x.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BankService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    // Method to onboard a new customer with Current and Savings accounts
    public CustomerResponse onboardCustomer(Customer customer) {
        CustomerResponse customerResponse = new CustomerResponse();
        Optional<Customer> existingCustomer = customerRepository.findByEmail(customer.getEmail());
        if (existingCustomer.isPresent()) {
            throw new CustomerException("A customer with this email already exists: " + customer.getEmail());
        }
        if(customer.getUser()!=null){
            String encodedPassword = passwordEncoder.encode(customer.getUser().getPassword());
            customer.getUser().setPassword(encodedPassword);
            userRepository.save(customer.getUser());
        }
        customer = customerRepository.save(customer);

        // curr account
        Account currentAccount = new Account();
        currentAccount.setAccountType(AccountType.CURRENT);
        currentAccount.setBalance(BigDecimal.ZERO);
        currentAccount.setCustomer(customer);
        accountRepository.save(currentAccount);
        // sav acc
        Account savingsAccount = new Account();
        savingsAccount.setAccountType(AccountType.SAVINGS);
        savingsAccount.setBalance(BigDecimal.valueOf(500));
        savingsAccount.setCustomer(customer);
        accountRepository.save(savingsAccount);

        //customer.setAccounts(List.of(currentAccount, savingsAccount));
        customer.setAccounts(new ArrayList<>(List.of(currentAccount, savingsAccount)));
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

    public Transaction transferMoney(TransferRequest transferRequest) {
        Account fromAccount = accountRepository.findById(transferRequest.getFromAccountId()).orElseThrow();
        Account toAccount = accountRepository.findById(transferRequest.getToAccountId()).orElseThrow();
        BigDecimal amount = transferRequest.getAmount();
        if(Objects.equals(fromAccount.getId(), toAccount.getId())){
            throw new TransactionException("You cannot transfer money to your own account");
        }
        if ("SAVINGS".equals(fromAccount.getAccountType())) {
            throw new TransactionException("You cannot transfer from savings account.");
        }

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

