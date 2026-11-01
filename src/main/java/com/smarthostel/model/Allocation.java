package com.smarthostel.model;

import java.sql.Date;

public class Allocation {
    private int allocationId;
    private int studentId;
    private int roomId;
    private Date allocationDate;
    private Date vacateDate;
    private String status;
    private Integer allocatedBy;
    private String notes;

    // Display helpers
    private String studentName;
    private String registerNumber;
    private String roomNumber;
    private String blockName;
    private String roomType;
    private String allocatedByName;

    public Allocation() {}

    public Allocation(int allocationId, int studentId, int roomId, Date allocationDate,
                      Date vacateDate, String status, Integer allocatedBy, String notes) {
        this.allocationId = allocationId;
        this.studentId = studentId;
        this.roomId = roomId;
        this.allocationDate = allocationDate;
        this.vacateDate = vacateDate;
        this.status = status;
        this.allocatedBy = allocatedBy;
        this.notes = notes;
    }

    public int getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(int allocationId) {
        this.allocationId = allocationId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Date getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(Date allocationDate) {
        this.allocationDate = allocationDate;
    }

    public Date getVacateDate() {
        return vacateDate;
    }

    public void setVacateDate(Date vacateDate) {
        this.vacateDate = vacateDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAllocatedBy() {
        return allocatedBy;
    }

    public void setAllocatedBy(Integer allocatedBy) {
        this.allocatedBy = allocatedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getAllocatedByName() {
        return allocatedByName;
    }

    public void setAllocatedByName(String allocatedByName) {
        this.allocatedByName = allocatedByName;
    }
}
