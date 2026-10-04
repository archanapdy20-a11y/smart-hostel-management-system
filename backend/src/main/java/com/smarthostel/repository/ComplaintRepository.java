package com.smarthostel.repository;

import com.smarthostel.model.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudentId(Long studentId);
    List<Complaint> findByAssignedWardenId(Long wardenId);
    List<Complaint> findByPriority(Complaint.Priority priority);
    List<Complaint> findByStatus(Complaint.ComplaintStatus status);
}
