/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 12:33:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-17 16:32:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action.disruptor;

// import org.springframework.stereotype.Service;

// import com.lmax.disruptor.RingBuffer;

// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Service
// @AllArgsConstructor
// public class LogEventService {

//     private final RingBuffer<LogEvent> logEventRingBuffer;

//     public void testMq(String content) {
//         log.info("record the content: {}", content);
//         // 获取下一个Event槽的下标
//         long sequence = logEventRingBuffer.next();
//         try {
//             // 给Event填充数据
//             LogEvent event = logEventRingBuffer.get(sequence);
//             event.setContent(content);
//             log.info("add event to mq: {}", event);
//         } catch (Exception e) {
//             log.error("failed to add event to longEventRingBuffer for : e = {},{}", e, e.getMessage());
//         } finally {
//             // 发布Event，激活观察者去消费，将sequence传递给改消费者
//             // 注意最后的publish方法必须放在finally中以确保必须得到调用；如果某个请求的sequence未被提交将会堵塞后续的发布操作或者其他的producer
//             logEventRingBuffer.publish(sequence);
//         }
//     }

// }
