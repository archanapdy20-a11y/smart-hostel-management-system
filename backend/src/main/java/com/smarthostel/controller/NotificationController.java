package com.smarthostel.controller;

import com.smarthostel.dto.ApiResponse;
import com.smarthostel.model.entity.Notification;
import com.smarthostel.model.entity.User;
import com.smarthostel.repository.UserRepository;
import com.smarthostel.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification APIs")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Get User Notifications")
    public ResponseEntity<ApiResponse<List<Notification>>> getMyNotifications() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        List<Notification> data = notificationService.getUserNotifications(user.getId());
        return ResponseEntity.ok(ApiResponse.success(data, "Notifications retrieved successfully"));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark Notification Read")
    public ResponseEntity<ApiResponse<Notification>> markRead(@PathVariable Long id) {
        Notification data = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(data, "Notification marked as read"));
    }
}
