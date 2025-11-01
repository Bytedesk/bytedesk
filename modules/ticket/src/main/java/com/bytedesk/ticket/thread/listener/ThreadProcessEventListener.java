/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-01 14:08:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-08 20:18:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread.listener;

// import java.time.Duration;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Optional;

// import org.flowable.engine.RuntimeService;
// import org.flowable.engine.TaskService;
// import org.flowable.engine.runtime.ProcessInstance;
// import org.flowable.task.api.Task;
// import org.springframework.context.event.EventListener;
// import org.springframework.stereotype.Component;

// import com.bytedesk.core.thread.ThreadEntity;
// import com.bytedesk.core.thread.ThreadRestService;
// import com.bytedesk.core.thread.event.ThreadAgentQueueEvent;
// import com.bytedesk.core.thread.event.ThreadAgentOfflineEvent;
// import com.bytedesk.core.thread.event.ThreadProcessCreateEvent;
// import com.bytedesk.core.thread.event.ThreadTransferToAgentEvent;
// import com.bytedesk.core.utils.Utils;
// import com.bytedesk.ticket.thread.ThreadConsts;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * @author jackning 270580156@qq.com
//  * 
//  * 会话流程创建事件监听器
//  * 
//  * 1. 当会话创建时，创建会话流程实例
//  * 2. 为流程实例设置必要的变量
//  * 3. 启动流程实例
//  * 4. 创建任务
//  * 5. 设置流程实例变量
//  * 6. 设置 SLA 时间
//  * 7. 设置人工客服空闲超时时间
//  * 8. 设置机器人超时时间
//  * 9. 设置任务变量
//  * 
//  * 注意：
//  * 1. 这里仅处理了基本的会话类型，如一对一客服接待、工作组接待、机器人接待等
//  * 
//  */
// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class ThreadProcessEventListener {

//     private final RuntimeService runtimeService;

//     private final TaskService taskService;

//     private final ThreadRestService threadRestService;

//     @EventListener
//     public void onThreadProcessCreateEvent(ThreadProcessCreateEvent event) {
//         log.info("ticket - ThreadProcessCreateEvent: {}", event);
//         ThreadEntity thread = event.getThread();
//         if (thread == null) {
//             log.error("会话线程创建事件, 线程对象为空: {}", event);
//             return;
//         }
//         log.info("开始创建会话流程实例: threadUid={}, orgUid={}", thread.getUid(), thread.getOrgUid());
        
//         // 1. 准备流程变量
//         Map<String, Object> variables = new HashMap<>();
//         // 基本变量
//         variables.put(ThreadConsts.THREAD_VARIABLE_THREAD_UID, thread.getUid());
//         variables.put(ThreadConsts.THREAD_VARIABLE_ORGUID, thread.getOrgUid());
//         variables.put(ThreadConsts.THREAD_VARIABLE_STATUS, thread.getStatus());
//         variables.put(ThreadConsts.THREAD_VARIABLE_START_TIME, new Date());
        
//         // 设置初始的访客最后消息时间
//         Date now = new Date();
//         variables.put(ThreadConsts.THREAD_VARIABLE_LAST_VISITOR_MESSAGE_TIME, now);
//         variables.put(ThreadConsts.THREAD_VARIABLE_LAST_VISITOR_ACTIVITY_TIME, now);
        
//         // 设置默认值，避免流程执行时找不到变量
//         // 设置 SLA 时间 - 预先设置默认值（毫秒值，用于业务逻辑）
//         int slaTime = ThreadConsts.DEFAULT_SLA_TIME;
//         variables.put(ThreadConsts.THREAD_VARIABLE_SLA_TIME, slaTime);
        
//         // 设置 SLA 时间（ISO格式，用于定时器）
//         String slaTimeIso = formatDurationToIso8601(slaTime);
//         variables.put(ThreadConsts.THREAD_VARIABLE_SLA_TIME_ISO, slaTimeIso);
//         log.info("SLA超时时间设置为: {}ms ({})", slaTime, slaTimeIso);
        
//         // 设置人工客服空闲超时时间（毫秒值，用于业务逻辑）
//         int humanIdleTimeout = ThreadConsts.DEFAULT_HUMAN_IDLE_TIMEOUT;
//         variables.put(ThreadConsts.THREAD_VARIABLE_HUMAN_IDLE_TIMEOUT, humanIdleTimeout);
        
//         // 设置人工客服空闲超时时间（ISO格式，用于定时器）
//         String humanIdleTimeoutIso = formatDurationToIso8601(humanIdleTimeout);
//         variables.put(ThreadConsts.THREAD_VARIABLE_HUMAN_IDLE_TIMEOUT_ISO, humanIdleTimeoutIso);
//         log.info("人工客服空闲超时时间设置为: {}ms ({})", humanIdleTimeout, humanIdleTimeoutIso);
        
//         // 设置机器人空闲超时时间（毫秒值，用于业务逻辑）
//         int robotIdleTimeout = ThreadConsts.DEFAULT_ROBOT_IDLE_TIMEOUT;
//         variables.put(ThreadConsts.THREAD_VARIABLE_ROBOT_IDLE_TIMEOUT, robotIdleTimeout);
        
//         // 设置机器人空闲超时时间（ISO格式，用于定时器）
//         String robotIdleTimeoutIso = formatDurationToIso8601(robotIdleTimeout);
//         variables.put(ThreadConsts.THREAD_VARIABLE_ROBOT_IDLE_TIMEOUT_ISO, robotIdleTimeoutIso);
//         log.info("机器人空闲超时时间设置为: {}ms ({})", robotIdleTimeout, robotIdleTimeoutIso);
        
//         // 添加控制流程流转的变量
//         variables.put(ThreadConsts.THREAD_VARIABLE_NEED_HUMAN_SERVICE, false);  // 默认不需要转人工
//         variables.put(ThreadConsts.THREAD_VARIABLE_THREAD_STATUS, ThreadConsts.THREAD_STATUS_NEW);   // 使用常量引用初始状态
        
//         // 显式初始化机器人相关变量，避免空指针和循环问题
//         variables.put(ThreadConsts.THREAD_VARIABLE_ROBOT_UNANSWERED_COUNT, 0);
//         variables.put(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_EXECUTION_COUNT, 0);
//         // 初始化访客请求转人工的标志
//         variables.put(ThreadConsts.THREAD_VARIABLE_VISITOR_REQUESTED_TRANSFER, false);
        
//         if (thread.getUserProtobuf() != null) {
//             variables.put(ThreadConsts.THREAD_VARIABLE_USER_UID, thread.getUserProtobuf().getUid());
//         }
//         // 设置客服是否繁忙变量
//         variables.put(ThreadConsts.THREAD_VARIABLE_AGENTS_BUSY, thread.isQueuing());
//         variables.put(ThreadConsts.THREAD_VARIABLE_WORKGROUP_UID, "");
//         // 设置为非机器人接待
//         variables.put(ThreadConsts.THREAD_VARIABLE_ROBOT_ENABLED, thread.isRoboting());
//         // 设置客服是否离线
//         variables.put(ThreadConsts.THREAD_VARIABLE_AGENTS_OFFLINE, thread.isOffline());
        
//         // 设置会话类型，用于在delegate中判断是否支持转人工等操作
//         if (thread.isAgentType()) {
//             variables.put(ThreadConsts.THREAD_VARIABLE_THREAD_TYPE, ThreadConsts.THREAD_TYPE_AGENT);
//         } else if (thread.isWorkgroupType()) {
//             variables.put(ThreadConsts.THREAD_VARIABLE_THREAD_TYPE, ThreadConsts.THREAD_TYPE_WORKGROUP);
//         } else if (thread.isRobotType()) {
//             variables.put(ThreadConsts.THREAD_VARIABLE_THREAD_TYPE, ThreadConsts.THREAD_TYPE_ROBOT);
//         } else {
//             variables.put(ThreadConsts.THREAD_VARIABLE_THREAD_TYPE, "unknown");
//         }
        
//         // 根据不同的会话类型设置相应的流程变量
//         if (thread.isAgentType()) {
//             // 一对一客服接待，直接指定assignee为特定客服
//             if (thread.getAgentProtobuf() != null) {
//                 variables.put(ThreadConsts.THREAD_VARIABLE_ASSIGNEE, thread.getAgentProtobuf().getUid());
//                 variables.put(ThreadConsts.THREAD_VARIABLE_AGENT_UID, thread.getAgentProtobuf().getUid());
//                 log.info("一对一客服接待，设置assignee为: {}", thread.getAgentProtobuf().getUid());
//             } else {
//                 log.error("一对一客服接待，但客服信息为空");
//             }
//         } else if (thread.isWorkgroupType()) {
//             // 工作组接待，设置candidateGroups为工作组ID
//             if (thread.getWorkgroupProtobuf() != null) {
//                 variables.put(ThreadConsts.THREAD_VARIABLE_WORKGROUP_UID, thread.getWorkgroupProtobuf().getUid());
//                 log.info("工作组接待，设置candidateGroups为: {}", thread.getWorkgroupProtobuf().getUid());
//             } else {
//                 log.error("工作组接待，但工作组信息为空");
//             }
//         } else if (thread.isRobotType()) {
//             // 机器人接待
//             variables.put(ThreadConsts.THREAD_VARIABLE_ROBOT_ENABLED, true);  // 确保这里设置为true
//             if (thread.getAgentProtobuf() != null) {
//                 variables.put(ThreadConsts.THREAD_VARIABLE_AGENT_UID, thread.getAgentProtobuf().getUid());
//                 log.info("机器人接待，设置robotUid为: {}", thread.getAgentProtobuf().getUid());
//             }
//         } else {
//             // 暂时仅处理上述客服会话类型
//             log.warn("未知的会话类型，不处理");
//             return;
//         }
        
//         // 2. 启动流程实例
//         ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
//                 .processDefinitionKey(ThreadConsts.THREAD_PROCESS_KEY)
//                 .tenantId(thread.getOrgUid())
//                 .name(thread.getUid()) // 流程实例名称
//                 .businessKey(thread.getUid())
//                 .variables(variables)
//                 .start();
//         log.info("会话流程实例创建成功: processInstanceId={}, businessKey={}",
//                 processInstance.getId(), processInstance.getBusinessKey());

//         // 在后续操作前先检查流程实例是否还存在
//         boolean isProcessInstanceActive = checkProcessInstanceActive(processInstance.getId());
        
//         // 3. 创建任务 - 只有在流程实例仍然活跃时才创建任务
//         if (isProcessInstanceActive && thread.isAgentType() && thread.getAgentProtobuf() != null) {
//             try {
//                 Task task = taskService.createTaskQuery()
//                     .processInstanceId(processInstance.getId())
//                     .taskAssignee(thread.getAgentProtobuf().getUid()) // 指定任务负责人
//                     .singleResult();
//                 if (task != null) {
//                     // 完成会话创建任务
//                     taskService.complete(task.getId());
//                 } else {
//                     log.warn("会话创建任务未找到，可能是因为流程已自动执行到其他节点: threadUid={}", thread.getUid());
//                 }
//             } catch (Exception e) {
//                 log.error("创建或完成任务时出错: {}", e.getMessage());
//             }
//         }

//         // 7. 更新会话的流程实例ID - 无论流程是否活跃都需要更新
//         Optional<ThreadEntity> threadOptional = threadRestService.findByUid(thread.getUid());
//         if (threadOptional.isPresent()) {
//             ThreadEntity threadEntity = threadOptional.get();
//             String processUid = Utils.formatUid(threadEntity.getOrgUid(), ThreadConsts.THREAD_PROCESS_KEY);
//             threadEntity.setProcessEntityUid(processUid);
//             threadEntity.setProcessInstanceId(processInstance.getId());
//             threadRestService.save(threadEntity);
//         }
//     }
    

//     @EventListener
//     public void onThreadTransferToAgentEvent(ThreadTransferToAgentEvent event) {
//         // 处理会话转人工事件，例如更新流程变量等
//         ThreadEntity thread = event.getThread();
//         if (thread == null) {
//             log.error("会话转人工事件, 会话对象为空: {}", event);
//             return;
//         }
        
//         // 检查线程是否有关联的流程实例ID
//         if (thread.getProcessInstanceId() == null) {
//             log.error("会话转人工事件, 但会话没有关联的流程实例ID: threadUid={}", thread.getUid());
//             return;
//         }
        
//         log.info("处理会话转人工事件: threadUid={}, processInstanceId={}", thread.getUid(), thread.getProcessInstanceId());
        
//         try {
//             // 设置流程变量，标记访客通过UI请求转人工
//             runtimeService.setVariable(thread.getProcessInstanceId(), 
//                     ThreadConsts.THREAD_VARIABLE_VISITOR_REQUESTED_TRANSFER, true);
            
//             // 关键修复：设置需要人工服务变量为true，这样流程才能从transferToHumanTask流转到下一步
//             runtimeService.setVariable(thread.getProcessInstanceId(),
//                     ThreadConsts.THREAD_VARIABLE_NEED_HUMAN_SERVICE, true);
            
//             // 设置转人工方式为UI
//             runtimeService.setVariable(thread.getProcessInstanceId(),
//                     ThreadConsts.THREAD_VARIABLE_TRANSFER_TYPE, ThreadConsts.TRANSFER_TYPE_UI);
            
//             // 转人工时额外设置优先级为高
//             runtimeService.setVariable(thread.getProcessInstanceId(),
//                     ThreadConsts.THREAD_VARIABLE_TRANSFER_PRIORITY, 3); // 最高优先级
            
//             // 设置转人工原因
//             runtimeService.setVariable(thread.getProcessInstanceId(),
//                     ThreadConsts.THREAD_VARIABLE_TRANSFER_REASON, "访客从工作组UI请求转人工");
            
//             log.info("已设置访客通过UI请求转人工标记: threadUid={}, processInstanceId={}", 
//                     thread.getUid(), thread.getProcessInstanceId());
//         } catch (Exception e) {
//             log.error("设置访客通过UI请求转人工标记失败: {}", e.getMessage(), e);
//         }
//     }

//     @EventListener
//     public void onThreadAgentOfflineEvent(ThreadAgentOfflineEvent event) {
//         // 处理客服离线事件，更新流程变量
//         ThreadEntity thread = event.getThread();
//         if (thread == null) {
//             log.error("客服离线事件, 会话对象为空: {}", event);
//             return;
//         }
        
//         // 检查线程是否有关联的流程实例ID
//         if (thread.getProcessInstanceId() == null) {
//             log.error("客服离线事件, 但会话没有关联的流程实例ID: threadUid={}", thread.getUid());
//             return;
//         }
        
//         log.info("处理客服离线事件: threadUid={}, processInstanceId={}", thread.getUid(), thread.getProcessInstanceId());
        
//         // 添加重试机制
//         int maxRetries = 3;
//         int retryCount = 0;
//         boolean success = false;
        
//         while (!success && retryCount < maxRetries) {
//             try {
//                 // 设置客服离线状态变量
//                 runtimeService.setVariable(thread.getProcessInstanceId(), 
//                         ThreadConsts.THREAD_VARIABLE_AGENTS_OFFLINE, true);
                
//                 // 设置线程状态为离线
//                 runtimeService.setVariable(thread.getProcessInstanceId(),
//                         ThreadConsts.THREAD_VARIABLE_THREAD_STATUS, ThreadConsts.THREAD_STATUS_OFFLINE);
                
//                 log.info("已更新客服离线状态变量: threadUid={}, processInstanceId={}", 
//                         thread.getUid(), thread.getProcessInstanceId());
                
//                 success = true;
//             } catch (org.flowable.common.engine.api.FlowableOptimisticLockingException e) {
//                 // 并发修改异常，需要重试
//                 retryCount++;
//                 log.warn("设置客服离线状态变量发生并发修改异常，尝试重试 #{}: {}", retryCount, e.getMessage());
                
//                 try {
//                     // 短暂延时后重试
//                     Thread.sleep(200 * retryCount);
//                 } catch (InterruptedException ie) {
//                     Thread.currentThread().interrupt();
//                     log.error("重试时线程被中断", ie);
//                     break;
//                 }
//             } catch (Exception e) {
//                 log.error("设置客服离线状态变量失败: {}", e.getMessage(), e);
//                 break;
//             }
//         }
        
//         if (!success) {
//             log.error("多次尝试后仍无法更新客服离线状态变量: threadUid={}", thread.getUid());
//         }
//     }

//     @EventListener
//     public void onThreadAgentQueueEvent(ThreadAgentQueueEvent event) {
//         // 处理客服繁忙事件，更新流程变量
//         ThreadEntity thread = event.getThread();
//         if (thread == null) {
//             log.error("客服繁忙事件, 会话对象为空: {}", event);
//             return;
//         }
        
//         // 检查线程是否有关联的流程实例ID
//         if (thread.getProcessInstanceId() == null) {
//             log.error("客服繁忙事件, 但会话没有关联的流程实例ID: threadUid={}", thread.getUid());
//             return;
//         }
        
//         log.info("处理客服繁忙事件: threadUid={}, processInstanceId={}", thread.getUid(), thread.getProcessInstanceId());
        
//         try {
//             // 设置客服繁忙状态变量
//             runtimeService.setVariable(thread.getProcessInstanceId(), 
//                     ThreadConsts.THREAD_VARIABLE_AGENTS_BUSY, true);
            
//             // 设置线程状态为排队中
//             runtimeService.setVariable(thread.getProcessInstanceId(),
//                     ThreadConsts.THREAD_VARIABLE_THREAD_STATUS, ThreadConsts.THREAD_STATUS_QUEUING);
            
//             // 记录进入排队时间，用于计算排队时长
//             runtimeService.setVariable(thread.getProcessInstanceId(),
//                     ThreadConsts.THREAD_VARIABLE_QUEUE_START_TIME, new Date());
            
//             log.info("已更新客服繁忙状态变量: threadUid={}, processInstanceId={}", 
//                     thread.getUid(), thread.getProcessInstanceId());
//         } catch (Exception e) {
//             log.error("设置客服繁忙状态变量失败: {}", e.getMessage(), e);
//         }
//     }

//     /**
//      * 检查流程实例是否仍然活跃
//      * 
//      * @param processInstanceId 流程实例ID
//      * @return 如果流程实例仍然活跃，则返回true；否则返回false
//      */
//     private Boolean checkProcessInstanceActive(String processInstanceId) {
//         try {
//             // 尝试查询流程实例
//             ProcessInstance instance = runtimeService.createProcessInstanceQuery()
//                     .processInstanceId(processInstanceId)
//                     .singleResult();
//             return instance != null;
//         } catch (Exception e) {
//             log.warn("检查流程实例状态时出错: {}", e.getMessage());
//             return false;
//         }
//     }

//     /**
//      * 将毫秒时间转换为Flowable定时器可识别的ISO 8601持续时间格式
//      * 格式为: PT{n}S，其中{n}表示秒数
//      * 
//      * @param milliseconds 毫秒数
//      * @return ISO 8601持续时间格式字符串
//      */
//     private String formatDurationToIso8601(int milliseconds) {
//         // 将毫秒转换成秒
//         long seconds = milliseconds / 1000;
//         return Duration.ofSeconds(seconds).toString();
//     }

// }
