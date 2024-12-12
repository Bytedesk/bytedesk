package com.bytedesk.ticket.comment.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CommentRequest {
    @NotNull
    private Long ticketId;
    
    private Long parentId;  // 回复评论时使用
    
    @NotBlank
    private String content;
    
    private Boolean internal = false;
} 