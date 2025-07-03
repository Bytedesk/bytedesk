package com.bytedesk.freeswitch.config;

import java.time.ZonedDateTime;

import org.freeswitch.esl.client.IEslEventListener;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.bytedesk.freeswitch.call.event.FreeSwitchCallAnsweredEvent;
import com.bytedesk.freeswitch.call.event.FreeSwitchCallHangupEvent;
import com.bytedesk.freeswitch.call.event.FreeSwitchCallStartEvent;
import com.bytedesk.freeswitch.call.event.FreeSwitchDtmfEvent;
import com.bytedesk.freeswitch.cdr.FreeSwitchCdrEntity;
import com.bytedesk.freeswitch.cdr.FreeSwitchCdrService;
// import com.bytedesk.freeswitch.number.FreeSwitchNumberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch事件监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class FreeSwitchEventListener implements IEslEventListener {

    private final ApplicationEventPublisher eventPublisher;
    private final FreeSwitchCdrService cdrService;
    // private final FreeSwitchNumberService userService;
    
    /**
     * 处理FreeSwitch事件
     */
    @Override
    public void eventReceived(EslEvent eslEvent) {
            String eventName = eslEvent.getEventName();
        
        log.debug("收到FreeSwitch事件: {} / {}", eventName, eslEvent.getEventHeaders());
        
        // 处理不同类型的事件
        switch(eventName) {
            case "CHANNEL_CREATE":
                handleChannelCreate(eslEvent);
                break;
                
            case "CHANNEL_ANSWER":
                handleChannelAnswer(eslEvent);
                break;
                
            case "CHANNEL_HANGUP":
                handleChannelHangup(eslEvent);
                break;
                
            case "DTMF":
                handleDtmf(eslEvent);
                break;
                
            case "CUSTOM":
                handleCustomEvent(eslEvent);
                break;
                
            default:
                // 其他事件暂不处理
                break;
        }
    }

    @Override
    public void backgroundJobResultReceived(EslEvent event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'backgroundJobResultReceived'");
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
            FreeSwitchCdrEntity cdr = new FreeSwitchCdrEntity();
            cdr.setUid(uuid);
            cdr.setCallerIdNumber(callerId);
            cdr.setDestinationNumber(destination);
            cdr.setStartStamp(ZonedDateTime.now());
            cdr.setDirection("outbound"); // 默认为outbound，可根据实际情况调整
            cdr.setHangupCause(""); // 初始为空
            
            cdrService.createCdr(cdr);
            log.debug("已创建CDR记录: UUID {}", uuid);
        } catch (Exception e) {
            log.error("创建CDR记录失败: UUID {} - {}", uuid, e.getMessage(), e);
        }
        
        // 更新用户在线状态
        updateUserOnlineStatus(callerId, true);
        updateUserOnlineStatus(destination, true);
        
        // 发布通话开始事件
        eventPublisher.publishEvent(new FreeSwitchCallStartEvent(this, uuid, callerId, destination));
    }
    
    /**
     * 处理通道应答事件
     */
    private void handleChannelAnswer(EslEvent eslEvent) {
        String uuid = eslEvent.getEventHeaders().get("Unique-ID");
        
        log.info("通道应答: UUID {}", uuid);
        
        // 更新CDR记录 - 设置应答时间
        try {
            cdrService.updateCdrAnswerTime(uuid, ZonedDateTime.now());
            log.debug("已更新CDR应答时间: UUID {}", uuid);
        } catch (Exception e) {
            log.error("更新CDR应答时间失败: UUID {} - {}", uuid, e.getMessage(), e);
        }
        
        // 发布通话应答事件
        eventPublisher.publishEvent(new FreeSwitchCallAnsweredEvent(this, uuid));
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
            cdrService.updateCdrEndTime(uuid, ZonedDateTime.now(), hangupCause);
            log.debug("已更新CDR结束时间: UUID {} 原因 {}", uuid, hangupCause);
        } catch (Exception e) {
            log.error("更新CDR结束时间失败: UUID {} - {}", uuid, e.getMessage(), e);
        }
        
        // 发布通话挂断事件
        eventPublisher.publishEvent(new FreeSwitchCallHangupEvent(this, uuid, hangupCause));
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
        eventPublisher.publishEvent(new FreeSwitchDtmfEvent(this, uuid, digit));
    }
    
    /**
     * 处理自定义事件
     */
    private void handleCustomEvent(EslEvent eslEvent) {
        // String eventSubclass = eslEvent.getEventSubclass();
        
        // if ("bytedesk::custom".equals(eventSubclass)) {
        //     // 处理自定义事件
        //     log.info("自定义事件: {}", eslEvent.getEventHeaders());
        // }
    }
    
    /**
     * 更新用户在线状态
     */
    private void updateUserOnlineStatus(String username, boolean online) {
        try {
            // Optional<FreeSwitchNumberEntity> userOptional = userService.findByUsername(username);
            // if (userOptional.isPresent()) {
            //     if (online) {
            //         userService.updateLastRegistration(username, ZonedDateTime.now());
            //     }
            //     log.debug("已更新用户在线状态: {} -> {}", username, online);
            // }
        } catch (Exception e) {
            log.error("更新用户在线状态失败: {} - {}", username, e.getMessage(), e);
        }
    }
}
