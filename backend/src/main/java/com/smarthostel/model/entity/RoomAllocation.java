package com.smarthostel.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "room_allocations")
public class RoomAllocation {

    public enum AllocationStatus {
        ACTIVE,
        VACATED,
        TRANSFERRED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bed_id", nullable = false)
    private Bed bed;

    @Column(name = "allocated_date", nullable = false)
    private LocalDate allocatedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AllocationStatus status;

    public RoomAllocation() {}

    public RoomAllocation(Long id, Student student, Bed bed, LocalDate allocatedDate, AllocationStatus status) {
        this.id = id;
        this.student = student;
        this.bed = bed;
        this.allocatedDate = allocatedDate;
        this.status = status;
    }

    public static RoomAllocationBuilder builder() { return new RoomAllocationBuilder(); }

    public static class RoomAllocationBuilder {
        private Long id;
        private Student student;
        private Bed bed;
        private LocalDate allocatedDate;
        private AllocationStatus status;

        public RoomAllocationBuilder id(Long id) { this.id = id; return this; }
        public RoomAllocationBuilder student(Student student) { this.student = student; return this; }
        public RoomAllocationBuilder bed(Bed bed) { this.bed = bed; return this; }
        public RoomAllocationBuilder allocatedDate(LocalDate allocatedDate) { this.allocatedDate = allocatedDate; return this; }
        public RoomAllocationBuilder status(AllocationStatus status) { this.status = status; return this; }

        public RoomAllocation build() {
            return new RoomAllocation(id, student, bed, allocatedDate, status);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Bed getBed() { return bed; }
    public void setBed(Bed bed) { this.bed = bed; }

    public LocalDate getAllocatedDate() { return allocatedDate; }
    public void setAllocatedDate(LocalDate allocatedDate) { this.allocatedDate = allocatedDate; }

    public AllocationStatus getStatus() { return status; }
    public void setStatus(AllocationStatus status) { this.status = status; }
}
