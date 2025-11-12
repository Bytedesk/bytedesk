package com.bytedesk.ticket.ticket_settings.sub.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 工单指派设置响应 DTO（结构化 workingDays）。
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
    private List<Integer> workingDays; // 1-7
    private Integer maxConcurrentTickets;
}
