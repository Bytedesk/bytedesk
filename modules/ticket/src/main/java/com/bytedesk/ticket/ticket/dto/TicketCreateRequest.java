/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 13:39:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 14:37:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketCreateRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private Long categoryId;
    private String priority = "normal";  // 默认优先级
    private String source = "web";       // 工单来源
    private List<String> tags;           // 标签
    private List<MultipartFile> attachments;  // 附件
    private LocalDateTime dueDate;       // 截止日期
} 