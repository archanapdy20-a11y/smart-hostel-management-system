package com.smarthostel.repository;

import com.smarthostel.model.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByHostelId(Long hostelId);
    List<Block> findByWardenId(Long wardenId);
}
