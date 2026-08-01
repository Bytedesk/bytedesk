/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-20 12:00:00
 * @Description: 工单流程 - 通知委托
 *  用于在流程执行过程中发送通知（站内消息、邮件、短信）
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 */
package com.bytedesk.ticket.process.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 流程通知委托 - TicketBuilder 生成 BPMN 中 notification 节点对应的 JavaDelegate
 * <p>
 * BPMN 示例：
 * {@code <serviceTask id="xxx" name="通知" flowable:class="com.bytedesk.ticket.process.delegate.NotificationDelegate" />}
 * </p>
 * <p>
 * 流程变量约定：
 * - notificationChannels: String (逗号分隔: message,email,sms)
 * - notificationTemplate: String (通知模板内容)
 * - notificationRecipientUid: String (接收人 UID)
 * - ticketUid: String (关联工单 UID)
 * </p>
 */
@Slf4j
@Component("notificationDelegate")
@RequiredArgsConstructor
public class NotificationDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        String activityName = execution.getCurrentActivityName();
        String activityId = execution.getCurrentActivityId();

        // 读取流程变量
        String channels = (String) execution.getVariable("notificationChannels");
        // String template = (String) execution.getVariable("notificationTemplate");
        String recipientUid = (String) execution.getVariable("notificationRecipientUid");
        String ticketUid = (String) execution.getVariable("ticketUid");

        log.info("[NotificationDelegate] processInstanceId={}, activityId={}, activityName={}",
                processInstanceId, activityId, activityName);
        log.info("[NotificationDelegate] channels={}, recipientUid={}, ticketUid={}",
                channels, recipientUid, ticketUid);

        // TODO: 实际发送通知逻辑
        // 1. 解析 channels 确定通知渠道
        // 2. 根据 template 渲染通知内容
        // 3. 调用 MessageRestService / EmailService / SmsService 发送通知
        // 4. 记录通知发送日志

        if (channels != null && channels.contains("message")) {
            log.info("[NotificationDelegate] 站内消息通知: recipientUid={}", recipientUid);
        }
        if (channels != null && channels.contains("email")) {
            log.info("[NotificationDelegate] 邮件通知: recipientUid={}", recipientUid);
        }
        if (channels != null && channels.contains("sms")) {
            log.info("[NotificationDelegate] 短信通知: recipientUid={}", recipientUid);
        }
    }
}
