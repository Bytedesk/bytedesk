package com.bytedesk.ticket.ticket_settings_category;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 工单分类设置草稿请求 DTO。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TicketCategorySettingsRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private List<TicketCategoryItemRequest> items = new ArrayList<>();
}