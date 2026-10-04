package com.smarthostel.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        EMERGENCY
    }

    public enum ComplaintStatus {
        PENDING,
        IN_PROGRESS,
        RESOLVED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private ComplaintCategory category;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ComplaintStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_warden_id")
    private Warden assignedWarden;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "priority_score")
    private Double priorityScore;

    public Complaint() {}

    public Complaint(Long id, Student student, ComplaintCategory category, String title, String description, Priority priority, ComplaintStatus status, Warden assignedWarden, LocalDateTime createdAt, LocalDateTime resolvedAt, Double priorityScore) {
        this.id = id;
        this.student = student;
        this.category = category;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.assignedWarden = assignedWarden;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
        this.priorityScore = priorityScore;
    }

    public static ComplaintBuilder builder() { return new ComplaintBuilder(); }

    public static class ComplaintBuilder {
        private Long id;
        private Student student;
        private ComplaintCategory category;
        private String title;
        private String description;
        private Priority priority;
        private ComplaintStatus status;
        private Warden assignedWarden;
        private LocalDateTime createdAt;
        private LocalDateTime resolvedAt;
        private Double priorityScore;

        public ComplaintBuilder id(Long id) { this.id = id; return this; }
        public ComplaintBuilder student(Student student) { this.student = student; return this; }
        public ComplaintBuilder category(ComplaintCategory category) { this.category = category; return this; }
        public ComplaintBuilder title(String title) { this.title = title; return this; }
        public ComplaintBuilder description(String description) { this.description = description; return this; }
        public ComplaintBuilder priority(Priority priority) { this.priority = priority; return this; }
        public ComplaintBuilder status(ComplaintStatus status) { this.status = status; return this; }
        public ComplaintBuilder assignedWarden(Warden assignedWarden) { this.assignedWarden = assignedWarden; return this; }
        public ComplaintBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ComplaintBuilder resolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; return this; }
        public ComplaintBuilder priorityScore(Double priorityScore) { this.priorityScore = priorityScore; return this; }

        public Complaint build() {
            return new Complaint(id, student, category, title, description, priority, status, assignedWarden, createdAt, resolvedAt, priorityScore);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public ComplaintCategory getCategory() { return category; }
    public void setCategory(ComplaintCategory category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public Warden getAssignedWarden() { return assignedWarden; }
    public void setAssignedWarden(Warden assignedWarden) { this.assignedWarden = assignedWarden; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public Double getPriorityScore() { return priorityScore; }
    public void setPriorityScore(Double priorityScore) { this.priorityScore = priorityScore; }
}
