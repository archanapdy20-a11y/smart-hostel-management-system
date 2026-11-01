package com.smarthostel.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Payment {
    private int paymentId;
    private int feeId;
    private int studentId;
    private BigDecimal amountPaid;
    private Date paymentDate;
    private String paymentMethod;
    private String transactionId;
    private Integer recordedBy;
    private String receiptNumber;
    private String remarks;

    // Display helpers
    private String studentName;
    private String registerNumber;
    private String feeSemester;
    private String recordedByName;

    public Payment() {}

    public Payment(int paymentId, int feeId, int studentId, BigDecimal amountPaid,
                   Date paymentDate, String paymentMethod, String transactionId,
                   Integer recordedBy, String receiptNumber, String remarks) {
        this.paymentId = paymentId;
        this.feeId = feeId;
        this.studentId = studentId;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.recordedBy = recordedBy;
        this.receiptNumber = receiptNumber;
        this.remarks = remarks;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getFeeId() {
        return feeId;
    }

    public void setFeeId(int feeId) {
        this.feeId = feeId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(Integer recordedBy) {
        this.recordedBy = recordedBy;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getFeeSemester() {
        return feeSemester;
    }

    public void setFeeSemester(String feeSemester) {
        this.feeSemester = feeSemester;
    }

    public String getRecordedByName() {
        return recordedByName;
    }

    public void setRecordedByName(String recordedByName) {
        this.recordedByName = recordedByName;
    }
}
