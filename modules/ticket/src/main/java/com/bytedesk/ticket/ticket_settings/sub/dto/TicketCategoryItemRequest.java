package com.bytedesk.ticket.ticket_settings.sub.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 单个工单分类项的请求 DTO。
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TicketCategoryItemRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;

    private String name;

    private String description;

    @Builder.Default
    private Boolean enabled = Boolean.TRUE;

    @Builder.Default
    private Boolean defaultCategory = Boolean.FALSE;

    private Integer orderIndex;
}