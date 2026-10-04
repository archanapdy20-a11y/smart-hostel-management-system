package com.smarthostel.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    public enum LeaveStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LeaveStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by_warden_id")
    private Warden approvedByWarden;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public LeaveRequest() {}

    public LeaveRequest(Long id, Student student, LocalDate startDate, LocalDate endDate, String reason, LeaveStatus status, Warden approvedByWarden, String rejectionReason, LocalDateTime createdAt) {
        this.id = id;
        this.student = student;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.approvedByWarden = approvedByWarden;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
    }

    public static LeaveRequestBuilder builder() { return new LeaveRequestBuilder(); }

    public static class LeaveRequestBuilder {
        private Long id;
        private Student student;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;
        private LeaveStatus status;
        private Warden approvedByWarden;
        private String rejectionReason;
        private LocalDateTime createdAt;

        public LeaveRequestBuilder id(Long id) { this.id = id; return this; }
        public LeaveRequestBuilder student(Student student) { this.student = student; return this; }
        public LeaveRequestBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public LeaveRequestBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public LeaveRequestBuilder reason(String reason) { this.reason = reason; return this; }
        public LeaveRequestBuilder status(LeaveStatus status) { this.status = status; return this; }
        public LeaveRequestBuilder approvedByWarden(Warden approvedByWarden) { this.approvedByWarden = approvedByWarden; return this; }
        public LeaveRequestBuilder rejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; return this; }
        public LeaveRequestBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public LeaveRequest build() {
            return new LeaveRequest(id, student, startDate, endDate, reason, status, approvedByWarden, rejectionReason, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }

    public Warden getApprovedByWarden() { return approvedByWarden; }
    public void setApprovedByWarden(Warden approvedByWarden) { this.approvedByWarden = approvedByWarden; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
