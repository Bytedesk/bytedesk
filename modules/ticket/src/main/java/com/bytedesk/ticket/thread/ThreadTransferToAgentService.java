/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-05 22:25:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-07 22:28:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread;

import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.thread.ThreadEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 会话消息处理服务
 * 
 * 处理访客发送的消息，检测是否包含转人工请求
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThreadTransferToAgentService {

    private final RuntimeService runtimeService;

    // 转人工关键词列表
    private static final String[] TRANSFER_KEYWORDS = {
            "转人工", "人工客服", "真人客服", "人工服务", "真人服务", "转接人工",
            "转接客服", "人工", "客服", "转接", "转人", "人工解答"
    };

    /**
     * 处理访客消息
     * 
     * @param message 访客发送的消息
     * @param thread  会话线程
     */
    public void processVisitorMessage(MessageEntity message, ThreadEntity thread) {
        // 基础检查
        if (message == null || thread == null) {
            return;
        }

        // 检查是否是访客消息
        // 注意：这里需要根据实际的消息类型判断逻辑进行调整
        if (!isVisitorMessage(message, thread)) {
            return;
        }

        String content = message.getContent();
        if (content == null || content.trim().isEmpty()) {
            return;
        }

        // 检查是否包含转人工关键词
        boolean requestTransfer = checkTransferRequest(content);
        if (requestTransfer && thread.getProcessInstanceId() != null) {
            log.info("检测到访客请求通过关键词转人工: threadUid={}, content={}", thread.getUid(), content);

            // 设置流程变量，标记访客请求转人工
            try {
                // 使用常量替代直接的字符串值
                runtimeService.setVariable(thread.getProcessInstanceId(),
                        ThreadConsts.THREAD_VARIABLE_VISITOR_REQUESTED_TRANSFER, true);

                // 设置转人工方式为关键词
                runtimeService.setVariable(thread.getProcessInstanceId(),
                        ThreadConsts.THREAD_VARIABLE_TRANSFER_TYPE, ThreadConsts.TRANSFER_TYPE_KEYWORD);

                log.info("已设置访客通过关键词请求转人工标记: processInstanceId={}", thread.getProcessInstanceId());

                /*
                 * visitorRequestedTransfer 变量使用说明:
                 * 
                 * 1. 此变量在流程中用于标记访客是否主动请求了转人工服务
                 * 2. 可以在流程的服务任务（如robotService）中检查此变量，从而决定是否进行转人工操作
                 * 3. 在BPMN流程文件中可以添加条件表达式，例如：${visitorRequestedTransfer == true}
                 * 4. 建议在ThreadRobotServiceDelegate等相关服务委托类中使用此变量
                 * 5. 示例：
                 * if (execution.getVariable(ThreadConsts.
                 * THREAD_VARIABLE_VISITOR_REQUESTED_TRANSFER) == Boolean.TRUE) {
                 * execution.setVariable(ThreadConsts.THREAD_VARIABLE_NEED_HUMAN_SERVICE, true);
                 * }
                 */
            } catch (Exception e) {
                log.error("设置访客请求转人工标记失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 处理UI按钮转人工请求
     * 
     * @param thread 会话线程
     */
    public void processUiTransferRequest(ThreadEntity thread) {
        if (thread == null || thread.getProcessInstanceId() == null) {
            log.error("处理UI转人工请求失败：会话或流程实例ID为空");
            return;
        }

        try {
            // 设置流程变量，标记访客通过UI请求转人工
            runtimeService.setVariable(thread.getProcessInstanceId(),
                    ThreadConsts.THREAD_VARIABLE_VISITOR_REQUESTED_TRANSFER, true);

            // 设置转人工方式为UI
            runtimeService.setVariable(thread.getProcessInstanceId(),
                    ThreadConsts.THREAD_VARIABLE_TRANSFER_TYPE, ThreadConsts.TRANSFER_TYPE_UI);

            log.info("已设置访客通过UI请求转人工标记: threadUid={}, processInstanceId={}",
                    thread.getUid(), thread.getProcessInstanceId());
        } catch (Exception e) {
            log.error("设置访客通过UI请求转人工标记失败: {}", e.getMessage());
        }
    }

    /**
     * 判断消息是否是访客发送的
     * 
     * @param message 消息实体
     * @param thread  会话线程
     * @return 如果是访客消息返回true，否则返回false
     */
    private Boolean isVisitorMessage(MessageEntity message, ThreadEntity thread) {
        // 判断消息发送者是否为访客
        // 这里需要根据实际的消息类型和用户角色判断逻辑进行调整

        // 方法1: 如果MessageEntity有isUserType()方法
        if (message.isFromVisitor()) {
            return true;
        }

        // 方法2: 根据消息的发送者uid和会话中的访客uid比较
        // if (thread.getUserProtobuf() != null &&
        // thread.getUserProtobuf().getUid().equals(message.getCreatorUid())) {
        // return true;
        // }

        return false;
    }

    /**
     * 检查消息内容是否包含转人工请求关键词
     * 
     * @param content 消息内容
     * @return 如果包含转人工关键词返回true，否则返回false
     */
    private Boolean checkTransferRequest(String content) {
        String normalizedContent = content.trim().toLowerCase();
        for (String keyword : TRANSFER_KEYWORDS) {
            if (normalizedContent.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
