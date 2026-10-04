package com.smarthostel.controller;

import com.smarthostel.dto.ApiResponse;
import com.smarthostel.model.entity.Attendance;
import com.smarthostel.model.entity.Student;
import com.smarthostel.model.entity.User;
import com.smarthostel.model.enums.RoleEnum;
import com.smarthostel.repository.AttendanceRepository;
import com.smarthostel.repository.StudentRepository;
import com.smarthostel.repository.UserRepository;
import com.smarthostel.service.QRAttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@Tag(name = "QR Attendance APIs")
public class AttendanceController {

    private final QRAttendanceService qrAttendanceService;
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public AttendanceController(QRAttendanceService qrAttendanceService,
                                AttendanceRepository attendanceRepository,
                                StudentRepository studentRepository,
                                UserRepository userRepository) {
        this.qrAttendanceService = qrAttendanceService;
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    public static class QRScanRequest {
        private String qrToken;

        public QRScanRequest() {}

        public QRScanRequest(String qrToken) {
            this.qrToken = qrToken;
        }

        public String getQrToken() { return qrToken; }
        public void setQrToken(String qrToken) { this.qrToken = qrToken; }
    }

    @GetMapping("/generate-qr")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Generate Live Signed QR Token", description = "Generates time-bounded HMAC token for block attendance canvas")
    public ResponseEntity<ApiResponse<String>> generateQR(@RequestParam(defaultValue = "1") Long blockId) {
        String data = qrAttendanceService.generateQRToken(blockId);
        return ResponseEntity.ok(ApiResponse.success(data, "Live signed QR token generated"));
    }

    @PostMapping("/mark-qr")
    @Operation(summary = "Mark Attendance via QR Code Scan", description = "Validates dynamic QR token signature & records student attendance")
    public ResponseEntity<ApiResponse<Attendance>> markQR(@RequestBody QRScanRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        Student student = studentRepository.findByUserId(user.getId()).orElseThrow();

        Attendance data = qrAttendanceService.markAttendanceViaQR(student.getId(), request.getQrToken());
        return ResponseEntity.ok(ApiResponse.success(data, "Attendance marked PRESENT successfully"));
    }

    @GetMapping("/my-history")
    @Operation(summary = "Get Student Attendance Records")
    public ResponseEntity<ApiResponse<List<Attendance>>> getMyAttendance() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        List<Attendance> data;
        if (user.getRole() == RoleEnum.ROLE_STUDENT) {
            Student student = studentRepository.findByUserId(user.getId()).orElseThrow();
            data = attendanceRepository.findByStudentId(student.getId());
        } else {
            data = attendanceRepository.findByAttendanceDate(LocalDate.now());
        }

        return ResponseEntity.ok(ApiResponse.success(data, "Attendance logs retrieved"));
    }
}
