package com.ono.bank.x.exception;

public class TransactionException extends RuntimeException{
    public TransactionException(String message) {
        super(message);
    }
}
