package com.ono.bank.x.controller;


import com.ono.bank.x.model.Customer;
import com.ono.bank.x.model.CustomerResponse;
import com.ono.bank.x.model.Transaction;
import com.ono.bank.x.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/onboard")
    public ResponseEntity<CustomerResponse> onboardCustomer(@RequestBody Customer customer) {
        CustomerResponse onboardedCustomer = customerService.onboardCustomer(customer);
        return ResponseEntity.ok(onboardedCustomer);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferMoney(@RequestParam Long fromAccountId,
                                                     @RequestParam Long toAccountId,
                                                     @RequestParam BigDecimal amount) {
        Transaction transaction = customerService.transferMoney(fromAccountId, toAccountId, amount);
        return ResponseEntity.ok(transaction);
    }
}


