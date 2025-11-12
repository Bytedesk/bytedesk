package com.bytedesk.ticket.ticket_settings.sub.dto;

import com.bytedesk.ticket.ticket_settings.sub.model.StatusFlowSettingsData;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 状态流转设置响应 DTO（结构化）。
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketStatusFlowSettingsResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private StatusFlowSettingsData content;
}
