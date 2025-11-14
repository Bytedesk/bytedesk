package com.bytedesk.ticket.ticket_settings.sub.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 工单分类设置草稿请求 DTO。
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TicketCategorySettingsRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private List<TicketCategoryItemRequest> items = new ArrayList<>();
}