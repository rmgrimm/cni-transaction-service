package com.redhat.consulting.transaction.jpa.entity;

import com.redhat.consulting.transaction.jpa.util.TransactionStatusConverter;
import com.redhat.consulting.transaction.model.TransactionStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    @GeneratedValue
    @Column(name = "database_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(name = "transaction_id", unique = true, nullable = false, updatable = false)
    private String transactionId;

    @Column(name = "status", nullable = false)
    @Convert(converter = TransactionStatusConverter.class)
    private TransactionStatus status;

    @Column(name = "source_inst", nullable = false)
    private String sourceInstitution;

    @Column(name = "source_acct", nullable = false)
    private String sourceAccount;

    @Column(name = "target_inst", nullable = false)
    private String targetInstitution;

    @Column(name = "target_acct", nullable = false)
    private String targetAccount;

    @Column(name = "currency_amount", nullable = false)
    private Double currencyAmount;

    @Column(name = "currency_unit", nullable = false, length = 3)
    @Length(max = 3)
    private String currencyUnit;

    @Column(name = "request_time", nullable = false)
    private OffsetDateTime requestTimestamp;

    @Column(name = "process_time")
    private OffsetDateTime processTimestamp;

    @Column(name = "cancel_time")
    private OffsetDateTime cancelTimestamp;

    public Long getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getSourceInstitution() {
        return sourceInstitution;
    }

    public void setSourceInstitution(String sourceInstitution) {
        this.sourceInstitution = sourceInstitution;
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(String sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public String getTargetInstitution() {
        return targetInstitution;
    }

    public void setTargetInstitution(String targetInstitution) {
        this.targetInstitution = targetInstitution;
    }

    public String getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(String targetAccount) {
        this.targetAccount = targetAccount;
    }

    public Double getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(Double currencyAmount) {
        this.currencyAmount = currencyAmount;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public OffsetDateTime getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(OffsetDateTime requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    public OffsetDateTime getProcessTimestamp() {
        return processTimestamp;
    }

    public void setProcessTimestamp(OffsetDateTime processTimestamp) {
        this.processTimestamp = processTimestamp;
    }

    public OffsetDateTime getCancelTimestamp() {
        return cancelTimestamp;
    }

    public void setCancelTimestamp(OffsetDateTime cancelTimestamp) {
        this.cancelTimestamp = cancelTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof TransactionEntity)) return false;

        TransactionEntity that = (TransactionEntity) o;

        return new EqualsBuilder()
                .append(getId(), that.getId())
                .append(getTransactionId(), that.getTransactionId())
                .append(getStatus(), that.getStatus())
                .append(getSourceInstitution(), that.getSourceInstitution())
                .append(getSourceAccount(), that.getSourceAccount())
                .append(getTargetInstitution(), that.getTargetInstitution())
                .append(getTargetAccount(), that.getTargetAccount())
                .append(getCurrencyAmount(), that.getCurrencyAmount())
                .append(getCurrencyUnit(), that.getCurrencyUnit())
                .append(getRequestTimestamp(), that.getRequestTimestamp())
                .append(getProcessTimestamp(), that.getProcessTimestamp())
                .append(getCancelTimestamp(), that.getCancelTimestamp())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getTransactionId())
                .append(getStatus())
                .append(getSourceInstitution())
                .append(getSourceAccount())
                .append(getTargetInstitution())
                .append(getTargetAccount())
                .append(getCurrencyAmount())
                .append(getCurrencyUnit())
                .append(getRequestTimestamp())
                .append(getProcessTimestamp())
                .append(getCancelTimestamp())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("transactionId", transactionId)
                .append("status", status)
                .append("sourceInstitution", sourceInstitution)
                .append("sourceAccount", sourceAccount)
                .append("targetInstitution", targetInstitution)
                .append("targetAccount", targetAccount)
                .append("currencyAmount", currencyAmount)
                .append("currencyUnit", currencyUnit)
                .append("requestTimestamp", requestTimestamp)
                .append("processTimestamp", processTimestamp)
                .append("cancelTimestamp", cancelTimestamp)
                .toString();
    }
}
