package com.smarthostel.model;

import java.sql.Date;

public class Student {
    private int studentId;
    private Integer userId;
    private String registerNumber;
    private String fullName;
    private String gender;
    private String department;
    private int academicYear;
    private Date dateOfBirth;
    private String phoneNumber;
    private String email;
    private String address;
    private String parentName;
    private String parentContact;
    private Date admissionDate;

    // Derived / Display helpers
    private String allocatedRoom;
    private String feeStatus;

    public Student() {}

    public Student(int studentId, Integer userId, String registerNumber, String fullName,
                   String gender, String department, int academicYear, Date dateOfBirth,
                   String phoneNumber, String email, String address, String parentName,
                   String parentContact, Date admissionDate) {
        this.studentId = studentId;
        this.userId = userId;
        this.registerNumber = registerNumber;
        this.fullName = fullName;
        this.gender = gender;
        this.department = department;
        this.academicYear = academicYear;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.parentName = parentName;
        this.parentContact = parentContact;
        this.admissionDate = admissionDate;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        this.academicYear = academicYear;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentContact() {
        return parentContact;
    }

    public void setParentContact(String parentContact) {
        this.parentContact = parentContact;
    }

    public Date getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(Date admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getAllocatedRoom() {
        return allocatedRoom;
    }

    public void setAllocatedRoom(String allocatedRoom) {
        this.allocatedRoom = allocatedRoom;
    }

    public String getFeeStatus() {
        return feeStatus;
    }

    public void setFeeStatus(String feeStatus) {
        this.feeStatus = feeStatus;
    }

    @Override
    public String toString() {
        return fullName + " (" + registerNumber + " - Year " + academicYear + ")";
    }
}
