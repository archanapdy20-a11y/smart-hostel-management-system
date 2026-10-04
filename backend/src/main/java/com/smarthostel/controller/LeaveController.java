package com.smarthostel.controller;

import com.smarthostel.dto.ApiResponse;
import com.smarthostel.model.entity.LeaveRequest;
import com.smarthostel.model.entity.Student;
import com.smarthostel.model.entity.User;
import com.smarthostel.model.enums.RoleEnum;
import com.smarthostel.repository.LeaveRequestRepository;
import com.smarthostel.repository.StudentRepository;
import com.smarthostel.repository.UserRepository;
import com.smarthostel.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leaves")
@Tag(name = "Leave Management APIs")
public class LeaveController {

    private final LeaveRequestRepository leaveRequestRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public LeaveController(LeaveRequestRepository leaveRequestRepository,
                           StudentRepository studentRepository,
                           UserRepository userRepository,
                           NotificationService notificationService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public static class LeaveApplyRequest {
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;

        public LeaveApplyRequest() {}

        public LeaveApplyRequest(LocalDate startDate, LocalDate endDate, String reason) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.reason = reason;
        }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    @PostMapping
    @Operation(summary = "Apply for Leave")
    public ResponseEntity<ApiResponse<LeaveRequest>> applyLeave(@RequestBody LeaveApplyRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        Student student = studentRepository.findByUserId(user.getId()).orElseThrow();

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .student(student)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .status(LeaveRequest.LeaveStatus.PENDING)
                .build();

        LeaveRequest data = leaveRequestRepository.save(leaveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(data, "Leave application submitted successfully"));
    }

    @GetMapping
    @Operation(summary = "Get Leave Requests")
    public ResponseEntity<ApiResponse<List<LeaveRequest>>> getLeaves() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        List<LeaveRequest> data;
        if (user.getRole() == RoleEnum.ROLE_STUDENT) {
            Student student = studentRepository.findByUserId(user.getId()).orElseThrow();
            data = leaveRequestRepository.findByStudentId(student.getId());
        } else {
            data = leaveRequestRepository.findAll();
        }

        return ResponseEntity.ok(ApiResponse.success(data, "Leave requests retrieved"));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Approve or Reject Leave")
    public ResponseEntity<ApiResponse<LeaveRequest>> updateLeaveStatus(@PathVariable Long id, @RequestParam LeaveRequest.LeaveStatus status) {
        LeaveRequest leave = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        leave.setStatus(status);
        LeaveRequest saved = leaveRequestRepository.save(leave);

        notificationService.sendNotification(
                leave.getStudent().getUser(),
                "Leave Application " + status.name(),
                "Your leave request from " + leave.getStartDate() + " to " + leave.getEndDate() + " was " + status.name().toLowerCase() + ".",
                "LEAVE_UPDATE"
        );

        return ResponseEntity.ok(ApiResponse.success(saved, "Leave status updated to " + status));
    }
}
