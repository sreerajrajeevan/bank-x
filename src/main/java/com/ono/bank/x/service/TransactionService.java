package com.ono.bank.x.service;

import com.ono.bank.x.model.Account;
import com.ono.bank.x.model.Transaction;
import com.ono.bank.x.repository.AccountRepository;
import com.ono.bank.x.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Handle deposit
    public Transaction deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        account.setBalance(account.getBalance().add(amount));

        // Create a deposit transaction record
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType("Deposit");
        transaction.setTimestamp(new Date());
        transaction.setAccount(account);
        transactionRepository.save(transaction);
        return transaction;
    }

    // Handle withdrawal
    public Transaction withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));

        // Create a withdrawal transaction record
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType("Withdrawal");
        transaction.setTimestamp(new Date());
        transaction.setAccount(account);

        transactionRepository.save(transaction);
        return transaction;
    }

    // Handle transfer between accounts
    public Transaction transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("From Account not found"));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("To Account not found"));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance for transfer");
        }

        // Perform transfer
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // Create a transfer transaction record
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType("Transfer");
        transaction.setTimestamp(new Date());
        transaction.setAccount(fromAccount);

        transactionRepository.save(transaction);
        return transaction;
    }
}

