package com.bytedesk.ticket.ticket_settings.sub.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优先级配置数据结构，对应原 JSON priorities 数组。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrioritySettingsData implements Serializable {
    private static final long serialVersionUID = 1L;
    @Builder.Default
    private List<PriorityDef> priorities = new ArrayList<>();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriorityDef implements Serializable {
        private static final long serialVersionUID = 1L;
        private String code; // e.g. low/medium/high/urgent
        private String name;
        @Builder.Default
        private Integer level = 0; // 数值等级
        private String color; // 展示颜色
        @Builder.Default
        private Integer order = 0;
        @Builder.Default
        private Boolean active = Boolean.TRUE;
    }
}