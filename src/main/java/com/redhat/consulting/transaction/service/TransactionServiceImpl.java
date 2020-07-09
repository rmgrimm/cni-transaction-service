package com.redhat.consulting.transaction.service;

import com.github.dozermapper.core.Mapper;
import com.redhat.consulting.transaction.exception.TransactionNotFoundException;
import com.redhat.consulting.transaction.jpa.entity.TransactionEntity;
import com.redhat.consulting.transaction.jpa.entity.TransactionEntity_;
import com.redhat.consulting.transaction.jpa.repository.TransactionRepository;
import com.redhat.consulting.transaction.jpa.util.SortKeyMapper;
import com.redhat.consulting.transaction.model.TransactionRecord;
import com.redhat.consulting.transaction.model.TransactionRequest;
import com.redhat.consulting.transaction.model.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.OffsetDateTime;
import java.util.*;

import static java.util.Optional.ofNullable;

@Component
public class TransactionServiceImpl implements TransactionService {

    private final EntityManager entityManager;
    private final TransactionRepository transactionRepository;
    private final SortKeyMapper sortKeyMapper;
    private final Mapper dozer;

    @Autowired
    public TransactionServiceImpl(EntityManager entityManager,
                                  TransactionRepository transactionRepository,
                                  SortKeyMapper sortKeyMapper,
                                  Mapper dozer) {
        this.entityManager = entityManager;
        this.transactionRepository = transactionRepository;
        this.sortKeyMapper = sortKeyMapper;
        this.dozer = dozer;
    }

    @Transactional
    @Override
    public TransactionRecord createTransaction(TransactionRequest transactionRequest) {
        TransactionRecord newRecord = dozer.map(transactionRequest, TransactionRecord.class)
                // TODO(rgrimm): Use a better method of generating IDs
                .id(UUID.randomUUID().toString())
                .status(TransactionStatus.REQUESTED)
                .requestTimestamp(OffsetDateTime.now());

        transactionRepository.save(dozer.map(newRecord, TransactionEntity.class));

        return newRecord;
    }

    @Transactional
    @Override
    public void deleteTransaction(String transactionId) {
        Optional<TransactionEntity> transaction = transactionRepository.findByTransactionId(transactionId);

        if (transaction.isPresent()) {
            transactionRepository.delete(transaction.get());
        } else {
            throw new TransactionNotFoundException(transactionId);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public TransactionRecord getTransaction(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId)
                .map(entity -> dozer.map(entity, TransactionRecord.class))
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
    }

    @Transactional(readOnly = true)
    @Override
    public long countTransactions(List<String> sourceAccount, List<String> sourceInstitution, List<String> targetAccount, List<String> targetInstitution, List<Double> valueAmount, List<String> valueCurrency, List<TransactionStatus> status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> queryBuilder = cb.createQuery(Long.class);
        Root<TransactionEntity> root = queryBuilder.from(TransactionEntity.class);

        queryBuilder.select(cb.count(root))
                .where(buildTransactionFilterClauses(root, sourceAccount, sourceInstitution, targetAccount, targetInstitution, valueAmount, valueCurrency, status));

        TypedQuery<Long> typedQuery = entityManager.createQuery(queryBuilder);

        return typedQuery.getSingleResult();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TransactionRecord> listTransactions(List<String> sourceAccount, List<String> sourceInstitution, List<String> targetAccount, List<String> targetInstitution, List<Double> valueAmount, List<String> valueCurrency, List<TransactionStatus> status, Pageable pageable) {
        long totalTransactions = countTransactions(sourceAccount, sourceInstitution, targetAccount, targetInstitution, valueAmount, valueCurrency, status);

        if (pageable != null && pageable.isPaged() && pageable.getOffset() >= totalTransactions) {
            return new PageImpl<>(Collections.emptyList(), pageable, totalTransactions);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TransactionEntity> queryBuilder = cb.createQuery(TransactionEntity.class);
        Root<TransactionEntity> root = queryBuilder.from(TransactionEntity.class);

        queryBuilder.select(root)
                .where(buildTransactionFilterClauses(root, sourceAccount, sourceInstitution, targetAccount, targetInstitution, valueAmount, valueCurrency, status));

        if (pageable != null && pageable.getSort().isSorted()) {
            queryBuilder.orderBy(sortKeyMapper.performMap(TransactionRecord.class, root, cb, pageable.getSort()));
        }

        TypedQuery<TransactionEntity> typedQuery = entityManager.createQuery(queryBuilder);

        if (pageable != null && pageable.isPaged()) {
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());
        }

        return new PageImpl<>(typedQuery.getResultList(),
                ofNullable(pageable).orElse(Pageable.unpaged()),
                totalTransactions)
                .map(entity -> dozer.map(entity, TransactionRecord.class));
    }

    private Predicate[] buildTransactionFilterClauses(Path<TransactionEntity> transactionEntityPath, List<String> sourceAccount, List<String> sourceInstitution, List<String> targetAccount, List<String> targetInstitution, List<Double> valueAmount, List<String> valueCurrency, List<TransactionStatus> status) {
        List<Predicate> restrictions = new ArrayList<>();
        addOptionalFilter(restrictions, transactionEntityPath.get(TransactionEntity_.status), status);
        addOptionalFilter(restrictions, transactionEntityPath.get(TransactionEntity_.sourceInstitution), sourceInstitution);
        addOptionalFilter(restrictions, transactionEntityPath.get(TransactionEntity_.sourceAccount), sourceAccount);
        addOptionalFilter(restrictions, transactionEntityPath.get(TransactionEntity_.targetInstitution), targetInstitution);
        addOptionalFilter(restrictions, transactionEntityPath.get(TransactionEntity_.targetAccount), targetAccount);
        addOptionalFilter(restrictions, transactionEntityPath.get(TransactionEntity_.currencyAmount), valueAmount);
        addOptionalFilter(restrictions, transactionEntityPath.get(TransactionEntity_.currencyUnit), valueCurrency);
        return restrictions.toArray(new Predicate[0]);
    }

    private <T> void addOptionalFilter(List<Predicate> restrictions, Path<T> filterPropertyPath, Collection<T> filterValues) {
        ofNullable(filterValues)
                .filter(values -> !values.isEmpty())
                .map(filterPropertyPath::in)
                .ifPresent(restrictions::add);
    }

    @Transactional
    @Override
    public void updateTransaction(TransactionRecord transactionRecord) {
        TransactionEntity transactionEntity = transactionRepository.findByTransactionId(transactionRecord.getId())
                .orElseThrow(() -> new TransactionNotFoundException(transactionRecord.getId()));

        dozer.map(transactionRecord, transactionEntity);

        transactionRepository.save(transactionEntity);
    }

}
