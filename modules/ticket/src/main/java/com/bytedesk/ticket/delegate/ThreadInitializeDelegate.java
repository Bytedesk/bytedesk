/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-24 09:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-24 09:40:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.UUID;

/**
 * 客服会话初始化服务
 * 
 * 负责在客服会话流程开始时进行初始化操作
 * - 生成并记录必要的流程变量
 * - 记录会话开始时间和基本信息
 * - 设置默认处理策略
 */
@Slf4j
@Component("threadInitializeDelegate")
public class ThreadInitializeDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        log.info("Initializing thread process: {}", processInstanceId);
        
        try {
            // 生成并记录会话UID（如果没有提供）
            String threadUid = (String) execution.getVariable("threadUid");
            if (threadUid == null || threadUid.isEmpty()) {
                threadUid = generateThreadUid();
                execution.setVariable("threadUid", threadUid);
            }
            
            // 记录会话开始时间
            Date startTime = new Date();
            execution.setVariable("threadStartTime", startTime);
            
            // 记录会话基本信息
            initializeBasicInfo(execution);
            
            // 设置处理策略
            initializeProcessingStrategy(execution);
            
            // 记录流程状态
            execution.setVariable("threadStatus", "INITIALIZED");
            
            log.info("Thread process initialized successfully: {}, threadUid: {}", 
                processInstanceId, threadUid);
        } catch (Exception e) {
            log.error("Error initializing thread process", e);
            execution.setVariable("initializationError", e.getMessage());
            execution.setVariable("threadStatus", "INITIALIZATION_FAILED");
        }
    }
    
    /**
     * 生成会话UID
     */
    private String generateThreadUid() {
        // 生成唯一的会话ID
        return "TH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * 初始化会话基本信息
     */
    private void initializeBasicInfo(DelegateExecution execution) {
        // 获取访客ID（如果已提供）
        String visitorId = (String) execution.getVariable("visitorId");
        if (visitorId == null || visitorId.isEmpty()) {
            // 生成默认访客ID
            visitorId = "VISITOR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            execution.setVariable("visitorId", visitorId);
        }
        
        // 记录会话来源
        String source = (String) execution.getVariable("source");
        if (source == null || source.isEmpty()) {
            // 默认来源
            source = "WEBSITE";
            execution.setVariable("source", source);
        }
        
        // 记录访客信息
        String visitorName = (String) execution.getVariable("visitorName");
        if (visitorName == null || visitorName.isEmpty()) {
            visitorName = "访客" + visitorId.substring(visitorId.length() - 4);
            execution.setVariable("visitorName", visitorName);
        }
        
        // 记录访客IP和地理位置（如果有）
        if (execution.getVariable("visitorIp") == null) {
            execution.setVariable("visitorIp", "0.0.0.0");
        }
        
        if (execution.getVariable("visitorLocation") == null) {
            execution.setVariable("visitorLocation", "未知");
        }
        
        // 记录访客设备信息（如果有）
        if (execution.getVariable("visitorDevice") == null) {
            execution.setVariable("visitorDevice", "未知");
        }
        
        // 设置会话优先级（默认为普通）
        if (execution.getVariable("priority") == null) {
            execution.setVariable("priority", "NORMAL");
        }
        
        log.info("Basic info initialized for thread: {}, visitor: {}, source: {}", 
            execution.getVariable("threadUid"), visitorId, source);
    }
    
    /**
     * 初始化处理策略
     */
    private void initializeProcessingStrategy(DelegateExecution execution) {
        // 设置是否启用机器人
        if (execution.getVariable("enableRobot") == null) {
            execution.setVariable("enableRobot", true);
        }
        
        // 设置机器人UID（如果启用机器人）
        if ((Boolean) execution.getVariable("enableRobot") && execution.getVariable("robotUid") == null) {
            execution.setVariable("robotUid", "DEFAULT_ROBOT");
        }
        
        // 设置机器人空闲超时时间（访客长时间不回复）
        if (execution.getVariable("robotIdleTimeout") == null) {
            execution.setVariable("robotIdleTimeout", "PT5M"); // 5分钟
        }
        
        // 设置是否允许转接
        if (execution.getVariable("allowTransfer") == null) {
            execution.setVariable("allowTransfer", true);
        }
        
        // 设置坐席组ID（如果有）
        if (execution.getVariable("agentGroupId") == null) {
            execution.setVariable("agentGroupId", "DEFAULT_GROUP");
        }
        
        // 设置会话超时时间（秒）
        if (execution.getVariable("sessionTimeout") == null) {
            execution.setVariable("sessionTimeout", 7200); // 2小时
        }
        
        // 设置排队策略
        if (execution.getVariable("queueStrategy") == null) {
            execution.setVariable("queueStrategy", "FIFO"); // 先进先出
        }
        
        // 设置SLA时间（秒）
        if (execution.getVariable("slaTime") == null) {
            execution.setVariable("slaTime", 1800); // 30分钟
        }
        
        log.info("Processing strategy initialized for thread: {}", execution.getVariable("threadUid"));
    }
}