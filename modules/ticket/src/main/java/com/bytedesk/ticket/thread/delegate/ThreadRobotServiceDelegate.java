/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-24 08:34:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-07 18:02:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import com.bytedesk.ticket.thread.ThreadConsts;

import lombok.extern.slf4j.Slf4j;

/**
 * 机器人接待服务
 * 
 * 纯机器人类型（THREAD_TYPE_ROBOT）不支持转人工
 * 工作组类型（THREAD_TYPE_WORKGROUP）支持机器人和转人工功能
 * 一对一客服类型（THREAD_TYPE_AGENT）仅支持人工，不应该执行机器人服务
 * 
 * 处理客服会话中的机器人自动应答环节
 * - 接收访客消息
 * - 调用AI模块进行回复
 * - 判断是否需要转人工服务
 * - 仅当访客手动触发时才转人工
 */
@Slf4j
@Component("threadRobotServiceDelegate")
public class ThreadRobotServiceDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        log.info("Robot service for thread process: {}", processInstanceId);
        
        // 获取流程变量
        String threadUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_THREAD_UID);
        String userUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_USER_UID);
        String workgroupUid = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_WORKGROUP_UID);
        log.info("Processing robot service for thread: {}, visitor: {}, workgroup: {}",
            threadUid, userUid, workgroupUid);
        
        // 记录机器人接待开始时间
        long startTime = System.currentTimeMillis();
        execution.setVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_START_TIME, startTime);
        
        try {
            // TODO: 实际项目中，这里需要调用AI服务进行机器人回复
            // 1. 获取访客最近的消息
            // 2. 调用AI服务生成回复
            // 3. 发送机器人回复给访客
            
            log.info("Robot processing thread: {}, visitor: {}", threadUid, userUid);
            
            // 获取会话类型
            String threadType = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_THREAD_TYPE);
            
            // 检查是否有访客手动请求转人工的标记
            Boolean visitorRequestedTransfer = (Boolean) execution.getVariable(
                ThreadConsts.THREAD_VARIABLE_VISITOR_REQUESTED_TRANSFER);
            
            // 获取转人工方式
            String transferType = (String) execution.getVariable(
                ThreadConsts.THREAD_VARIABLE_TRANSFER_TYPE);
            
            // 根据会话类型决定是否支持转人工
            boolean needHumanService = false;
            
            if (ThreadConsts.THREAD_TYPE_ROBOT.equals(threadType)) {
                // 纯机器人类型，永远不支持转人工
                needHumanService = false;
                
                // 如果用户请求了转人工，给用户发送提示消息
                if (visitorRequestedTransfer != null && visitorRequestedTransfer) {
                    log.info("访客请求转人工，但当前是纯机器人会话，不支持转人工: {}, 转人工方式: {}", threadUid, transferType);
                    // TODO: 发送系统消息告知访客此会话不支持转人工
                    // sendSystemMessage(threadUid, "很抱歉，当前会话不支持转接人工客服");
                    
                    // 重置访客请求转人工的标记
                    execution.setVariable(ThreadConsts.THREAD_VARIABLE_VISITOR_REQUESTED_TRANSFER, false);
                    execution.setVariable(ThreadConsts.THREAD_VARIABLE_TRANSFER_TYPE, null);
                }
                
                log.info("纯机器人会话类型，不允许转人工: {}", threadUid);
                
            } else if (ThreadConsts.THREAD_TYPE_WORKGROUP.equals(threadType)) {
                // 工作组类型，支持转人工
                needHumanService = (visitorRequestedTransfer != null && visitorRequestedTransfer);
                
                // 如果检测到访客请求转人工，则记录日志
                if (needHumanService) {
                    // 根据转人工方式设置不同的处理逻辑和优先级
                    if (ThreadConsts.TRANSFER_TYPE_UI.equals(transferType)) {
                        log.info("工作组会话类型，检测到访客 {} 通过UI按钮请求转人工，正在处理转接流程...", userUid);
                        
                        // UI按钮转人工，设置高优先级
                        execution.setVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_SUMMARY, "访客通过UI按钮请求转人工服务");
                        execution.setVariable(ThreadConsts.THREAD_VARIABLE_TRANSFER_REASON, "访客通过UI按钮请求");
                        execution.setVariable(ThreadConsts.THREAD_VARIABLE_TRANSFER_PRIORITY, 3); // 最高优先级
                    } else if (ThreadConsts.TRANSFER_TYPE_KEYWORD.equals(transferType)) {
                        log.info("工作组会话类型，检测到访客 {} 通过关键词请求转人工，正在处理转接流程...", userUid);
                        
                        // 关键词转人工，设置中等优先级
                        execution.setVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_SUMMARY, "访客通过关键词请求转人工服务");
                        execution.setVariable(ThreadConsts.THREAD_VARIABLE_TRANSFER_REASON, "访客通过关键词请求");
                        execution.setVariable(ThreadConsts.THREAD_VARIABLE_TRANSFER_PRIORITY, 2); // 中等优先级
                    } else {
                        log.info("工作组会话类型，检测到访客 {} 请求转人工，但未指定方式，正在处理转接流程...", userUid);
                        
                        // 未指定方式，设置默认优先级
                        execution.setVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_SUMMARY, "访客请求转人工服务");
                        execution.setVariable(ThreadConsts.THREAD_VARIABLE_TRANSFER_REASON, "访客请求");
                        execution.setVariable(ThreadConsts.THREAD_VARIABLE_TRANSFER_PRIORITY, 1); // 普通优先级
                    }
                    
                    // 重置访客请求转人工的标记
                    execution.setVariable(ThreadConsts.THREAD_VARIABLE_VISITOR_REQUESTED_TRANSFER, false);
                    execution.setVariable(ThreadConsts.THREAD_VARIABLE_TRANSFER_TYPE, null);
                }
            } else {
                // 其他类型（如一对一客服）不应该执行机器人服务
                log.warn("非机器人支持的会话类型执行了机器人服务: {} 类型: {}", threadUid, threadType);
                needHumanService = true;
            }
            
            // 设置是否需要人工服务的流程变量
            execution.setVariable(ThreadConsts.THREAD_VARIABLE_NEED_HUMAN_SERVICE, needHumanService);
            
            log.info("Robot service completed for thread: {}, thread type: {}, visitor requested transfer: {}, transfer type: {}, need human service: {}", 
                     threadUid, threadType, visitorRequestedTransfer, transferType, needHumanService);
            
        } catch (Exception e) {
            log.error("Error in robot service for thread: {}", threadUid, e);
            
            // 获取会话类型
            String threadType = (String) execution.getVariable(ThreadConsts.THREAD_VARIABLE_THREAD_TYPE);
            
            // 异常情况下，根据会话类型决定处理方式
            if (ThreadConsts.THREAD_TYPE_ROBOT.equals(threadType)) {
                // 纯机器人类型即使出错也不转人工
                execution.setVariable(ThreadConsts.THREAD_VARIABLE_NEED_HUMAN_SERVICE, false);
                log.warn("纯机器人会话处理异常，但不转人工: {}", threadUid);
            } else {
                // 其他类型转人工处理
                execution.setVariable(ThreadConsts.THREAD_VARIABLE_NEED_HUMAN_SERVICE, true);
                log.info("非纯机器人会话处理异常，转人工处理: {}", threadUid);
            }
            
            execution.setVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_ERROR, e.getMessage());
        } finally {
            // 记录机器人接待结束时间和总时长
            long endTime = System.currentTimeMillis();
            execution.setVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_END_TIME, endTime);
            execution.setVariable(ThreadConsts.THREAD_VARIABLE_ROBOT_SERVICE_DURATION, endTime - startTime);
        }
    }
}