/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *   仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   技术/商务联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ticket.ticket;

import lombok.Getter;

/**
 * 工作流任务已结束专用异常：taskId 属于当前工单流程实例的已结束历史任务（重复提交）时抛出。
 *
 * <p>用于服务内部识别「前端残留旧 taskId 的重复确认」（如工单验证后对话窗口按钮未收敛、
 * 多端/刷新竞态），由 {@link TicketService#executeWorkflowAction} 捕获后幂等返回当前工单，
 * 不向用户暴露「任务不存在或已结束」原始异常。</p>
 *
 * <p>真正的非法 taskId（查无历史任务 / 不属于该流程实例）仍抛原有 RuntimeException，不使用本异常。</p>
 */
@Getter
public class TicketWorkflowTaskAlreadyEndedException extends RuntimeException {

    private final String taskId;
    private final String ticketUid;

    public TicketWorkflowTaskAlreadyEndedException(String taskId, String ticketUid) {
        super("workflow task already ended: taskId=" + taskId + ", ticketUid=" + ticketUid);
        this.taskId = taskId;
        this.ticketUid = ticketUid;
    }
}
