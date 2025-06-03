package com.bytedesk.freeswitch.handler;

import org.freeswitch.esl.client.IEslEventListener;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.springframework.stereotype.Component;

import com.bytedesk.freeswitch.service.ICallService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch事件监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FreeSwitchEventListener implements IEslEventListener {

    private final ICallService callService;
    
    /**
     * 处理FreeSwitch事件
     */
    @Override
    public void eventReceived(EslEvent eslEvent) {
            String eventName = eslEvent.getEventName();
        // String eventSubclass = eslEvent.getEventSubclass();
        
        log.debug("收到FreeSwitch事件: {} / {}", eventName);
        
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
        
        // 调用通话服务处理
        callService.handleCallStart(callerId, destination, uuid);
    }
    
    /**
     * 处理通道应答事件
     */
    private void handleChannelAnswer(EslEvent eslEvent) {
        String uuid = eslEvent.getEventHeaders().get("Unique-ID");
        
        log.info("通道应答: UUID {}", uuid);
        
        // 调用通话服务处理
        callService.handleCallAnswered(uuid);
    }
    
    /**
     * 处理通道挂断事件
     */
    private void handleChannelHangup(EslEvent eslEvent) {
        String uuid = eslEvent.getEventHeaders().get("Unique-ID");
        String hangupCause = eslEvent.getEventHeaders().get("Hangup-Cause");
        
        log.info("通道挂断: UUID {} 原因 {}", uuid, hangupCause);
        
        // 调用通话服务处理
        callService.handleCallEnd(uuid, hangupCause);
    }
    
    /**
     * 处理DTMF按键事件
     */
    private void handleDtmf(EslEvent eslEvent) {
        String uuid = eslEvent.getEventHeaders().get("Unique-ID");
        String digit = eslEvent.getEventHeaders().get("DTMF-Digit");
        
        log.info("DTMF按键: UUID {} 键值 {}", uuid, digit);
        
        // 调用通话服务处理
        callService.handleDtmf(uuid, digit);
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
}
