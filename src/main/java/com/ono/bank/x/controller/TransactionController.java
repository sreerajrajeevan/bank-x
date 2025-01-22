package com.ono.bank.x.controller;

import com.ono.bank.x.model.Transaction;
import com.ono.bank.x.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit/{accountId}")
    public Transaction deposit(@PathVariable Long accountId, @RequestBody BigDecimal amount) {
        return transactionService.deposit(accountId, amount);
    }

    @PostMapping("/withdraw/{accountId}")
    public Transaction withdraw(@PathVariable Long accountId, @RequestBody BigDecimal amount) {
        return transactionService.withdraw(accountId, amount);
    }
    @PostMapping("/transfer")
    public Transaction transfer(@RequestParam Long fromAccountId, @RequestParam Long toAccountId, @RequestBody BigDecimal amount) {
        return transactionService.transfer(fromAccountId, toAccountId, amount);
    }
}

