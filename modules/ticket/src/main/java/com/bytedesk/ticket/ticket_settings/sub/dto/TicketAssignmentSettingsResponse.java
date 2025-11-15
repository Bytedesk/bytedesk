package com.bytedesk.ticket.ticket_settings.sub.dto;

import java.util.List;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 工单指派设置响应 DTO（结构化 workingDays）。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TicketAssignmentSettingsResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;
    private Boolean autoAssign;
    private String assignmentType;
    private Boolean workingHoursEnabled;
    private String workingHoursStart;
    private String workingHoursEnd;
    private List<Integer> workingDays; // 1-7
    private Integer maxConcurrentTickets;
}
