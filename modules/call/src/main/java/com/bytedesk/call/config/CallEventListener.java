package com.bytedesk.call.config;

import com.bytedesk.call.esl.client.internal.Context;
import com.bytedesk.call.esl.client.transport.event.EslEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Call事件监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class CallEventListener implements com.bytedesk.call.esl.client.inbound.IEslEventListener {

    // 实现 IEslEventListener 的回调
    @Override
    public void onEslEvent(Context ctx, EslEvent eslEvent) {
        String eventName = eslEvent.getEventName();
        // log.info("收到Call事件: {}", eventName); // HEARTBEAT/RE_SCHEDULE

        switch (eventName) {
            case "CHANNEL_CREATE":
                handleChannelCreate(eslEvent);
                break;
            case "CHANNEL_ANSWER":
                handleChannelAnswer(eslEvent);
                break;
            case "CHANNEL_HANGUP":
                handleChannelHangup(eslEvent);
                break;
            case "CHANNEL_HANGUP_COMPLETE":
                handleChannelHangupComplete(eslEvent);
                break;
            case "CHANNEL_DESTROY":
                handleChannelDestroy(eslEvent);
                break;
            case "DTMF":
                handleDtmf(eslEvent);
                break;
            case "CUSTOM":
                handleCustomEvent(eslEvent);
                break;
            case "CHANNEL_EXECUTE":
                handleChannelExecute(eslEvent);
                break;
            case "CHANNEL_EXECUTE_COMPLETE":
                handleChannelExecuteComplete(eslEvent);
                break;
            case "CHANNEL_STATE":
                handleChannelState(eslEvent);
                break;
            case "CHANNEL_CALLSTATE":
                handleChannelCallState(eslEvent);
                break;
            case "PRESENCE_IN":
                handlePresenceIn(eslEvent);
                break;
            case "API":
                handleApiEvent(eslEvent);
                break;
            default:
                // log.info("handle default event: {}", eslEvent.getEventHeaders());
                break;
        }
    }

    /**
     * 处理通道创建事件
     */
    private void handleChannelCreate(EslEvent eslEvent) {
        String callerId = eslEvent.getEventHeaders().get("Caller-Caller-ID-Number");
        String destination = eslEvent.getEventHeaders().get("Caller-Destination-Number");
        String uuid = eslEvent.getEventHeaders().get("Unique-ID");

        log.info("通道创建: 主叫 {} 被叫 {} UUID {}", callerId, destination, uuid);

        // 创建CDR记录
        try {
            // CallCdrEntity cdr = new CallCdrEntity();
            // cdr.setUid(uuid);
            // cdr.setCallerIdNumber(callerId);
            // cdr.setDestinationNumber(destination);
            // cdr.setStartStamp(BdDateUtils.now());
            // cdr.setDirection("outbound"); // 默认为outbound，可根据实际情况调整
            // cdr.setHangupCause(""); // 初始为空

            // cdrService.createCdr(cdr);
            log.debug("已创建CDR记录: UUID {}", uuid);
        } catch (Exception e) {
            log.error("创建CDR记录失败: UUID {} - {}", uuid, e.getMessage(), e);
        }

        // 更新用户在线状态
        updateUserOnlineStatus(callerId, true);
        updateUserOnlineStatus(destination, true);
        log.debug("已更新用户在线状态: 主叫 {} 被叫 {}", callerId, destination);

        // 发布通话开始事件
        // eventPublisher.publishEvent(new CallCallStartEvent(this, uuid, callerId, destination));
    }

    /**
     * 处理通道应答事件
     */
    private void handleChannelAnswer(EslEvent eslEvent) {
        String uuid = eslEvent.getEventHeaders().get("Unique-ID");

        log.info("通道应答: UUID {}", uuid);

        // 更新CDR记录 - 设置应答时间
        try {
            // cdrService.updateCdrAnswerTime(uuid, BdDateUtils.now());
            log.debug("已更新CDR应答时间: UUID {}", uuid);
        } catch (Exception e) {
            log.error("更新CDR应答时间失败: UUID {} - {}", uuid, e.getMessage(), e);
        }

        // 发布通话应答事件
        // eventPublisher.publishEvent(new CallCallAnsweredEvent(this, uuid));
    }

    /**
     * 处理通道挂断事件
     */
    private void handleChannelHangup(EslEvent eslEvent) {
        String uuid = eslEvent.getEventHeaders().get("Unique-ID");
        String hangupCause = eslEvent.getEventHeaders().get("Hangup-Cause");

        log.info("通道挂断: UUID {} 原因 {}", uuid, hangupCause);

        // 更新CDR记录 - 设置结束时间和挂断原因
        try {
            // cdrService.updateCdrEndTime(uuid, BdDateUtils.now(), hangupCause);
            log.debug("已更新CDR结束时间: UUID {} 原因 {}", uuid, hangupCause);
        } catch (Exception e) {
            log.error("更新CDR结束时间失败: UUID {} - {}", uuid, e.getMessage(), e);
        }

        // 发布通话挂断事件
        // eventPublisher.publishEvent(new CallCallHangupEvent(this, uuid, hangupCause));
    }

    /**
     * 处理通道挂断完成事件
     */
    private void handleChannelHangupComplete(EslEvent eslEvent) {
        String uuid = eslEvent.getEventHeaders().get("Unique-ID");
        String hangupCause = eslEvent.getEventHeaders().getOrDefault("hangup_cause",
                eslEvent.getEventHeaders().get("Hangup-Cause"));
        String duration = eslEvent.getEventHeaders().getOrDefault("duration", "0");
        String billsec = eslEvent.getEventHeaders().getOrDefault("billsec", "0");

        log.info("通道挂断完成: UUID {} 原因 {} 通话时长(s) {} 计费时长(s) {}", uuid, hangupCause, duration, billsec);

        try {
            // cdrService.finalizeCdr(uuid, hangupCause, Integer.parseInt(duration), Integer.parseInt(billsec));
            log.debug("已最终完成CDR: UUID {}", uuid);
        } catch (Exception e) {
            log.error("最终完成CDR失败: UUID {} - {}", uuid, e.getMessage(), e);
        }
    }

    /**
     * 处理DTMF按键事件
     */
    private void handleDtmf(EslEvent eslEvent) {
        String uuid = eslEvent.getEventHeaders().get("Unique-ID");
        String digit = eslEvent.getEventHeaders().get("DTMF-Digit");

        log.info("DTMF按键: UUID {} 键值 {}", uuid, digit);

        // 调用通话服务处理
        // callService.handleDtmf(uuid, digit);
        // 发布DTMF事件
        // eventPublisher.publishEvent(new CallDtmfEvent(this, uuid, digit));
    }

    /**
     * 处理自定义事件
     */
    private void handleCustomEvent(EslEvent eslEvent) {
        // String eventSubclass = eslEvent.getEventSubclass();
        log.info("自定义事件: {}", eslEvent.getEventHeaders());

        // if ("bytedesk::custom".equals(eventSubclass)) {
        // // 处理自定义事件
        // log.info("自定义事件: {}", eslEvent.getEventHeaders());
        // }
    }

    /**
     * 处理应用执行事件
     */
    private void handleChannelExecute(EslEvent eslEvent) {
        var headers = eslEvent.getEventHeaders();
        String uuid = headers.get("Unique-ID");
        String application = headers.get("Application");
        String appData = headers.get("Application-Data");
        String currentApp = headers.get("variable_current_application");
        String currentAppData = headers.get("variable_current_application_data");

        log.info("应用执行: UUID {} App {} Data {} CurrApp {} CurrData {}", uuid, application, appData, currentApp,
                currentAppData);

        // 典型关键信息示例：录音文件、转接、拨号等
        String recordFile = headers.get("variable_record_filename");
        String executeOnAnswer = headers.get("variable_execute_on_answer");
        if (recordFile != null || executeOnAnswer != null) {
            log.debug("执行参数: record={} execute_on_answer={}", recordFile, executeOnAnswer);
        }
    }

    /**
     * 处理应用执行完成事件
     */
    private void handleChannelExecuteComplete(EslEvent eslEvent) {
        var headers = eslEvent.getEventHeaders();
        String uuid = headers.get("Unique-ID");
        String application = headers.get("Application");
        String response = headers.get("Application-Response");
        String recordFile = headers.get("variable_record_filename");

        log.info("应用执行完成: UUID {} App {} Response {} Record {}", uuid, application, response, recordFile);
    }

    /**
     * 处理通道状态事件
     */
    private void handleChannelState(EslEvent eslEvent) {
        var headers = eslEvent.getEventHeaders();
        String uuid = headers.get("Unique-ID");
        String state = headers.get("Channel-State");
        String callState = headers.get("Channel-Call-State");
        String answerState = headers.get("Answer-State");

        log.info("通道状态: UUID {} State {} CallState {} AnswerState {}", uuid, state, callState, answerState);
    }

    /**
     * 处理通话状态变更事件
     */
    private void handleChannelCallState(EslEvent eslEvent) {
        var headers = eslEvent.getEventHeaders();
        String uuid = headers.get("Unique-ID");
        String callState = headers.get("Channel-Call-State");
        String original = headers.get("Original-Channel-Call-State");
        String hangupCause = headers.get("Hangup-Cause");

        log.info("通话状态: UUID {} CallState {} -> {} Cause {}", uuid, original, callState, hangupCause);
    }

    /**
     * 处理Presence事件（座席/用户振铃、空闲等）
     */
    private void handlePresenceIn(EslEvent eslEvent) {
        var headers = eslEvent.getEventHeaders();
        String presenceId = headers.get("Channel-Presence-ID");
        String direction = headers.getOrDefault("presence-call-direction", headers.get("Presence-Call-Direction"));
        String infoState = headers.get("presence-call-info-state");
        String status = headers.get("status");

        log.info("Presence: {} direction={} infoState={} status={}", presenceId, direction, infoState, status);

        // 可在此更新坐席/用户实时状态
        // presenceService.update(presenceId, direction, infoState, status);
    }

    /**
     * 处理 API 事件（如 strftime 等调用）
     */
    private void handleApiEvent(EslEvent eslEvent) {
        var headers = eslEvent.getEventHeaders();
        String cmd = headers.get("API-Command");
        String arg = headers.get("API-Command-Argument");
        log.info("API事件: command={} arg={} headers={}", cmd, arg, headers);
    }

    /**
     * 处理通道销毁事件
     */
    private void handleChannelDestroy(EslEvent eslEvent) {
        var headers = eslEvent.getEventHeaders();
        String uuid = headers.get("Unique-ID");
        String hangupCause = headers.getOrDefault("Hangup-Cause", headers.get("variable_hangup_cause"));
        log.info("通道销毁: UUID {} 原因 {}", uuid, hangupCause);

        try {
            // cdrService.closeSession(uuid);
            log.debug("会话资源已清理: UUID {}", uuid);
        } catch (Exception e) {
            log.error("清理会话资源失败: UUID {} - {}", uuid, e.getMessage(), e);
        }
    }

    /**
     * 更新用户在线状态
     */
    private void updateUserOnlineStatus(String username, boolean online) {
        log.debug("更新用户在线状态: {} -> {}", username, online);

        try {
            // Optional<CallNumberEntity> userOptional =
            // userService.findByUsername(username);
            // if (userOptional.isPresent()) {
            // if (online) {
            // userService.updateLastRegistration(username, BdDateUtils.now());
            // }
            // log.debug("已更新用户在线状态: {} -> {}", username, online);
            // }
        } catch (Exception e) {
            log.error("更新用户在线状态失败: {} - {}", username, e.getMessage(), e);
        }
    }

    
}
