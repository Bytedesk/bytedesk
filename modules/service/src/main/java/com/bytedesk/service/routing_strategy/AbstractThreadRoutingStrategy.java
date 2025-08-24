/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:57:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 16:15:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.routing_strategy;

import java.util.Optional;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 线程路由策略抽象基类
 * 
 * <p>提供线程路由策略的通用功能和模板方法，减少具体策略实现的重复代码
 * 
 * <p>主要功能：
 * - 线程状态检查和验证
 * - 通用的线程操作方法
 * - 消息内容处理
 * - 异常处理和日志记录
 * 
 * @author jackning 270580156@qq.com
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractThreadRoutingStrategy {

    // ==================== 常量定义 ====================
    
    /** 默认欢迎消息 */
    protected static final String DEFAULT_WELCOME_MESSAGE = "您好，请问有什么可以帮助您？";
    
    /** 默认离线消息 */
    protected static final String DEFAULT_OFFLINE_MESSAGE = "您好，请留言，我们会尽快回复您";
    
    /** 排队等待消息 - 下一个 */
    protected static final String QUEUE_NEXT_MESSAGE = "请稍后，下一个就是您";
    
    /** 排队等待消息模板 */
    protected static final String QUEUE_WAITING_MESSAGE_TEMPLATE = "当前排队人数：%d 大约等待时间：%d 分钟";
    
    /** 每人预估等待时间（分钟） */
    protected static final int ESTIMATED_WAIT_TIME_PER_PERSON = 2;
    
    /** 消息缓存时间（分钟） - 用于判断是否复用之前的消息 */
    protected static final int MESSAGE_CACHE_MINUTES = 30;

    // ==================== 抽象方法 ====================
    
    /**
     * 获取线程服务实例
     * 由具体实现类提供
     * 
     * @return ThreadRestService实例
     */
    protected abstract ThreadRestService getThreadRestService();

    // ==================== 通用方法 ====================
    
    /**
     * 验证线程实体不为空
     * 
     * @param thread 线程实体
     * @param operation 操作名称，用于错误日志
     * @throws IllegalArgumentException 如果线程为空
     */
    protected void validateThread(ThreadEntity thread, String operation) {
        Assert.notNull(thread, "ThreadEntity must not be null for operation: " + operation);
    }
    
    /**
     * 验证UID格式有效性
     * 
     * @param uid 要验证的UID
     * @param entityType 实体类型名称，用于错误信息
     * @throws IllegalArgumentException 如果UID无效
     */
    protected void validateUid(String uid, String entityType) {
        if (!StringUtils.hasText(uid)) {
            throw new IllegalArgumentException(entityType + " UID cannot be null or empty");
        }
    }
    
    /**
     * 根据UID获取线程实体
     * 
     * @param threadUid 线程UID
     * @return 线程实体
     * @throws IllegalArgumentException 如果线程不存在
     */
    protected ThreadEntity getThreadByUid(String threadUid) {
        validateUid(threadUid, "Thread");
        
        Optional<ThreadEntity> threadOptional = getThreadRestService().findByUid(threadUid);
        if (!threadOptional.isPresent()) {
            log.error("Thread with uid {} not found", threadUid);
            throw new IllegalArgumentException("Thread with uid " + threadUid + " not found");
        }
        
        return threadOptional.get();
    }
    
    /**
     * 保存线程实体并处理异常
     * 
     * @param thread 要保存的线程实体
     * @return 保存后的线程实体
     * @throws RuntimeException 如果保存失败
     */
    protected ThreadEntity saveThread(ThreadEntity thread) {
        validateThread(thread, "save");
        
        try {
            ThreadEntity savedThread = getThreadRestService().save(thread);
            if (savedThread == null) {
                log.error("Failed to save thread {}", thread.getUid());
                throw new RuntimeException("Failed to save thread " + thread.getUid());
            }
            
            log.debug("Successfully saved thread {}", savedThread.getUid());
            return savedThread;
        } catch (Exception e) {
            log.error("Error saving thread {}: {}", thread.getUid(), e.getMessage(), e);
            throw new RuntimeException("Failed to save thread " + thread.getUid(), e);
        }
    }
    
    /**
     * 检查线程是否为新创建状态
     * 
     * @param thread 线程实体
     * @return 如果是新状态返回true
     */
    protected boolean isNewThread(ThreadEntity thread) {
        validateThread(thread, "check new status");
        return thread.isNew();
    }
    
    /**
     * 检查线程是否处于活跃状态（聊天中或排队中）
     * 
     * @param thread 线程实体
     * @return 如果处于活跃状态返回true
     */
    protected boolean isActiveThread(ThreadEntity thread) {
        validateThread(thread, "check active status");
        return thread.isChatting() || thread.isQueuing() || thread.isRoboting();
    }
    
    /**
     * 检查线程是否已关闭
     * 
     * @param thread 线程实体
     * @return 如果已关闭返回true
     */
    protected boolean isClosedThread(ThreadEntity thread) {
        validateThread(thread, "check closed status");
        return thread.isClosed();
    }
    
    /**
     * 获取有效的欢迎消息
     * 
     * @param customMessage 自定义消息
     * @return 有效的欢迎消息，如果自定义消息为空则返回默认消息
     */
    protected String getValidWelcomeMessage(String customMessage) {
        return StringUtils.hasText(customMessage) ? customMessage : DEFAULT_WELCOME_MESSAGE;
    }
    
    /**
     * 获取有效的离线消息
     * 
     * @param customMessage 自定义消息
     * @return 有效的离线消息，如果自定义消息为空则返回默认消息
     */
    protected String getValidOfflineMessage(String customMessage) {
        return StringUtils.hasText(customMessage) ? customMessage : DEFAULT_OFFLINE_MESSAGE;
    }
    
    /**
     * 生成排队等待消息
     * 
     * @param queueingCount 排队人数
     * @return 排队等待消息
     */
    protected String generateQueueMessage(int queueingCount) {
        if (queueingCount <= 0) {
            return QUEUE_NEXT_MESSAGE;
        }
        
        int estimatedWaitTime = queueingCount * ESTIMATED_WAIT_TIME_PER_PERSON;
        return String.format(QUEUE_WAITING_MESSAGE_TEMPLATE, queueingCount, estimatedWaitTime);
    }
    
    /**
     * 检查消息是否在缓存时间内
     * 
     * @param messageTime 消息时间
     * @return 如果在缓存时间内返回true
     */
    protected boolean isMessageInCacheTime(java.time.LocalDateTime messageTime) {
        if (messageTime == null) {
            return false;
        }
        
        return messageTime.isAfter(BdDateUtils.now().minusMinutes(MESSAGE_CACHE_MINUTES).toLocalDateTime());
    }
    
    /**
     * 记录线程状态变更
     * 
     * @param thread 线程实体
     * @param operation 操作类型
     * @param fromStatus 原状态
     * @param toStatus 目标状态
     */
    protected void logThreadStatusChange(ThreadEntity thread, String operation, 
            String fromStatus, String toStatus) {
        log.info("Thread {} status changed during {}: {} -> {}", 
                thread.getUid(), operation, fromStatus, toStatus);
    }
    
    /**
     * 处理策略执行异常
     * 
     * @param operation 操作名称
     * @param error 异常信息
     * @param threadUid 线程UID（可选）
     * @return RuntimeException
     */
    protected RuntimeException handleStrategyException(String operation, Throwable error, String threadUid) {
        String message = StringUtils.hasText(threadUid) 
                ? String.format("Failed to %s for thread %s: %s", operation, threadUid, error.getMessage())
                : String.format("Failed to %s: %s", operation, error.getMessage());
        
        log.error(message, error);
        return new RuntimeException(message, error);
    }
    
    /**
     * 创建操作执行模板方法
     * 
     * @param <T> 返回类型
     * @param operation 操作名称
     * @param threadUid 线程UID
     * @param action 具体操作
     * @return 操作结果
     */
    protected <T> T executeWithExceptionHandling(String operation, String threadUid, 
            java.util.function.Supplier<T> action) {
        try {
            log.debug("Executing {} for thread {}", operation, threadUid);
            T result = action.get();
            log.debug("Successfully completed {} for thread {}", operation, threadUid);
            return result;
        } catch (Exception e) {
            throw handleStrategyException(operation, e, threadUid);
        }
    }
}