package com.smarthostel.service;

import com.smarthostel.model.*;

import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

public class ReportService {

    private final StudentService studentService = new StudentService();
    private final RoomService roomService = new RoomService();
    private final AllocationService allocationService = new AllocationService();
    private final FeeService feeService = new FeeService();
    private final ComplaintService complaintService = new ComplaintService();
    private final FoodService foodService = new FoodService();

    // Exports TableModel data to CSV file
    public boolean exportTableToCSV(TableModel model, File targetFile) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(targetFile))) {
            // Write Headers
            for (int col = 0; col < model.getColumnCount(); col++) {
                pw.print("\"" + model.getColumnName(col).replace("\"", "\"\"") + "\"");
                if (col < model.getColumnCount() - 1) pw.print(",");
            }
            pw.println();

            // Write Rows
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object val = model.getValueAt(row, col);
                    String text = val != null ? val.toString() : "";
                    pw.print("\"" + text.replace("\"", "\"\"") + "\"");
                    if (col < model.getColumnCount() - 1) pw.print(",");
                }
                pw.println();
            }
            return true;
        } catch (Exception e) {
            System.err.println("CSV export error: " + e.getMessage());
            return false;
        }
    }

    public List<Student> getStudentReport() throws SQLException {
        return studentService.getAllStudents();
    }

    public List<Room> getRoomAvailabilityReport() throws SQLException {
        return roomService.getAvailableRooms();
    }

    public List<Allocation> getAllocationReport() throws SQLException {
        return allocationService.getAllocations(false);
    }

    public List<Fee> getFeeCollectionReport() throws SQLException {
        return feeService.getAllFees(null);
    }

    public List<Fee> getPendingFeesReport() throws SQLException {
        return feeService.getAllFees("UNPAID");
    }

    public List<Payment> getPaymentHistoryReport() throws SQLException {
        return feeService.getAllPayments();
    }

    public List<Complaint> getComplaintsReport() throws SQLException {
        return complaintService.getAllComplaints("ALL");
    }

    public List<FoodFeedback> getFoodFeedbackReport() throws SQLException {
        return foodService.getAllFeedback();
    }
}
