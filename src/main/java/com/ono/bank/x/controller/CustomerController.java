package com.ono.bank.x.controller;


import com.ono.bank.x.model.Customer;
import com.ono.bank.x.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/onboard")
    public ResponseEntity<Customer> onboardCustomer(@RequestBody Customer customer) {
        Customer onboardedCustomer = customerService.onboardCustomer(customer);
        return ResponseEntity.ok(onboardedCustomer);
    }
}

