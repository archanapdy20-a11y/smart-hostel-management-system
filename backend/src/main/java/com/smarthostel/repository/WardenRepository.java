package com.smarthostel.repository;

import com.smarthostel.model.entity.Warden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WardenRepository extends JpaRepository<Warden, Long> {

    Optional<Warden> findByUserId(Long userId);

    Optional<Warden> findByEmployeeId(String employeeId);

    Boolean existsByEmployeeId(String employeeId);
}
