package com.bytedesk.ticket.ticket_settings.sub.dto;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 工单指派设置草稿请求 DTO。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TicketAssignmentSettingsRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;
    private Boolean autoAssign;
    private String assignmentType;
    private Boolean workingHoursEnabled;
    private String workingHoursStart;
    private String workingHoursEnd;
    private String workingDays; // JSON array as string
    private Integer maxConcurrentTickets;
}
