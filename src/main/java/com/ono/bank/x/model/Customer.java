package com.ono.bank.x.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;  // Customer's name

    @Column(unique = true, nullable = false)
    private String email;  // Customer's email

    @NotNull(message = "Phone number cannot be null")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be a 10-digit number")
    private String phoneNumber;
    private String address;

    @OneToOne
    private User user;  // Link to the User entity for authentication

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Account> accounts;  // Customer's accounts (Current & Savings)

}

