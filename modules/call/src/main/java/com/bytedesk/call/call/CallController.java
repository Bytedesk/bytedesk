/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-07 16:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 12:12:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.call;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.call.config.CallConnectionTester;
import com.bytedesk.call.config.CallFreeswitchProperties;
import com.bytedesk.call.config.CallHealthIndicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Call REST API控制器 - 包含FreSwitch操作和管理功能
 */
@Slf4j
@RestController
@RequestMapping("/test/api/freeswitch")
@RequiredArgsConstructor
@Tag(name = "FreSwitch API", description = "FreSwitch呼叫控制和管理API")
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class CallController {

    private final CallCallService callService;
    private final CallFreeswitchProperties callProperties;
    private final CallConnectionTester connectionTester;
    private final CallHealthIndicator healthIndicator;

    /**
     * http://127.0.0.1:9003/test/api/freeswitch/status
     * 获取Call状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean connected = callService.isConnected();
            String status = callService.getStatus();
            
            // 获取配置信息并过滤敏感信息
            CallFreeswitchProperties properties = callService.getProperties();
            Map<String, Object> safeProperties = new HashMap<>();
            safeProperties.put("enabled", properties.isEnabled());
            safeProperties.put("server", properties.getServer());
            safeProperties.put("eslPort", properties.getEslPort());
            safeProperties.put("sipPort", properties.getSipPort());
            safeProperties.put("webrtcPort", properties.getWebrtcPort());
            safeProperties.put("wsPort", properties.getWsPort());
            safeProperties.put("callTimeout", properties.getCallTimeout());
            safeProperties.put("rtpPortStart", properties.getRtpPortStart());
            safeProperties.put("rtpPortEnd", properties.getRtpPortEnd());
            // eslPassword 已被过滤，不包含在返回结果中
            
            result.put("connected", connected);
            result.put("status", status);
            result.put("properties", safeProperties);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取状态失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * http://127.0.0.1:9003/test/api/freeswitch/call/originate?caller=sip:1001@14.103.165.199&amp;amp;amp;destination=sip:1002@14.103.165.199
     * 发起呼叫
     */
    @PostMapping("/call/originate")
    public ResponseEntity<Map<String, Object>> originate(
            @RequestParam String caller,
            @RequestParam String destination,
            @RequestParam(defaultValue = "default") String context) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = callService.originate(caller, destination, context);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("caller", caller);
            result.put("destination", destination);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("发起呼叫失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 挂断呼叫
     */
    @PostMapping("/call/hangup")
    public ResponseEntity<Map<String, Object>> hangup(
            @RequestParam String uuid,
            @RequestParam(required = false) String cause) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = callService.hangup(uuid, cause);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("uuid", uuid);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("挂断呼叫失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 应答呼叫
     */
    @PostMapping("/call/answer")
    public ResponseEntity<Map<String, Object>> answer(@RequestParam String uuid) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = callService.answer(uuid);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("uuid", uuid);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("应答呼叫失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 转接呼叫
     */
    @PostMapping("/call/transfer")
    public ResponseEntity<Map<String, Object>> transfer(
            @RequestParam String uuid,
            @RequestParam String destination,
            @RequestParam(defaultValue = "default") String context) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = callService.transfer(uuid, destination, context);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("uuid", uuid);
            result.put("destination", destination);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("转接呼叫失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 播放语音
     */
    @PostMapping("/call/playback")
    public ResponseEntity<Map<String, Object>> playback(
            @RequestParam String uuid,
            @RequestParam String filePath) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = callService.playback(uuid, filePath);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("uuid", uuid);
            result.put("filePath", filePath);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("播放语音失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 开始录音
     */
    @PostMapping("/call/record/start")
    public ResponseEntity<Map<String, Object>> startRecord(
            @RequestParam String uuid,
            @RequestParam String filePath) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = callService.record(uuid, filePath);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("uuid", uuid);
            result.put("filePath", filePath);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("开始录音失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 停止录音
     */
    @PostMapping("/call/record/stop")
    public ResponseEntity<Map<String, Object>> stopRecord(
            @RequestParam String uuid,
            @RequestParam String filePath) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = callService.stopRecord(uuid, filePath);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("uuid", uuid);
            result.put("filePath", filePath);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("停止录音失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 发送DTMF
     */
    @PostMapping("/call/dtmf")
    public ResponseEntity<Map<String, Object>> sendDtmf(
            @RequestParam String uuid,
            @RequestParam String digits) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = callService.sendDtmf(uuid, digits);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("uuid", uuid);
            result.put("digits", digits);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("发送DTMF失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 获取活动通道
     */
    @GetMapping("/channels")
    public ResponseEntity<Map<String, Object>> getChannels() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String channels = callService.showChannels();
            
            result.put("success", true);
            result.put("channels", channels);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取通道失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 获取活动呼叫数量
     */
    @GetMapping("/calls/count")
    public ResponseEntity<Map<String, Object>> getCallsCount() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String calls = callService.showCalls();
            
            result.put("success", true);
            result.put("calls", calls);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取呼叫数量失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 执行自定义API命令
     */
    @PostMapping("/api/execute")
    public ResponseEntity<Map<String, Object>> executeCommand(
            @RequestParam String command,
            @RequestParam(required = false) String args) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String response = callService.executeApiCommand(command, args);
            
            result.put("success", response != null);
            result.put("response", response);
            result.put("command", command);
            result.put("args", args);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("执行命令失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ==================== 管理和诊断功能 ====================

    /**
     * 获取FreSwitch配置信息
     */
    @GetMapping("/management/config")
    @Operation(summary = "获取FreSwitch配置信息", description = "返回当前的FreSwitch连接配置")
    public ResponseEntity<?> getConfig() {
        try {
            ConfigInfo config = new ConfigInfo();
            config.setEnabled(callProperties.isEnabled());
            config.setServer(callProperties.getServer());
            config.setEslPort(callProperties.getEslPort());
            config.setSipPort(callProperties.getSipPort());
            config.setWebrtcPort(callProperties.getWebrtcPort());
            config.setWsPort(callProperties.getWsPort());
            config.setCallTimeout(callProperties.getCallTimeout());
            config.setRtpPortStart(callProperties.getRtpPortStart());
            config.setRtpPortEnd(callProperties.getRtpPortEnd());
            // 密码不返回给客户端
            config.setPasswordConfigured(callProperties.getEslPassword() != null && 
                                       !callProperties.getEslPassword().isEmpty());
            
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            log.error("获取FreSwitch配置失败", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "获取配置失败: " + e.getMessage()));
        }
    }

    /**
     * 获取FreSwitch连接状态（管理功能）
     */
    @GetMapping("/management/status")
    @Operation(summary = "获取FreSwitch连接状态", description = "返回详细的连接状态和诊断信息")
    public ResponseEntity<?> getManagementStatus() {
        try {
            CallHealthIndicator.ConnectionStatus status = healthIndicator.getDetailedStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("获取FreSwitch状态失败", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "获取状态失败: " + e.getMessage()));
        }
    }

    /**
     * 测试FreSwitch连接
     */
    @PostMapping("/management/test-connection")
    @Operation(summary = "测试FreSwitch连接", description = "主动测试与FreSwitch的连接并返回结果")
    public ResponseEntity<?> testConnection() {
        try {
            log.info("管理员触发FreSwitch连接测试");
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
            log.error("FreSwitch连接测试失败", e);
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
    @GetMapping("/management/troubleshooting")
    @Operation(summary = "获取故障排除指南", description = "返回FreSwitch连接问题的排除指南")
    public ResponseEntity<?> getTroubleshootingGuide() {
        TroubleshootingGuide guide = new TroubleshootingGuide();
        
        guide.setCommonIssues(new String[]{
            "FreSwitch服务未运行",
            "ESL端口被防火墙阻止",
            "访问控制列表(ACL)限制",
            "密码配置错误",
            "网络连接问题"
        });
        
        guide.setDiagnosticCommands(new String[]{
            "sudo systemctl status freeswitch",
            "netstat -tlnp | grep 8021",
            "telnet " + callProperties.getServer() + " " + callProperties.getEslPort(),
            "fs_cli -x 'status'",
            "tail -f /usr/local/freeswitch/log/freeswitch.log"
        });
        
        guide.setConfigurationFiles(new String[]{
            "/usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml",
            "/usr/local/freeswitch/conf/autoload_configs/acl.conf.xml",
            "application-local.properties (Java应用配置)"
        });
        
        guide.setSolutions(new String[]{
            "检查并启动FreSwitch服务",
            "配置防火墙允许8021端口",
            "修改ACL配置允许客户端IP",
            "验证ESL密码配置",
            "使用SSH隧道进行安全连接"
        });
        
        return ResponseEntity.ok(guide);
    }

    // ==================== 内部类定义 ====================

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
