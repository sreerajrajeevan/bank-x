package com.ono.bank.x.repository;

import com.ono.bank.x.enums.AccountType;
import com.ono.bank.x.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface AccountRepository extends JpaRepository<Account, Long>{
    Optional<Account> findByAccountTypeAndCustomerId(String accountType, Long customerId);

    List<Account> findByAccountType(AccountType accountType);
}
