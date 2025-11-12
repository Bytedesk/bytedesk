package com.bytedesk.ticket.ticket_settings.sub.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.Builder;

/**
 * 工单指派设置草稿请求 DTO。
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketAssignmentSettingsRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private Boolean autoAssign;
    private String assignmentType;
    private Boolean workingHoursEnabled;
    private String workingHoursStart;
    private String workingHoursEnd;
    private String workingDays; // JSON array as string
    private Integer maxConcurrentTickets;
}
