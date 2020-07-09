package com.redhat.consulting.transaction.jpa.repository;

import com.redhat.consulting.transaction.jpa.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long>, JpaSpecificationExecutor<TransactionEntity> {
    Optional<TransactionEntity> findByTransactionId(String transactionId);
}
