/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 14:50:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 14:50:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Call管理API
 * 提供Call连接状态查询和诊断功能
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/freeswitch")
@RequiredArgsConstructor
@Tag(name = "Call Management", description = "Call连接管理和诊断API")
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class CallManagementController {

    private final CallProperties freeSwitchProperties;
    private final CallConnectionTester connectionTester;
    private final CallHealthIndicator healthIndicator;

    /**
     * 获取Call配置信息
     */
    @GetMapping("/config")
    @Operation(summary = "获取Call配置信息", description = "返回当前的Call连接配置")
    public ResponseEntity<?> getConfig() {
        try {
            ConfigInfo config = new ConfigInfo();
            config.setEnabled(freeSwitchProperties.isEnabled());
            config.setServer(freeSwitchProperties.getServer());
            config.setEslPort(freeSwitchProperties.getEslPort());
            config.setSipPort(freeSwitchProperties.getSipPort());
            config.setWebrtcPort(freeSwitchProperties.getWebrtcPort());
            config.setWsPort(freeSwitchProperties.getWsPort());
            config.setCallTimeout(freeSwitchProperties.getCallTimeout());
            config.setRtpPortStart(freeSwitchProperties.getRtpPortStart());
            config.setRtpPortEnd(freeSwitchProperties.getRtpPortEnd());
            // 密码不返回给客户端
            config.setPasswordConfigured(freeSwitchProperties.getEslPassword() != null && 
                                       !freeSwitchProperties.getEslPassword().isEmpty());
            
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            log.error("获取Call配置失败", e);
            return ResponseEntity.internalServerError()
                    .body("获取配置失败: " + e.getMessage());
        }
    }

    /**
     * 获取Call连接状态
     */
    @GetMapping("/status")
    @Operation(summary = "获取Call连接状态", description = "返回详细的连接状态和诊断信息")
    public ResponseEntity<?> getStatus() {
        try {
            CallHealthIndicator.ConnectionStatus status = healthIndicator.getDetailedStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("获取Call状态失败", e);
            return ResponseEntity.internalServerError()
                    .body("获取状态失败: " + e.getMessage());
        }
    }

    /**
     * 测试Call连接
     */
    @PostMapping("/test-connection")
    @Operation(summary = "测试Call连接", description = "主动测试与Call的连接并返回结果")
    public ResponseEntity<?> testConnection() {
        try {
            log.info("管理员触发Call连接测试");
            connectionTester.testConnection();
            
            // 获取测试后的状态
            CallHealthIndicator.ConnectionStatus status = healthIndicator.getDetailedStatus();
            
            return ResponseEntity.ok()
                    .body(new TestResult(
                        "连接测试完成",
                        status.isConnectable(),
                        status.getMessage(),
                        status.getTroubleshooting()
                    ));
        } catch (Exception e) {
            log.error("Call连接测试失败", e);
            return ResponseEntity.internalServerError()
                    .body(new TestResult(
                        "连接测试异常", 
                        false, 
                        e.getMessage(),
                        "检查系统日志获取详细错误信息"
                    ));
        }
    }

    /**
     * 获取故障排除指南
     */
    @GetMapping("/troubleshooting")
    @Operation(summary = "获取故障排除指南", description = "返回Call连接问题的排除指南")
    public ResponseEntity<?> getTroubleshootingGuide() {
        TroubleshootingGuide guide = new TroubleshootingGuide();
        
        guide.setCommonIssues(new String[]{
            "Call服务未运行",
            "ESL端口被防火墙阻止",
            "访问控制列表(ACL)限制",
            "密码配置错误",
            "网络连接问题"
        });
        
        guide.setDiagnosticCommands(new String[]{
            "sudo systemctl status freeswitch",
            "netstat -tlnp | grep 8021",
            "telnet " + freeSwitchProperties.getServer() + " " + freeSwitchProperties.getEslPort(),
            "fs_cli -x 'status'",
            "tail -f /usr/local/freeswitch/log/freeswitch.log"
        });
        
        guide.setConfigurationFiles(new String[]{
            "/usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml",
            "/usr/local/freeswitch/conf/autoload_configs/acl.conf.xml",
            "application-local.properties (Java应用配置)"
        });
        
        guide.setSolutions(new String[]{
            "检查并启动Call服务",
            "配置防火墙允许8021端口",
            "修改ACL配置允许客户端IP",
            "验证ESL密码配置",
            "使用SSH隧道进行安全连接"
        });
        
        return ResponseEntity.ok(guide);
    }

    /**
     * 配置信息类
     */
    public static class ConfigInfo {
        private boolean enabled;
        private String server;
        private int eslPort;
        private int sipPort;
        private int webrtcPort;
        private int wsPort;
        private int callTimeout;
        private int rtpPortStart;
        private int rtpPortEnd;
        private boolean passwordConfigured;

        // Getters and Setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public String getServer() { return server; }
        public void setServer(String server) { this.server = server; }
        
        public int getEslPort() { return eslPort; }
        public void setEslPort(int eslPort) { this.eslPort = eslPort; }
        
        public int getSipPort() { return sipPort; }
        public void setSipPort(int sipPort) { this.sipPort = sipPort; }
        
        public int getWebrtcPort() { return webrtcPort; }
        public void setWebrtcPort(int webrtcPort) { this.webrtcPort = webrtcPort; }
        
        public int getWsPort() { return wsPort; }
        public void setWsPort(int wsPort) { this.wsPort = wsPort; }
        
        public int getCallTimeout() { return callTimeout; }
        public void setCallTimeout(int callTimeout) { this.callTimeout = callTimeout; }
        
        public int getRtpPortStart() { return rtpPortStart; }
        public void setRtpPortStart(int rtpPortStart) { this.rtpPortStart = rtpPortStart; }
        
        public int getRtpPortEnd() { return rtpPortEnd; }
        public void setRtpPortEnd(int rtpPortEnd) { this.rtpPortEnd = rtpPortEnd; }
        
        public boolean isPasswordConfigured() { return passwordConfigured; }
        public void setPasswordConfigured(boolean passwordConfigured) { this.passwordConfigured = passwordConfigured; }
    }

    /**
     * 测试结果类
     */
    public static class TestResult {
        private String message;
        private boolean success;
        private String details;
        private String troubleshooting;

        public TestResult(String message, boolean success, String details, String troubleshooting) {
            this.message = message;
            this.success = success;
            this.details = details;
            this.troubleshooting = troubleshooting;
        }

        // Getters and Setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }
        
        public String getTroubleshooting() { return troubleshooting; }
        public void setTroubleshooting(String troubleshooting) { this.troubleshooting = troubleshooting; }
    }

    /**
     * 故障排除指南类
     */
    public static class TroubleshootingGuide {
        private String[] commonIssues;
        private String[] diagnosticCommands;
        private String[] configurationFiles;
        private String[] solutions;

        // Getters and Setters
        public String[] getCommonIssues() { return commonIssues; }
        public void setCommonIssues(String[] commonIssues) { this.commonIssues = commonIssues; }
        
        public String[] getDiagnosticCommands() { return diagnosticCommands; }
        public void setDiagnosticCommands(String[] diagnosticCommands) { this.diagnosticCommands = diagnosticCommands; }
        
        public String[] getConfigurationFiles() { return configurationFiles; }
        public void setConfigurationFiles(String[] configurationFiles) { this.configurationFiles = configurationFiles; }
        
        public String[] getSolutions() { return solutions; }
        public void setSolutions(String[] solutions) { this.solutions = solutions; }
    }
}
