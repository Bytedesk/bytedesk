package com.bytedesk.ticket.ticket.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketUpdateRequest {
    private String title;
    private String content;
    private Long categoryId;
    private String priority;
    private List<String> tags;
    private List<MultipartFile> attachments;
    private LocalDateTime dueDate;
} 