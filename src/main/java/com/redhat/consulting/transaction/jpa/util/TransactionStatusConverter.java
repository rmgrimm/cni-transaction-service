package com.redhat.consulting.transaction.jpa.util;

import com.redhat.consulting.transaction.model.TransactionStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import static java.util.Optional.ofNullable;

@Converter
public class TransactionStatusConverter implements AttributeConverter<TransactionStatus, String> {
    @Override
    public String convertToDatabaseColumn(TransactionStatus attribute) {
        return ofNullable(attribute)
                .map(TransactionStatus::getValue)
                .orElse(null);
    }

    @Override
    public TransactionStatus convertToEntityAttribute(String dbData) {
        return ofNullable(dbData)
                .map(TransactionStatus::fromValue)
                .orElse(null);
    }
}
