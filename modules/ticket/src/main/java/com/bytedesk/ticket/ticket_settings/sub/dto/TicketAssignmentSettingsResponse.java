package com.bytedesk.ticket.ticket_settings.sub.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 工单指派设置响应 DTO。
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketAssignmentSettingsResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private Boolean autoAssign;
    private String assignmentType;
    private Boolean workingHoursEnabled;
    private String workingHoursStart;
    private String workingHoursEnd;
    private String workingDays; // JSON array as string
    private Integer maxConcurrentTickets;
}
