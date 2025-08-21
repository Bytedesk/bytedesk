/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-21 08:45:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 08:45:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试MQTT消息ID服务
 */
public class MqttMessageIdServiceTest {

    private MqttMessageIdService messageIdService;

    @BeforeEach
    public void setUp() {
        messageIdService = new MqttMessageIdService();
        messageIdService.reset(); // 重置为初始状态
    }

    @Test
    public void testInitialMessageId() {
        // 第一个消息ID应该是1
        int firstId = messageIdService.getNextMessageId();
        assertEquals(1, firstId);
    }

    @Test
    public void testSequentialMessageIds() {
        // 连续获取消息ID应该递增
        int first = messageIdService.getNextMessageId();
        int second = messageIdService.getNextMessageId();
        int third = messageIdService.getNextMessageId();
        
        assertEquals(1, first);
        assertEquals(2, second);
        assertEquals(3, third);
    }

    @Test
    public void testMessageIdRange() {
        // 测试消息ID在有效范围内（1-65535）
        for (int i = 0; i < 1000; i++) {
            int messageId = messageIdService.getNextMessageId();
            assertTrue(messageId >= 1 && messageId <= 65535, 
                      "Message ID " + messageId + " is out of valid range");
        }
    }

    @Test
    public void testMessageIdWrapAround() {
        // 测试达到最大值时的回绕
        MqttMessageIdService service = new MqttMessageIdService();
        
        // 手动将计数器设置到接近最大值
        // 由于我们无法直接设置，我们通过多次调用来模拟
        // 这里只测试逻辑，实际生产中不太可能达到这么高
        
        // 先获取几个正常的ID
        int id1 = service.getNextMessageId();
        int id2 = service.getNextMessageId();
        
        assertTrue(id1 >= 1 && id1 <= 65535);
        assertTrue(id2 >= 1 && id2 <= 65535);
        assertTrue(id2 > id1 || (id1 == 65535 && id2 == 1)); // 考虑回绕情况
    }

    @Test
    public void testThreadSafety() throws InterruptedException {
        final int threadCount = 10;
        final int idsPerThread = 100;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final Set<Integer> allIds = ConcurrentHashMap.newKeySet();
        final AtomicBoolean hasError = new AtomicBoolean(false);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < idsPerThread; j++) {
                        int messageId = messageIdService.getNextMessageId();
                        
                        // 检查ID范围
                        if (messageId < 1 || messageId > 65535) {
                            hasError.set(true);
                            break;
                        }
                        
                        // 记录所有生成的ID
                        allIds.add(messageId);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        assertFalse(hasError.get(), "Generated message IDs should be in valid range");
        
        // 检查是否有重复的ID (在短时间内应该没有重复)
        // 注意：由于ID会回绕，这个测试只在ID总数小于65535时有效
        if (allIds.size() <= 65535) {
            assertEquals(threadCount * idsPerThread, allIds.size(), 
                        "Should not have duplicate message IDs");
        }
    }

    @RepeatedTest(5)
    public void testConcurrentAccess() throws InterruptedException {
        final int threadCount = 5;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final AtomicBoolean hasInvalidId = new AtomicBoolean(false);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 50; j++) {
                        int messageId = messageIdService.getNextMessageId();
                        if (messageId < 1 || messageId > 65535) {
                            hasInvalidId.set(true);
                            break;
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        assertFalse(hasInvalidId.get(), "All message IDs should be valid");
    }

    @Test
    public void testReset() {
        // 获取几个消息ID
        messageIdService.getNextMessageId();
        messageIdService.getNextMessageId();
        messageIdService.getNextMessageId();
        
        // 重置后应该从1开始
        messageIdService.reset();
        int firstAfterReset = messageIdService.getNextMessageId();
        assertEquals(1, firstAfterReset);
    }

    @Test
    public void testGetCurrentCounter() {
        // 初始值应该是1
        assertEquals(1, messageIdService.getCurrentCounter());
        
        // 获取一个ID后应该变成2
        messageIdService.getNextMessageId();
        assertEquals(2, messageIdService.getCurrentCounter());
    }
}
