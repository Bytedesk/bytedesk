package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 工单详情
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TicketContent extends BaseContent {

    private static final long serialVersionUID = 1L;

    private String uid;
    private String ticketNumber;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String type;
    private String threadUid;
    private String threadTopic;
    private String categoryUid;
    private String assignee;
    private String reporter;
    private String createdAt;
    private String updatedAt;
    private String preview;
    
}
