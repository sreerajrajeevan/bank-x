package com.ono.bank.x.repository;

import com.ono.bank.x.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface AccountRepository extends JpaRepository<Account, Long>{
}
