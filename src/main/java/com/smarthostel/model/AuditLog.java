package com.smarthostel.model;

import java.sql.Timestamp;

public class AuditLog {
    private int logId;
    private Integer userId;
    private String username;
    private String actionType;
    private String tableAffected;
    private Integer recordId;
    private String details;
    private Timestamp timestamp;

    public AuditLog() {}

    public AuditLog(int logId, Integer userId, String username, String actionType,
                    String tableAffected, Integer recordId, String details, Timestamp timestamp) {
        this.logId = logId;
        this.userId = userId;
        this.username = username;
        this.actionType = actionType;
        this.tableAffected = tableAffected;
        this.recordId = recordId;
        this.details = details;
        this.timestamp = timestamp;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getTableAffected() {
        return tableAffected;
    }

    public void setTableAffected(String tableAffected) {
        this.tableAffected = tableAffected;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
