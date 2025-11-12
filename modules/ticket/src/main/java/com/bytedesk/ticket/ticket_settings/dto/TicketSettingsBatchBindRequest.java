package com.bytedesk.ticket.ticket_settings.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 专用于批量绑定的请求体：仅包含工作组 UID 列表。
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketSettingsBatchBindRequest {
    private List<String> workgroupUids;
}
