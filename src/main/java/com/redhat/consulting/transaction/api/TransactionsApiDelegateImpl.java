package com.redhat.consulting.transaction.api;

import com.redhat.consulting.transaction.model.PagedTransactionRecords;
import com.redhat.consulting.transaction.model.TransactionRecord;
import com.redhat.consulting.transaction.model.TransactionRequest;
import com.redhat.consulting.transaction.model.TransactionStatus;
import com.redhat.consulting.transaction.service.TransactionProcessor;
import com.redhat.consulting.transaction.service.TransactionService;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class TransactionsApiDelegateImpl implements TransactionsApiDelegate {

    private final NativeWebRequest request;
    private final ApiUriSupport apiUriSupport;
    private final TransactionService transactionService;
    private final TransactionProcessor transactionProcessor;

    @Autowired
    public TransactionsApiDelegateImpl(NativeWebRequest request,
                                       ApiUriSupport apiUriSupport,
                                       TransactionService transactionService,
                                       TransactionProcessor transactionProcessor) {
        this.request = request;
        this.apiUriSupport = apiUriSupport;
        this.transactionService = transactionService;
        this.transactionProcessor = transactionProcessor;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<Void> createTransaction(TransactionRequest transactionRequest) {
        TransactionRecord record = transactionService.createTransaction(transactionRequest);

        // NOTE: Processing of the transaction could be moved off to a queue and doesn't need to happen immediately
        transactionProcessor.processTransaction(record.getId());
        record = transactionService.getTransaction(record.getId());

        return ResponseEntity.created(
                apiUriSupport
                        .buildTransactionLocation(fromMethodCall(on(TransactionsApiController.class).getTransaction(record.getId()))))
                .build();
    }

    @Override
    public ResponseEntity<Void> deleteTransaction(String transactionId) {
        transactionService.deleteTransaction(transactionId);

        return ResponseEntity.noContent()
                .build();
    }

    @Override
    public ResponseEntity<TransactionRecord> getTransaction(String transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(transactionId));
    }

    @Override
    public ResponseEntity<PagedTransactionRecords> listTransactions(List<String> sourceAccount,
                                                                    List<String> sourceInstitution,
                                                                    List<String> targetAccount,
                                                                    List<String> targetInstitution,
                                                                    List<Double> valueAmount,
                                                                    List<String> valueCurrency,
                                                                    List<String> status,
                                                                    List<String> sort,
                                                                    Integer page,
                                                                    Integer size,
                                                                    Boolean unpaged) {
        Page<TransactionRecord> transactionPage = transactionService.listTransactions(
                sourceAccount,
                sourceInstitution,
                targetAccount,
                targetInstitution,
                valueAmount,
                valueCurrency,
                ofNullable(status)
                        .map(statuses -> statuses.stream()
                                .map(TransactionStatus::fromValue)
                                .collect(Collectors.toList()))
                        .orElse(null),
                PageableApiUtil.parsePageable(
                        // Prefer using the native "sort" parameter over one Spring tried to parse to a List
                        getRequest()
                                .map(request -> request.getParameterValues("sort"))
                                .map(Arrays::asList)
                                .orElse(ListUtils.emptyIfNull(sort)),
                        page,
                        size,
                        unpaged));

        return ResponseEntity.ok(
                new PagedTransactionRecords()
                        .transactions(transactionPage.getContent())
                        .pageMetadata(PageableApiUtil.createPageMetadata(transactionPage)));
    }

    @Override
    public ResponseEntity<Void> updateTransaction(String transactionId, TransactionRecord transactionRecord) {
        transactionService.updateTransaction(transactionRecord.id(transactionId));

        return ResponseEntity.accepted().build();
    }
}
