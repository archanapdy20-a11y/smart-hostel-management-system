package com.smarthostel.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Fee {
    private int feeId;
    private int studentId;
    private String academicYear;
    private String semester;
    private String feeType;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal pendingAmount;
    private Date dueDate;
    private String status;

    // Display helpers
    private String studentName;
    private String registerNumber;

    public Fee() {}

    public Fee(int feeId, int studentId, String academicYear, String semester,
               String feeType, BigDecimal totalAmount, BigDecimal paidAmount,
               BigDecimal pendingAmount, Date dueDate, String status) {
        this.feeId = feeId;
        this.studentId = studentId;
        this.academicYear = academicYear;
        this.semester = semester;
        this.feeType = feeType;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.pendingAmount = pendingAmount;
        this.dueDate = dueDate;
        this.status = status;
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

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(BigDecimal pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Override
    public String toString() {
        return studentName + " - " + semester + " (Pending: ₹" + pendingAmount + ")";
    }
}
