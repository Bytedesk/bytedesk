package com.bytedesk.ticket.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.ticket.comment.dto.CommentRequest;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets/comments")
public class TicketCommentController {

    // @Autowired
    // private TicketCommentService commentService;

    // 添加评论
    @PostMapping
    public ResponseEntity<TicketCommentEntity> addComment(
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal Long userId) {
        // return ResponseEntity.ok(commentService.addComment(
        //     request.getTicketId(),
        //     request.getContent(),
        //     request.getInternal(),
        //     userId
        // ));
        return ResponseEntity.ok().build();
    }

    // 回复评论
    @PostMapping("/{commentId}/replies")
    public ResponseEntity<TicketCommentEntity> replyToComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal Long userId) {
        // return ResponseEntity.ok(commentService.replyToComment(
        //     commentId,
        //     request.getContent(),
        //     request.getInternal(),
        //     userId
        // ));
        return ResponseEntity.ok().build();
    }

    // 删除评论
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Long userId) {
        // commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    // 获取工单的所有评论
    @GetMapping("/tickets/{ticketId}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<Page<TicketCommentEntity>> getTicketComments(
            @PathVariable Long ticketId,
            Pageable pageable) {
        // return ResponseEntity.ok(commentService.getTicketComments(ticketId, pageable));
        return ResponseEntity.ok().build();
    }

    // 获取工单的公开评论
    @GetMapping("/tickets/{ticketId}/public")
    public ResponseEntity<Page<TicketCommentEntity>> getPublicComments(
            @PathVariable Long ticketId,
            Pageable pageable) {
        // return ResponseEntity.ok(commentService.getPublicComments(ticketId, pageable));
        return ResponseEntity.ok().build();
    }

    // 获取可见的评论（包括自己的内部评论）
    @GetMapping("/tickets/{ticketId}/visible")
    public ResponseEntity<Page<TicketCommentEntity>> getVisibleComments(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal Long userId,
            Pageable pageable) {
        // return ResponseEntity.ok(commentService.getVisibleComments(ticketId, userId, pageable));
        return ResponseEntity.ok().build();
    }

    // 获取评论的回复
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<TicketCommentEntity>> getReplies(
            @PathVariable Long commentId) {
        // return ResponseEntity.ok(commentService.getReplies(commentId));
        return ResponseEntity.ok().build();
    }

    // 上传附件
    @PostMapping("/{commentId}/attachments")
    public ResponseEntity<Void> uploadAttachments(
            @PathVariable Long commentId,
            @RequestParam("files") List<MultipartFile> files) {
        // commentService.addAttachments(commentId, files);
        return ResponseEntity.ok().build();
    }

    // 删除附件
    @DeleteMapping("/{commentId}/attachments/{attachmentId}")
    public ResponseEntity<Void> removeAttachment(
            @PathVariable Long commentId,
            @PathVariable Long attachmentId) {
        // commentService.removeAttachment(commentId, attachmentId);
        return ResponseEntity.ok().build();
    }

    // 获取评论数量
    @GetMapping("/tickets/{ticketId}/count")
    public ResponseEntity<Integer> getCommentCount(
            @PathVariable Long ticketId) {
        // return ResponseEntity.ok(commentService.countPublicComments(ticketId));
        return ResponseEntity.ok().build();
    }
} 