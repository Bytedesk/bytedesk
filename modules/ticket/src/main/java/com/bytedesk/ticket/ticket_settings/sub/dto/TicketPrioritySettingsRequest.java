package com.bytedesk.ticket.ticket_settings.sub.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.Builder;

/**
 * 优先级设置草稿请求 DTO：content 为 JSON priorities 数组。
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketPrioritySettingsRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String content; // JSON blob
}
