/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 14:01:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-17 16:47:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bytedesk.core.action.disruptor.LogEventProducer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = { StarterApplication.class })
public class DisruptorTests {

    @Autowired
    private LogEventProducer logEventProducer;

    // FIXME: 没有正常打印log
    @Test
    public void testLogEvent() {
        System.out.println("testLogEvent system start");
        log.info("testLogEvent start");
        logEventProducer.simulate();
        log.info("testLogEvent end");
        // try {
        // // 这里停止2000ms是为了确定是处理消息是异步的
        // Thread.sleep(2000);
        // } catch (InterruptedException e) {
        // }
    }

}
