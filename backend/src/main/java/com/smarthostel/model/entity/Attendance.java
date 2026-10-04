package com.smarthostel.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "attendance_date"})
})
public class Attendance {

    public enum AttendanceStatus {
        PRESENT,
        ABSENT,
        ON_LEAVE
    }

    public enum VerificationMethod {
        QR_SCAN,
        MANUAL_WARDEN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_method", length = 30)
    private VerificationMethod verificationMethod;

    @Column(name = "scanned_at")
    private LocalDateTime scannedAt;

    public Attendance() {}

    public Attendance(Long id, Student student, LocalDate attendanceDate, AttendanceStatus status, VerificationMethod verificationMethod, LocalDateTime scannedAt) {
        this.id = id;
        this.student = student;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.verificationMethod = verificationMethod;
        this.scannedAt = scannedAt;
    }

    public static AttendanceBuilder builder() { return new AttendanceBuilder(); }

    public static class AttendanceBuilder {
        private Long id;
        private Student student;
        private LocalDate attendanceDate;
        private AttendanceStatus status;
        private VerificationMethod verificationMethod;
        private LocalDateTime scannedAt;

        public AttendanceBuilder id(Long id) { this.id = id; return this; }
        public AttendanceBuilder student(Student student) { this.student = student; return this; }
        public AttendanceBuilder attendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; return this; }
        public AttendanceBuilder status(AttendanceStatus status) { this.status = status; return this; }
        public AttendanceBuilder verificationMethod(VerificationMethod verificationMethod) { this.verificationMethod = verificationMethod; return this; }
        public AttendanceBuilder scannedAt(LocalDateTime scannedAt) { this.scannedAt = scannedAt; return this; }

        public Attendance build() {
            return new Attendance(id, student, attendanceDate, status, verificationMethod, scannedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public VerificationMethod getVerificationMethod() { return verificationMethod; }
    public void setVerificationMethod(VerificationMethod verificationMethod) { this.verificationMethod = verificationMethod; }

    public LocalDateTime getScannedAt() { return scannedAt; }
    public void setScannedAt(LocalDateTime scannedAt) { this.scannedAt = scannedAt; }
}
