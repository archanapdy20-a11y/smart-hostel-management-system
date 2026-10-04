package com.smarthostel.repository;

import com.smarthostel.model.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {
    List<Bed> findByRoomId(Long roomId);
    List<Bed> findByStatus(Bed.BedStatus status);
    List<Bed> findByRoomIdAndStatus(Long roomId, Bed.BedStatus status);
}
