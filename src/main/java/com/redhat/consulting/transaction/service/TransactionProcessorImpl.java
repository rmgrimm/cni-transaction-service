package com.redhat.consulting.transaction.service;

import com.redhat.consulting.transaction.client.balance.api.TransactionApi;
import com.redhat.consulting.transaction.model.TransactionRecord;
import com.redhat.consulting.transaction.model.TransactionStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class TransactionProcessorImpl implements TransactionProcessor {

    private final TransactionService transactionService;

    // TODO(rgrimm): Adjust this API to the actual balance API when its available
    private final TransactionApi balanceApi;

    public TransactionProcessorImpl(TransactionService transactionService, TransactionApi balanceApi) {
        this.transactionService = transactionService;
        this.balanceApi = balanceApi;
    }

    @Transactional
    @Override
    public void processTransaction(String id) {
        TransactionRecord record = transactionService.getTransaction(id);

        // TODO(rgrimm): Check whether there's enough balance available

        if (true) {
            // TODO(rgrimm): Update the balance service
            transactionService.updateTransaction(record
                    .processTimestamp(OffsetDateTime.now())
                    .status(TransactionStatus.POSTED));
        }
    }
}
