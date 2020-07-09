package com.redhat.consulting.transaction.api;

import com.redhat.consulting.transaction.exception.TransactionNotFoundException;
import com.redhat.consulting.transaction.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TransactionNotFoundHandler {

    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleTransactionNotFound(TransactionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse()
                        .id(ex.getTransactionId())
                        .success(Boolean.FALSE)
                        .message(ex.getMessage()));
    }

}
