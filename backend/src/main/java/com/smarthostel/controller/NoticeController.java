package com.smarthostel.controller;

import com.smarthostel.dto.ApiResponse;
import com.smarthostel.model.entity.Notice;
import com.smarthostel.model.entity.User;
import com.smarthostel.repository.NoticeRepository;
import com.smarthostel.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notices")
@Tag(name = "Noticeboard APIs")
public class NoticeController {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    public NoticeController(NoticeRepository noticeRepository, UserRepository userRepository) {
        this.noticeRepository = noticeRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Get All Notices")
    public ResponseEntity<ApiResponse<List<Notice>>> getNotices() {
        if (noticeRepository.count() == 0) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username).orElse(null);
            noticeRepository.save(Notice.builder()
                    .title("📢 Welcome to Smart Hostel System")
                    .content("All residents are advised to register their attendance using the QR code scanner in their respective blocks.")
                    .category("General")
                    .targetRole("ALL")
                    .publishedBy(user)
                    .build());
            noticeRepository.save(Notice.builder()
                    .title("⚡ Scheduled Electrical Maintenance")
                    .content("Power supply will be interrupted tomorrow from 10:00 AM to 1:00 PM for transformer inspection.")
                    .category("Maintenance")
                    .targetRole("ALL")
                    .publishedBy(user)
                    .build());
        }
        List<Notice> data = noticeRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(ApiResponse.success(data, "Notices retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Publish Notice")
    public ResponseEntity<ApiResponse<Notice>> publishNotice(@RequestBody Notice notice) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        notice.setPublishedBy(user);
        Notice data = noticeRepository.save(notice);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(data, "Notice published successfully"));
    }
}
