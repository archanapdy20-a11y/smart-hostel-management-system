package com.smarthostel.service;

import com.smarthostel.dao.AuditLogDAO;
import com.smarthostel.model.AuditLog;

import java.sql.SQLException;
import java.util.List;

public class AuditService {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public List<AuditLog> getRecentLogs(int limit) throws SQLException {
        return auditLogDAO.getRecentLogs(limit);
    }
}
