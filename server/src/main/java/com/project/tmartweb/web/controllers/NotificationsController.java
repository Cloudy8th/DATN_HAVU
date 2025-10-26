package com.project.tmartweb.web.controllers;

import com.project.tmartweb.application.services.notification.INotificationService;
import com.project.tmartweb.domain.dtos.NotificationDTO;
import com.project.tmartweb.web.base.RoleAdmin;
import com.project.tmartweb.web.base.RoleUser;
import com.project.tmartweb.web.base.RolesAdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/notifications")
public class NotificationsController {
    @Autowired
    private INotificationService notificationService;

    @GetMapping("")
    @RoleAdmin
    public ResponseEntity<?> getAllNotifications(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer perPage
    ) {
        var result = notificationService.getAll(page, perPage);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @RolesAdminUser
    public ResponseEntity<?> getNotification(
            @PathVariable UUID id
    ) {
        var result = notificationService.getById(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/read/{id}")
    @RolesAdminUser
    public ResponseEntity<?> readNotification(
            @PathVariable UUID id
    ) {
        var result = notificationService.readNotification(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user-read/{id}")
    @RolesAdminUser
    public ResponseEntity<?> getAllByUserAndRead(
            @PathVariable UUID id
    ) {
        var res = notificationService.getAllByUserAndRead(id);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getAllNotificationsByUser(
            @PathVariable UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer perPage
    ) {
        var result = notificationService.getAllByUser(id, page, perPage);
        return ResponseEntity.ok(result);
    }

    @PostMapping("")
    @RoleAdmin
    public ResponseEntity<?> insertNotification(
            @RequestBody NotificationDTO notificationDTO
    ) {
        var result = notificationService.insert(notificationDTO);
        return ResponseEntity.status(201).body(result);
    }

    @PutMapping("/{id}")
    @RoleUser
    public ResponseEntity<?> updateNotification(
            @PathVariable UUID id,
            @RequestBody NotificationDTO notificationDTO
    ) {
        var result = notificationService.update(id, notificationDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @RoleAdmin
    public ResponseEntity<?> deleteNotification(
            @PathVariable UUID id
    ) {
        notificationService.delete(notificationService.getById(id));
        return ResponseEntity.ok("Deleted successfully!");
    }

    @PutMapping("/read-all/{id}")
    @RolesAdminUser
    public ResponseEntity<?> readAllNotifications(
            @PathVariable UUID id
    ) {
        notificationService.readAllNotifications(id);
        return ResponseEntity.ok("Update successfully!");
    }
}
