package com.bytedesk.ticket.ticket_settings.sub.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 工单分类设置响应 DTO。
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TicketCategorySettingsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private List<TicketCategoryItemResponse> items = new ArrayList<>();

    private String defaultCategoryUid;

    private Long enabledCount;

    private Long disabledCount;

    private ZonedDateTime updatedAt;
}