package com.smarthostel.repository;

import com.smarthostel.model.entity.RoomAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomAllocationRepository extends JpaRepository<RoomAllocation, Long> {
    Optional<RoomAllocation> findByStudentIdAndStatus(Long studentId, RoomAllocation.AllocationStatus status);
    Optional<RoomAllocation> findByStudentId(Long studentId);
}
