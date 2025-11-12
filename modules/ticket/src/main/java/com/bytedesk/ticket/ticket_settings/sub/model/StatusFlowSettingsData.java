package com.bytedesk.ticket.ticket_settings.sub.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 状态流转配置数据结构，对应原 JSON {statuses:[], transitions:[]}。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusFlowSettingsData implements Serializable {
    private static final long serialVersionUID = 1L;
    @Builder.Default
    private List<StatusDef> statuses = new ArrayList<>();
    @Builder.Default
    private List<TransitionDef> transitions = new ArrayList<>();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatusDef implements Serializable {
        private static final long serialVersionUID = 1L;
        private String code;
        private String name;
        @Builder.Default
        private Integer order = 0;
        @Builder.Default
        private Boolean active = Boolean.TRUE;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransitionDef implements Serializable {
        private static final long serialVersionUID = 1L;
        private String from;
        private String to;
        private String condition; // 预留条件表达式
    }
}