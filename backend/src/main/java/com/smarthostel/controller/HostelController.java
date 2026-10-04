package com.smarthostel.controller;

import com.smarthostel.dto.ApiResponse;
import com.smarthostel.model.entity.*;
import com.smarthostel.repository.*;
import com.smarthostel.service.SmartRoomAllocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hostels")
@Tag(name = "Hostel & Room Management APIs")
public class HostelController {

    private final HostelRepository hostelRepository;
    private final BlockRepository blockRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final StudentRepository studentRepository;
    private final RoomAllocationRepository roomAllocationRepository;
    private final SmartRoomAllocationService smartRoomAllocationService;

    public HostelController(HostelRepository hostelRepository, BlockRepository blockRepository, RoomRepository roomRepository, BedRepository bedRepository, StudentRepository studentRepository, RoomAllocationRepository roomAllocationRepository, SmartRoomAllocationService smartRoomAllocationService) {
        this.hostelRepository = hostelRepository;
        this.blockRepository = blockRepository;
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.studentRepository = studentRepository;
        this.roomAllocationRepository = roomAllocationRepository;
        this.smartRoomAllocationService = smartRoomAllocationService;
    }

    @GetMapping
    @Operation(summary = "Get all hostels")
    public ResponseEntity<ApiResponse<List<Hostel>>> getAllHostels() {
        List<Hostel> data = hostelRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(data, "Hostels retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create hostel")
    public ResponseEntity<ApiResponse<Hostel>> createHostel(@RequestBody Hostel hostel) {
        Hostel data = hostelRepository.save(hostel);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(data, "Hostel created successfully"));
    }

    @GetMapping("/blocks")
    @Operation(summary = "Get blocks")
    public ResponseEntity<ApiResponse<List<Block>>> getBlocks(@RequestParam(required = false) Long hostelId) {
        List<Block> data = hostelId != null ? blockRepository.findByHostelId(hostelId) : blockRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(data, "Blocks retrieved successfully"));
    }

    @PostMapping("/blocks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Block>> createBlock(@RequestBody Block block) {
        Block data = blockRepository.save(block);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(data, "Block created successfully"));
    }

    @GetMapping("/rooms")
    @Operation(summary = "Get rooms")
    public ResponseEntity<ApiResponse<List<Room>>> getRooms(@RequestParam(required = false) Long blockId) {
        List<Room> data = blockId != null ? roomRepository.findByBlockId(blockId) : roomRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(data, "Rooms retrieved successfully"));
    }

    @PostMapping("/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Room>> createRoom(@RequestBody Room room) {
        Room savedRoom = roomRepository.save(room);
        for (int i = 1; i <= room.getCapacity(); i++) {
            Bed bed = Bed.builder()
                    .room(savedRoom)
                    .bedCode(savedRoom.getRoomNumber() + "-" + (char)('A' + i - 1))
                    .status(Bed.BedStatus.AVAILABLE)
                    .build();
            bedRepository.save(bed);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedRoom, "Room and beds created successfully"));
    }

    @GetMapping("/recommend-allocation/{studentId}")
    @Operation(summary = "🤖 Smart Room Allocation Recommendation Algorithm")
    public ResponseEntity<ApiResponse<List<SmartRoomAllocationService.RoomRecommendation>>> getRecommendations(@PathVariable Long studentId) {
        List<SmartRoomAllocationService.RoomRecommendation> data = smartRoomAllocationService.recommendRoomsForStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(data, "Room suitability recommendations generated"));
    }

    @PostMapping("/allocate")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Allocate Bed to Student")
    public ResponseEntity<ApiResponse<RoomAllocation>> allocateRoom(@RequestParam Long studentId, @RequestParam Long bedId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new RuntimeException("Bed not found"));

        if (bed.getStatus() != Bed.BedStatus.AVAILABLE) {
            throw new RuntimeException("Bed is not available!");
        }

        bed.setStatus(Bed.BedStatus.OCCUPIED);
        bedRepository.save(bed);

        RoomAllocation allocation = RoomAllocation.builder()
                .student(student)
                .bed(bed)
                .allocatedDate(LocalDate.now())
                .status(RoomAllocation.AllocationStatus.ACTIVE)
                .build();

        RoomAllocation data = roomAllocationRepository.save(allocation);
        return ResponseEntity.ok(ApiResponse.success(data, "Bed allocated to student successfully"));
    }
}
