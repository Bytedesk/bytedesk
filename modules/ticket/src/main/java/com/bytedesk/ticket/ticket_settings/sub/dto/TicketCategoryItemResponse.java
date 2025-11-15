package com.bytedesk.ticket.ticket_settings.sub.dto;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 单个工单分类项的响应 DTO。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TicketCategoryItemResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private Boolean enabled;

    private Boolean defaultCategory;

    private Integer orderIndex;
}