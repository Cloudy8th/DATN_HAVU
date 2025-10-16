package com.project.hmartweb.web.controllers;

import com.project.hmartweb.application.services.feedback.IFeedbackService;
import com.project.hmartweb.domain.dtos.FeedbackDTO;
import com.project.hmartweb.domain.entities.Feedback;
import com.project.hmartweb.web.base.RoleAdmin;
import com.project.hmartweb.web.base.RoleUser;
import com.project.hmartweb.web.base.RolesAdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/feedbacks")
public class FeedbacksController {
    @Autowired
    private IFeedbackService feedbackService;

    @GetMapping("")
    @RoleAdmin
    public ResponseEntity<?> getAllFeedbacks(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer perPage
    ) {
        var result = feedbackService.getAll(page, perPage);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> getAllFeedbacksByProduct(
            @PathVariable("id") String id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer perPage,
            @RequestParam(required = false) Integer star
    ) {
        var result = feedbackService.getAllByProduct(id, page, perPage, star);
        return ResponseEntity.status(200).body(result);
    }

    @GetMapping("/{id}")
    @RolesAdminUser
    public ResponseEntity<?> getFeedbackById(
            @PathVariable("id") UUID id
    ) {
        var result = feedbackService.getById(id);
        return ResponseEntity.status(200).body(result);
    }

    @PutMapping("/{id}")
    @RoleAdmin
    public ResponseEntity<?> updateFeedbackById(
            @PathVariable("id") UUID id,
            @RequestBody FeedbackDTO feedbackDTO
    ) {
        var result = feedbackService.update(id, feedbackDTO);
        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("")
    @RoleUser
    public ResponseEntity<?> insertFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        var result = feedbackService.insert(feedbackDTO);
        return ResponseEntity.status(201).body(result);
    }

    @DeleteMapping("/{id}")
    @RoleAdmin
    public ResponseEntity<?> deleteFeedbackById(
            @PathVariable("id") UUID id
    ) {
        Feedback feedback = feedbackService.getById(id);
        feedbackService.delete(feedback);
        return ResponseEntity.status(200).body("Deleted successfully");
    }

    @PostMapping("/multiple")
    @RolesAdminUser
    public ResponseEntity<?> insertMultipleFeedbacks(
            @RequestBody List<FeedbackDTO> feedbackDTOS
    ) {
        var result = feedbackService.insertMultiple(feedbackDTOS);
        return ResponseEntity.status(201).body(result);
    }
}
