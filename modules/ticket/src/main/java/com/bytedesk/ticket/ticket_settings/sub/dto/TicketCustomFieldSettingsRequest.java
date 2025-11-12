package com.bytedesk.ticket.ticket_settings.sub.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.Builder;

/**
 * 自定义字段草稿设置 DTO：content 为字段数组 JSON。
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketCustomFieldSettingsRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String content; // JSON blob
}
