package com.bytedesk.ticket.ticket_settings.sub.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 单个自定义字段定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldDef implements Serializable {
    private String key;          // 唯一标识
    private String name;         // 展示名称
    private String type;         // 类型: text, select, multi_select, number, date, user, org 等
    @Builder.Default
    private boolean required = false; // 是否必填
    @Singular("option")
    private List<String> options;     // 可选项（当类型为 select/multi_select）
    private String defaultValue;      // 默认值
    @Builder.Default
    private Integer order = 0;        // 排序
    @Builder.Default
    private boolean active = true;    // 是否启用
}
