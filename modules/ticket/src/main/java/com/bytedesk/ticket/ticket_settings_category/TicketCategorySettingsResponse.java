package com.bytedesk.ticket.ticket_settings_category;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 工单分类设置响应 DTO。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TicketCategorySettingsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private List<TicketCategoryItemResponse> items = new ArrayList<>();

    private String defaultCategoryUid;

    private Long enabledCount;

    private Long disabledCount;
}