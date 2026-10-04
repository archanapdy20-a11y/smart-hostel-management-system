package com.smarthostel.controller;

import com.smarthostel.dto.ApiResponse;
import com.smarthostel.model.entity.*;
import com.smarthostel.model.enums.RoleEnum;
import com.smarthostel.repository.*;
import com.smarthostel.service.ComplaintPriorityService;
import com.smarthostel.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/complaints")
@Tag(name = "Complaint Priority & Management APIs")
public class ComplaintController {

    private final ComplaintRepository complaintRepository;
    private final ComplaintCategoryRepository categoryRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ComplaintPriorityService priorityService;
    private final NotificationService notificationService;

    public ComplaintController(ComplaintRepository complaintRepository, ComplaintCategoryRepository categoryRepository, StudentRepository studentRepository, UserRepository userRepository, ComplaintPriorityService priorityService, NotificationService notificationService) {
        this.complaintRepository = complaintRepository;
        this.categoryRepository = categoryRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.priorityService = priorityService;
        this.notificationService = notificationService;
    }

    public static class ComplaintRequest {
        private Long categoryId;
        private String title;
        private String description;

        public ComplaintRequest() {}

        public ComplaintRequest(Long categoryId, String title, String description) {
            this.categoryId = categoryId;
            this.title = title;
            this.description = description;
        }

        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<ComplaintCategory>>> getCategories() {
        if (categoryRepository.count() == 0) {
            categoryRepository.save(ComplaintCategory.builder().name("Plumbing & Water").baseWeight(0.8).build());
            categoryRepository.save(ComplaintCategory.builder().name("Electrical & Power").baseWeight(0.85).build());
            categoryRepository.save(ComplaintCategory.builder().name("Cleanliness & Hygiene").baseWeight(0.4).build());
            categoryRepository.save(ComplaintCategory.builder().name("Furniture & Woodwork").baseWeight(0.3).build());
            categoryRepository.save(ComplaintCategory.builder().name("General Inquiry").baseWeight(0.2).build());
        }
        List<ComplaintCategory> data = categoryRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(data, "Complaint categories retrieved"));
    }

    @PostMapping
    @Operation(summary = "Submit Complaint", description = "Submits a new complaint and automatically computes priority using NLP/Keyword engine")
    public ResponseEntity<ApiResponse<Complaint>> submitComplaint(@RequestBody ComplaintRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student record required"));

        ComplaintCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Complaint.Priority priority = priorityService.calculatePriority(request.getTitle(), request.getDescription(), category);

        Complaint complaint = Complaint.builder()
                .student(student)
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(priority)
                .status(Complaint.ComplaintStatus.PENDING)
                .build();

        Complaint saved = complaintRepository.save(complaint);

        if (priority == Complaint.Priority.EMERGENCY) {
            notificationService.sendNotification(user, "🚨 Emergency Complaint Logged", "Your complaint '" + request.getTitle() + "' was tagged as EMERGENCY and escalated to Warden.", "COMPLAINT_UPDATE");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(saved, "Complaint submitted successfully with priority " + priority));
    }

    @GetMapping
    @Operation(summary = "List Complaints")
    public ResponseEntity<ApiResponse<List<Complaint>>> getComplaints() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        List<Complaint> data;
        if (user.getRole() == RoleEnum.ROLE_STUDENT) {
            Student student = studentRepository.findByUserId(user.getId()).orElseThrow();
            data = complaintRepository.findByStudentId(student.getId());
        } else {
            data = complaintRepository.findAll();
        }

        return ResponseEntity.ok(ApiResponse.success(data, "Complaints retrieved successfully"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update Complaint Status")
    public ResponseEntity<ApiResponse<Complaint>> updateStatus(@PathVariable Long id, @RequestParam Complaint.ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus(status);
        if (status == Complaint.ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }

        Complaint saved = complaintRepository.save(complaint);
        notificationService.sendNotification(
                complaint.getStudent().getUser(),
                "Complaint Status Update",
                "Your complaint '" + complaint.getTitle() + "' is now " + status.name(),
                "COMPLAINT_UPDATE"
        );

        return ResponseEntity.ok(ApiResponse.success(saved, "Complaint status updated to " + status));
    }
}
