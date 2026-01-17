/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 13:20:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-17 16:32:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action.disruptor;

// import java.util.concurrent.Executors;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import com.lmax.disruptor.RingBuffer;
// import com.lmax.disruptor.dsl.Disruptor;

// @Configuration
// public class LogEventConfig {

//     @Bean
//     public RingBuffer<LogEvent> logEventRingBuffer() {
//         // 消费者线程池
//         // ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("weiyuai-pool-%d").build();
//         // 事件工厂
//         LogEventFactory eventFactory = new LogEventFactory();
//         // 指定RingBuffer大小
//         int bufferSize = 1024;
//         // 构造事件分发器
//         Disruptor<LogEvent> disruptor = new Disruptor<LogEvent>(eventFactory, bufferSize,
//                 Executors.defaultThreadFactory());
//         // 注册消费者
//         disruptor.handleEventsWith(new LogEventHandler());
//         // 启动事件分发
//         disruptor.start();
//         // 获取RingBuffer 用于生产事件
//         RingBuffer<LogEvent> ringBuffer = disruptor.getRingBuffer();
//         //
//         return ringBuffer;
//     }
// }
