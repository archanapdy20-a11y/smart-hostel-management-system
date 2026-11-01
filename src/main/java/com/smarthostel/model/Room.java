package com.smarthostel.model;

import java.math.BigDecimal;

public class Room {
    private int roomId;
    private String roomNumber;
    private String blockName;
    private int floorNumber;
    private String roomType;
    private int totalCapacity;
    private int occupiedCapacity;
    private int availableBeds;
    private BigDecimal monthlyRent;
    private String status;

    public Room() {}

    public Room(int roomId, String roomNumber, String blockName, int floorNumber,
                String roomType, int totalCapacity, int occupiedCapacity,
                BigDecimal monthlyRent, String status) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.blockName = blockName;
        this.floorNumber = floorNumber;
        this.roomType = roomType;
        this.totalCapacity = totalCapacity;
        this.occupiedCapacity = occupiedCapacity;
        this.availableBeds = totalCapacity - occupiedCapacity;
        this.monthlyRent = monthlyRent;
        this.status = status;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
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

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
        this.availableBeds = totalCapacity - this.occupiedCapacity;
    }

    public int getOccupiedCapacity() {
        return occupiedCapacity;
    }

    public void setOccupiedCapacity(int occupiedCapacity) {
        this.occupiedCapacity = occupiedCapacity;
        this.availableBeds = this.totalCapacity - occupiedCapacity;
    }

    public int getAvailableBeds() {
        return totalCapacity - occupiedCapacity;
    }

    public void setAvailableBeds(int availableBeds) {
        this.availableBeds = availableBeds;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFull() {
        return occupiedCapacity >= totalCapacity;
    }

    @Override
    public String toString() {
        return blockName + " - " + roomNumber + " (" + roomType + " | Beds: " + getAvailableBeds() + "/" + totalCapacity + ")";
    }
}
