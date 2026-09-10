package com.bytedesk.ticket.ticket.dto;

import java.util.Date;

import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketTimelineStepResponse {
    private String id;
    private String actionKey;
    private String title;
    private String titleKey;
    private String assignee;
    private String assigneeName;
    private String description;
    private Date occurredAt;

    public String getOccurredAt() {
        return BdDateUtils.formatDatetimeToString(occurredAt);
    }
}