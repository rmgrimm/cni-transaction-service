package com.redhat.consulting.transaction.service;

import com.redhat.consulting.transaction.model.TransactionRecord;
import com.redhat.consulting.transaction.model.TransactionRequest;
import com.redhat.consulting.transaction.model.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {

    TransactionRecord createTransaction(TransactionRequest transactionRequest);

    void deleteTransaction(String transactionId);

    TransactionRecord getTransaction(String transactionId);

    long countTransactions(List<String> sourceAccount, List<String> sourceInstitution, List<String> targetAccount, List<String> targetInstitution, List<Double> valueAmount, List<String> valueCurrency, List<TransactionStatus> status);

    Page<TransactionRecord> listTransactions(List<String> sourceAccount, List<String> sourceInstitution, List<String> targetAccount, List<String> targetInstitution, List<Double> valueAmount, List<String> valueCurrency, List<TransactionStatus> status, Pageable pageable);

    void updateTransaction(TransactionRecord transactionRecord);

}
