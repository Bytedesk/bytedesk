package com.bytedesk.forum.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentEntity> createComment(
            @RequestParam String content,
            @RequestParam Long postId,
            @RequestParam Long userId,
            @RequestParam(required = false) Long parentId) {
        return ResponseEntity.ok(commentService.createComment(content, postId, userId, parentId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentEntity> updateComment(
            @PathVariable Long commentId,
            @RequestParam String content) {
        return ResponseEntity.ok(commentService.updateComment(commentId, content));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentEntity> getComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getComment(commentId));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentEntity>> getCommentsByPost(
            @PathVariable Long postId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CommentEntity>> getCommentsByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByUser(userId, pageable));
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> incrementLikeCount(@PathVariable Long commentId) {
        commentService.incrementLikeCount(commentId);
        return ResponseEntity.ok().build();
    }
} 