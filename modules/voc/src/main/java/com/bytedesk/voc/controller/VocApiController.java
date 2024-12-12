package com.bytedesk.voc.controller;

import com.bytedesk.voc.feedback.*;
import com.bytedesk.voc.reply.ReplyEntity;
import com.bytedesk.voc.reply.ReplyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/voc")
public class VocApiController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ReplyService replyService;

    @PostMapping("/feedback")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FeedbackEntity> createFeedback(
            @RequestParam String content,
            @RequestParam String type,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(feedbackService.createFeedback(
            content,
            Long.valueOf(userDetails.getUsername()),
            type));
    }

    @PostMapping("/feedback/{feedbackId}/reply")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReplyEntity> createReply(
            @PathVariable Long feedbackId,
            @RequestParam String content,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Boolean internal,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(replyService.createReply(
            content,
            feedbackId,
            Long.valueOf(userDetails.getUsername()),
            parentId,
            internal));
    }

    @PostMapping("/feedback/{feedbackId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long feedbackId,
            @RequestParam String status) {
        feedbackService.updateStatus(feedbackId, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/feedback/{feedbackId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignFeedback(
            @PathVariable Long feedbackId,
            @RequestParam Long assignedTo) {
        feedbackService.assignFeedback(feedbackId, assignedTo);
        return ResponseEntity.ok().build();
    }
} 