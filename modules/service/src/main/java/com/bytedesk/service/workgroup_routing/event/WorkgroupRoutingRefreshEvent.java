package com.bytedesk.service.workgroup_routing.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * 异步刷新事件：presence/配置变化/定时任务触发，刷新 nextAgent（不推进 cursor）。
 */
@Getter
public class WorkgroupRoutingRefreshEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final String workgroupUid;
    /** 触发原因（用于日志/排查），可为空 */
    private final String reason;

    public WorkgroupRoutingRefreshEvent(Object source, String workgroupUid, String reason) {
        super(source);
        this.workgroupUid = workgroupUid;
        this.reason = reason;
    }
}
