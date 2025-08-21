/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-21 08:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 08:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.netty.channel.Channel;

import static org.mockito.Mockito.*;

/**
 * 测试MQTT消息ID验证功能
 */
@ExtendWith(MockitoExtension.class)
public class MqttMessageIdValidationTest {

    @Mock
    private Channel mockChannel;

    @Test
    public void testSendPubAckMessageWithInvalidMessageId() {
        // 测试messageId为0的情况
        MqttChannelUtils.sendPubAckMessage(mockChannel, 0);
        
        // 验证没有调用writeAndFlush方法，因为messageId无效
        verify(mockChannel, never()).writeAndFlush(any());
    }

    @Test
    public void testSendPubAckMessageWithValidMessageId() {
        // 测试正常的messageId
        MqttChannelUtils.sendPubAckMessage(mockChannel, 100);
        
        // 验证调用了writeAndFlush方法
        verify(mockChannel, times(1)).writeAndFlush(any());
    }

    @Test
    public void testSendPubRecMessageWithInvalidMessageId() {
        // 测试messageId超出范围的情况
        MqttChannelUtils.sendPubRecMessage(mockChannel, 65536);
        
        // 验证没有调用writeAndFlush方法
        verify(mockChannel, never()).writeAndFlush(any());
    }

    @Test
    public void testSendPubRecMessageWithValidMessageId() {
        // 测试边界值
        MqttChannelUtils.sendPubRecMessage(mockChannel, 65535);
        
        // 验证调用了writeAndFlush方法
        verify(mockChannel, times(1)).writeAndFlush(any());
    }

    @Test
    public void testSendPubAckMessageWithNegativeMessageId() {
        // 测试负数messageId
        MqttChannelUtils.sendPubAckMessage(mockChannel, -1);
        
        // 验证没有调用writeAndFlush方法
        verify(mockChannel, never()).writeAndFlush(any());
    }
}
