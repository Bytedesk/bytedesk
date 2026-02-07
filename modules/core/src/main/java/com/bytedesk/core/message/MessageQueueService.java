package com.bytedesk.core.message;

import org.springframework.stereotype.Service;

/**
 * TODO: 消息队列服务
 * 
 * - [] 所有消息只有在前端明确收到，并回复给服务器端回执。否则服务器端定时推送此消息。在此实现此重发机制。
 * 所有待重发消息存储在消息队列中，定时扫描未收到回执的消息，进行重发，直到收到回执为止。
 */
@Service
public class MessageQueueService {
    
}
