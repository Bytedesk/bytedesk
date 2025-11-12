package com.bytedesk.ticket.ticket_settings.sub.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 优先级设置响应 DTO。
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketPrioritySettingsResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String content; // JSON blob
}
