package com.redhat.consulting.transaction.exception;

public class TransactionNotFoundException extends RuntimeException {
    private final String transactionId;

    public TransactionNotFoundException(String transactionId) {
        super("Transaction not found: " + transactionId);
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
