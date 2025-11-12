package com.bytedesk.ticket.ticket_settings.sub.model;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义字段设置的数据载体（持久化为 JSON）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldSettingsData implements Serializable {
    /** 自定义字段集合 */
    @Builder.Default
    private List<CustomFieldDef> fields = new ArrayList<>();
}
