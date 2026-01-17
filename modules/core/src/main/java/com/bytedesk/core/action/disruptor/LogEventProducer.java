/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 12:31:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-17 16:40:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action.disruptor;

// import org.springframework.stereotype.Component;

// import com.lmax.disruptor.RingBuffer;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class LogEventProducer {

//     private final RingBuffer<LogEvent> ringBuffer;

//     public void simulate() {
//         log.info("simulate producer");
//         // 模拟日志生成
//         for (int i = 0; i < 10000; i++) {
//             long sequence = ringBuffer.next();
//             try {
//                 LogEvent event = ringBuffer.get(sequence);
//                 event.setContent("Log Message " + i); // 模拟日志消息
//             } finally {
//                 ringBuffer.publish(sequence);
//             }
//         }

//     }
// }
