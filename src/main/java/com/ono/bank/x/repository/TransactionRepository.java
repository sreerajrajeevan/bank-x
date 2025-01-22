package com.ono.bank.x.repository;

import com.ono.bank.x.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
