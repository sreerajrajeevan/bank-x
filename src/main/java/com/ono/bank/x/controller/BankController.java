package com.ono.bank.x.controller;


import com.ono.bank.x.dto.AccountDetails;
import com.ono.bank.x.dto.TransactionResponse;
import com.ono.bank.x.dto.TransferRequest;
import com.ono.bank.x.model.AppUser;
import com.ono.bank.x.model.Customer;
import com.ono.bank.x.dto.CustomerResponse;
import com.ono.bank.x.model.Transaction;
import com.ono.bank.x.service.BankService;
import com.ono.bank.x.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bankx/customers")
public class BankController {

    @Autowired
    private BankService bankService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/onboard")
    public ResponseEntity<CustomerResponse> onboardCustomer(@Valid @RequestBody Customer customer) {
        CustomerResponse onboardedCustomer = bankService.onboardCustomer(customer);
        return ResponseEntity.ok(onboardedCustomer);
    }
    @PostMapping("/getAccount")
    public ResponseEntity<AccountDetails> getAccountData(@RequestBody Long accountNumber) {
        if (accountNumber == null || accountNumber <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);  // Return a 400 Bad Request response if invalid
        }
        AccountDetails onboardedCustomer = bankService.getAccountById(accountNumber);
        return ResponseEntity.ok(onboardedCustomer);
    }


    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transferMoney(@Validated @RequestBody TransferRequest transferRequest) {
        TransactionResponse transaction = bankService.transferMoney(transferRequest);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AppUser customer) {
        if (bankService.validateCustomerCredentials(customer)) {
            String token = jwtUtil.generateToken(customer.getUsername());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }


}


