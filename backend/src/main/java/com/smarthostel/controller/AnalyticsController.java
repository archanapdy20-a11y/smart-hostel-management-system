package com.smarthostel.controller;

import com.smarthostel.dto.ApiResponse;
import com.smarthostel.model.entity.Bed;
import com.smarthostel.model.entity.Complaint;
import com.smarthostel.model.entity.LeaveRequest;
import com.smarthostel.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Hostel Analytics Dashboard APIs")
public class AnalyticsController {

    private final StudentRepository studentRepository;
    private final BedRepository bedRepository;
    private final HostelRepository hostelRepository;
    private final ComplaintRepository complaintRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public AnalyticsController(StudentRepository studentRepository,
                               BedRepository bedRepository,
                               HostelRepository hostelRepository,
                               ComplaintRepository complaintRepository,
                               LeaveRequestRepository leaveRequestRepository) {
        this.studentRepository = studentRepository;
        this.bedRepository = bedRepository;
        this.hostelRepository = hostelRepository;
        this.complaintRepository = complaintRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    public static class AnalyticsSummary {
        private long totalStudents;
        private long totalHostels;
        private long totalBeds;
        private long occupiedBeds;
        private long availableBeds;
        private double occupancyPercentage;
        private long pendingComplaints;
        private long emergencyComplaints;
        private long pendingLeaveRequests;
        private Map<String, Long> complaintsByPriority;

        public AnalyticsSummary() {}

        public AnalyticsSummary(long totalStudents, long totalHostels, long totalBeds, long occupiedBeds, long availableBeds, double occupancyPercentage, long pendingComplaints, long emergencyComplaints, long pendingLeaveRequests, Map<String, Long> complaintsByPriority) {
            this.totalStudents = totalStudents;
            this.totalHostels = totalHostels;
            this.totalBeds = totalBeds;
            this.occupiedBeds = occupiedBeds;
            this.availableBeds = availableBeds;
            this.occupancyPercentage = occupancyPercentage;
            this.pendingComplaints = pendingComplaints;
            this.emergencyComplaints = emergencyComplaints;
            this.pendingLeaveRequests = pendingLeaveRequests;
            this.complaintsByPriority = complaintsByPriority;
        }

        public long getTotalStudents() { return totalStudents; }
        public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

        public long getTotalHostels() { return totalHostels; }
        public void setTotalHostels(long totalHostels) { this.totalHostels = totalHostels; }

        public long getTotalBeds() { return totalBeds; }
        public void setTotalBeds(long totalBeds) { this.totalBeds = totalBeds; }

        public long getOccupiedBeds() { return occupiedBeds; }
        public void setOccupiedBeds(long occupiedBeds) { this.occupiedBeds = occupiedBeds; }

        public long getAvailableBeds() { return availableBeds; }
        public void setAvailableBeds(long availableBeds) { this.availableBeds = availableBeds; }

        public double getOccupancyPercentage() { return occupancyPercentage; }
        public void setOccupancyPercentage(double occupancyPercentage) { this.occupancyPercentage = occupancyPercentage; }

        public long getPendingComplaints() { return pendingComplaints; }
        public void setPendingComplaints(long pendingComplaints) { this.pendingComplaints = pendingComplaints; }

        public long getEmergencyComplaints() { return emergencyComplaints; }
        public void setEmergencyComplaints(long emergencyComplaints) { this.emergencyComplaints = emergencyComplaints; }

        public long getPendingLeaveRequests() { return pendingLeaveRequests; }
        public void setPendingLeaveRequests(long pendingLeaveRequests) { this.pendingLeaveRequests = pendingLeaveRequests; }

        public Map<String, Long> getComplaintsByPriority() { return complaintsByPriority; }
        public void setComplaintsByPriority(Map<String, Long> complaintsByPriority) { this.complaintsByPriority = complaintsByPriority; }

        public static AnalyticsSummaryBuilder builder() {
            return new AnalyticsSummaryBuilder();
        }

        public static class AnalyticsSummaryBuilder {
            private long totalStudents;
            private long totalHostels;
            private long totalBeds;
            private long occupiedBeds;
            private long availableBeds;
            private double occupancyPercentage;
            private long pendingComplaints;
            private long emergencyComplaints;
            private long pendingLeaveRequests;
            private Map<String, Long> complaintsByPriority;

            public AnalyticsSummaryBuilder totalStudents(long totalStudents) { this.totalStudents = totalStudents; return this; }
            public AnalyticsSummaryBuilder totalHostels(long totalHostels) { this.totalHostels = totalHostels; return this; }
            public AnalyticsSummaryBuilder totalBeds(long totalBeds) { this.totalBeds = totalBeds; return this; }
            public AnalyticsSummaryBuilder occupiedBeds(long occupiedBeds) { this.occupiedBeds = occupiedBeds; return this; }
            public AnalyticsSummaryBuilder availableBeds(long availableBeds) { this.availableBeds = availableBeds; return this; }
            public AnalyticsSummaryBuilder occupancyPercentage(double occupancyPercentage) { this.occupancyPercentage = occupancyPercentage; return this; }
            public AnalyticsSummaryBuilder pendingComplaints(long pendingComplaints) { this.pendingComplaints = pendingComplaints; return this; }
            public AnalyticsSummaryBuilder emergencyComplaints(long emergencyComplaints) { this.emergencyComplaints = emergencyComplaints; return this; }
            public AnalyticsSummaryBuilder pendingLeaveRequests(long pendingLeaveRequests) { this.pendingLeaveRequests = pendingLeaveRequests; return this; }
            public AnalyticsSummaryBuilder complaintsByPriority(Map<String, Long> complaintsByPriority) { this.complaintsByPriority = complaintsByPriority; return this; }

            public AnalyticsSummary build() {
                return new AnalyticsSummary(totalStudents, totalHostels, totalBeds, occupiedBeds, availableBeds, occupancyPercentage, pendingComplaints, emergencyComplaints, pendingLeaveRequests, complaintsByPriority);
            }
        }
    }

    @GetMapping("/summary")
    @Operation(summary = "Get System-wide Analytics Summary")
    public ResponseEntity<ApiResponse<AnalyticsSummary>> getSummary() {
        long totalStudents = studentRepository.count();
        long totalHostels = hostelRepository.count();
        long totalBeds = bedRepository.count();
        long occupiedBeds = bedRepository.findByStatus(Bed.BedStatus.OCCUPIED).size();
        long availableBeds = bedRepository.findByStatus(Bed.BedStatus.AVAILABLE).size();
        double occupancyRate = totalBeds > 0 ? ((double) occupiedBeds / totalBeds) * 100.0 : 0.0;

        long pendingComplaints = complaintRepository.findByStatus(Complaint.ComplaintStatus.PENDING).size();
        long emergencyComplaints = complaintRepository.findByPriority(Complaint.Priority.EMERGENCY).size();
        long pendingLeaves = leaveRequestRepository.findByStatus(LeaveRequest.LeaveStatus.PENDING).size();

        Map<String, Long> byPriority = new HashMap<>();
        byPriority.put("EMERGENCY", (long) complaintRepository.findByPriority(Complaint.Priority.EMERGENCY).size());
        byPriority.put("HIGH", (long) complaintRepository.findByPriority(Complaint.Priority.HIGH).size());
        byPriority.put("MEDIUM", (long) complaintRepository.findByPriority(Complaint.Priority.MEDIUM).size());
        byPriority.put("LOW", (long) complaintRepository.findByPriority(Complaint.Priority.LOW).size());

        AnalyticsSummary data = AnalyticsSummary.builder()
                .totalStudents(totalStudents)
                .totalHostels(totalHostels)
                .totalBeds(totalBeds)
                .occupiedBeds(occupiedBeds)
                .availableBeds(availableBeds)
                .occupancyPercentage(Math.round(occupancyRate * 10.0) / 10.0)
                .pendingComplaints(pendingComplaints)
                .emergencyComplaints(emergencyComplaints)
                .pendingLeaveRequests(pendingLeaves)
                .complaintsByPriority(byPriority)
                .build();

        return ResponseEntity.ok(ApiResponse.success(data, "Analytics summary retrieved successfully"));
    }
}
