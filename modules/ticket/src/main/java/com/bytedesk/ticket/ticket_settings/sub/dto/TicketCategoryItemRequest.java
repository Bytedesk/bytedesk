package com.bytedesk.ticket.ticket_settings.sub.dto;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 单个工单分类项的请求 DTO。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TicketCategoryItemRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    @Builder.Default
    private Boolean enabled = Boolean.TRUE;

    @Builder.Default
    private Boolean defaultCategory = Boolean.FALSE;

    private Integer orderIndex;
}