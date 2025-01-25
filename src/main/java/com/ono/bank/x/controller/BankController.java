package com.ono.bank.x.controller;


import com.ono.bank.x.dto.TransferRequest;
import com.ono.bank.x.model.Customer;
import com.ono.bank.x.dto.CustomerResponse;
import com.ono.bank.x.model.Transaction;
import com.ono.bank.x.service.BankService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bankx/customers")
public class BankController {

    @Autowired
    private BankService bankService;

    @PostMapping("/onboard")
    public ResponseEntity<CustomerResponse> onboardCustomer(@Valid @RequestBody Customer customer) {
        CustomerResponse onboardedCustomer = bankService.onboardCustomer(customer);
        return ResponseEntity.ok(onboardedCustomer);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferMoney(@Validated @RequestBody TransferRequest transferRequest) {
        Transaction transaction = bankService.transferMoney(transferRequest);
        return ResponseEntity.ok(transaction);
    }


}


