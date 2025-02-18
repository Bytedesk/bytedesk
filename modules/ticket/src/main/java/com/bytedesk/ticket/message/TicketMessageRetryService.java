package com.bytedesk.ticket.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketMessageRetryService {

    private final TicketMessageRepository messageRepository;
    
    @Scheduled(fixedDelay = 300000) // 5分钟
    public void retryFailedMessages() {
        List<TicketMessageEntity> failedMessages = 
            messageRepository.findByStatusAndRetryCountLessThan(
                TicketMessageStatusEnum.FAILED, 3);
                
        for (TicketMessageEntity message : failedMessages) {
            try {
                // 重试发送消息
                message.setRetryCount(message.getRetryCount() + 1);
                messageRepository.save(message);
            } catch (Exception e) {
                log.error("消息重试失败: {}", e.getMessage());
            }
        }
    }
} 