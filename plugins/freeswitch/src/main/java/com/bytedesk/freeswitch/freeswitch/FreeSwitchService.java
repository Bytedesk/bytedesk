/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-07 16:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-07 16:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.freeswitch;

import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.transport.CommandResponse;
import org.freeswitch.esl.client.transport.SendMsg;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch核心服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class FreeSwitchService {

    private final Client eslClient;
    private final FreeSwitchProperties freeSwitchProperties;

    /**
     * 执行FreeSwitch API命令
     */
    public String executeApiCommand(String command, String args) {
        try {
            EslMessage response = eslClient.sendSyncApiCommand(command, args);
            if (response != null) {
                return response.getBodyLines().toString();
            }
            return null;
        } catch (Exception e) {
            log.error("执行API命令失败: {} {}", command, args, e);
            return null;
        }
    }

    /**
     * 发起呼叫
     */
    public String originate(String caller, String destination, String context) {
        String command = String.format(
            "user/%s &bridge(user/%s) XML %s %s %s", 
            caller, destination, context, caller, caller
        );
        
        log.info("发起呼叫: {}", command);
        return executeApiCommand("originate", command);
    }

    /**
     * 挂断呼叫
     */
    public String hangup(String uuid, String cause) {
        String args = String.format("%s %s", uuid, cause != null ? cause : "NORMAL_CLEARING");
        log.info("挂断呼叫: {}", args);
        return executeApiCommand("uuid_kill", args);
    }

    /**
     * 应答呼叫
     */
    public String answer(String uuid) {
        log.info("应答呼叫: {}", uuid);
        return executeApiCommand("uuid_answer", uuid);
    }

    /**
     * 转接呼叫
     */
    public String transfer(String uuid, String destination, String context) {
        String args = String.format("%s %s %s", uuid, destination, context);
        log.info("转接呼叫: {}", args);
        return executeApiCommand("uuid_transfer", args);
    }

    /**
     * 播放语音文件
     */
    public String playback(String uuid, String filePath) {
        String args = String.format("%s playback %s", uuid, filePath);
        log.info("播放语音: {}", args);
        return executeApiCommand("uuid_broadcast", args);
    }

    /**
     * 录音
     */
    public String record(String uuid, String filePath) {
        String args = String.format("%s start %s", uuid, filePath);
        log.info("开始录音: {}", args);
        return executeApiCommand("uuid_record", args);
    }

    /**
     * 停止录音
     */
    public String stopRecord(String uuid, String filePath) {
        String args = String.format("%s stop %s", uuid, filePath);
        log.info("停止录音: {}", args);
        return executeApiCommand("uuid_record", args);
    }

    /**
     * 获取通道变量
     */
    public String getChannelVar(String uuid, String varName) {
        String args = String.format("%s %s", uuid, varName);
        return executeApiCommand("uuid_getvar", args);
    }

    /**
     * 设置通道变量
     */
    public String setChannelVar(String uuid, String varName, String varValue) {
        String args = String.format("%s %s %s", uuid, varName, varValue);
        log.info("设置通道变量: {}", args);
        return executeApiCommand("uuid_setvar", args);
    }

    /**
     * 发送DTMF
     */
    public String sendDtmf(String uuid, String digits) {
        String args = String.format("%s %s", uuid, digits);
        log.info("发送DTMF: {}", args);
        return executeApiCommand("uuid_send_dtmf", args);
    }

    /**
     * 获取活动通道列表
     */
    public String showChannels() {
        return executeApiCommand("show", "channels");
    }

    /**
     * 获取活动呼叫数量
     */
    public String showCalls() {
        return executeApiCommand("show", "calls count");
    }

    /**
     * 重新加载模块
     */
    public String reloadModule(String moduleName) {
        log.info("重新加载模块: {}", moduleName);
        return executeApiCommand("reload", moduleName);
    }

    /**
     * 发送消息到通道
     */
    public void sendMessage(String uuid, String application, String... args) {
        try {
            SendMsg sendMsg = new SendMsg(uuid);
            sendMsg.addCallCommand("execute");
            sendMsg.addExecuteAppName(application);
            
            if (args != null && args.length > 0) {
                sendMsg.addExecuteAppArg(String.join(" ", args));
            }
            
            eslClient.sendMessage(sendMsg);
            log.info("发送消息到通道 {}: {} {}", uuid, application, String.join(" ", args));
        } catch (Exception e) {
            log.error("发送消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 检查FreeSwitch连接状态
     */
    public boolean isConnected() {
        try {
            String result = executeApiCommand("status", "");
            return result != null && !result.isEmpty();
        } catch (Exception e) {
            log.error("检查连接状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取FreeSwitch状态信息
     */
    public String getStatus() {
        return executeApiCommand("status", "");
    }

    /**
     * 创建用户
     */
    public String createUser(String domain, String userId, String password) {
        // 这里需要通过XML配置或数据库来创建用户
        // 由于FreeSwitch的用户管理通常通过配置文件，这里提供一个基础实现
        log.info("创建用户: {}@{}", userId, domain);
        
        // 可以通过API命令创建用户目录
        String userXml = String.format(
            "<user id=\"%s\"><params><param name=\"password\" value=\"%s\"/>" +
            "<param name=\"vm-password\" value=\"%s\"/></params>" +
            "<variables><variable name=\"toll_allow\" value=\"domestic,international,local\"/>" +
            "<variable name=\"accountcode\" value=\"%s\"/></variables></user>",
            userId, password, password, userId
        );
        
        // 注意：实际使用中需要将用户信息写入到目录配置文件或数据库
        return userXml;
    }

    /**
     * 获取配置信息
     */
    public FreeSwitchProperties getProperties() {
        return freeSwitchProperties;
    }
}
