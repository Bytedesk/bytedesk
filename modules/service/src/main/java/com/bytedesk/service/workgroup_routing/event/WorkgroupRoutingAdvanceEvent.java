package com.bytedesk.service.workgroup_routing.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * 异步推进事件：一次分配完成后，推进 cursor 并刷新 nextAgent。
 * 仅携带最小必要信息，避免实体序列化/懒加载问题。
 */
@Getter
public class WorkgroupRoutingAdvanceEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final String workgroupUid;
    private final String assignedAgentUid;

    public WorkgroupRoutingAdvanceEvent(Object source, String workgroupUid, String assignedAgentUid) {
        super(source);
        this.workgroupUid = workgroupUid;
        this.assignedAgentUid = assignedAgentUid;
    }
}
