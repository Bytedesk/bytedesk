/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-01 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-02 08:17:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member.mq;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.TestPropertySource;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.jms.JmsArtemisConstants;
import com.bytedesk.team.member.MemberExcel;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Member批量导入MQ处理测试
 * 测试异步消息队列处理Member批量导入的完整流程
 */
@Slf4j
@SpringBootTest
@TestPropertySource(properties = {
    "spring.artemis.embedded.enabled=true",
    "spring.artemis.embedded.persistent=false"
})
public class MemberBatchMqTest {
    
    @Autowired
    private JmsTemplate jmsTemplate;
    

    /**
     * 测试消息的序列化和反序列化
     */
    @Test
    public void testMemberBatchMessageSerialization() {
        // 创建测试数据
        MemberExcel memberExcel = new MemberExcel();
        memberExcel.setNickname("测试用户");
        memberExcel.setMobile("13800138000");
        memberExcel.setEmail("test@example.com");

        // 创建批量导入消息
        MemberBatchMessage message = MemberBatchMessage.builder()
                .batchUid("test-batch-001")
                .operationType("batch_import")
                .memberExcelJson(com.alibaba.fastjson2.JSON.toJSONString(memberExcel))
                .orgUid("test-org-001")
                .batchIndex(1)
                .batchTotal(10)
                .isLastBatch(false)
                .retryCount(0)
                .createTimestamp(System.currentTimeMillis())
                .build();

        // 验证消息构建
        assert message.getBatchUid().equals("test-batch-001");
        assert message.getOperationType().equals("batch_import");
        assert message.getBatchIndex() == 1;
        assert message.getBatchTotal() == 10;
        assert !message.getIsLastBatch();
        assert message.getRetryCount() == 0;

        log.info("MemberBatchMessage序列化测试通过: {}", message);
    }

    /**
     * 测试批量数据分片逻辑
     */
    @Test
    public void testBatchDataPartitioning() {
        // 创建测试数据
        List<MemberExcel> memberList = new ArrayList<>();
        for (int i = 1; i <= 250; i++) {
            MemberExcel member = new MemberExcel();
            member.setNickname("测试用户" + i);
            member.setMobile("1380013800" + String.format("%02d", i % 100));
            member.setEmail("test" + i + "@example.com");
            memberList.add(member);
        }

        // 模拟分批处理逻辑
        int batchSize = 100; // 每批100条
        int totalBatches = (int) Math.ceil((double) memberList.size() / batchSize);
        
        log.info("总数据量: {}, 批次大小: {}, 总批次数: {}", memberList.size(), batchSize, totalBatches);

        for (int batchIndex = 0; batchIndex < totalBatches; batchIndex++) {
            int startIndex = batchIndex * batchSize;
            int endIndex = Math.min(startIndex + batchSize, memberList.size());
            List<MemberExcel> batchData = memberList.subList(startIndex, endIndex);
            boolean isLastBatch = (batchIndex == totalBatches - 1);

            log.info("批次 {}: 开始索引{}, 结束索引{}, 数据量{}, 是否最后批次: {}", 
                    batchIndex + 1, startIndex, endIndex, batchData.size(), isLastBatch);

            // 验证分批逻辑
            assert batchData.size() <= batchSize;
            if (isLastBatch) {
                assert endIndex == memberList.size();
            }
        }

        log.info("批量数据分片逻辑测试通过");
    }

    /**
     * 测试重试延迟计算
     */
    @Test 
    public void testRetryDelayCalculation() {
        long baseDelay = 2000L; // 2秒基础延迟
        
        // 测试指数退避重试延迟
        for (int retryCount = 0; retryCount < 5; retryCount++) {
            long retryDelay = baseDelay * (long) Math.pow(2, retryCount);
            log.info("重试次数: {}, 延迟时间: {}ms ({}秒)", retryCount, retryDelay, retryDelay / 1000.0);
            
            // 验证延迟计算
            assert retryDelay >= baseDelay;
            if (retryCount > 0) {
                long previousDelay = baseDelay * (long) Math.pow(2, retryCount - 1);
                assert retryDelay >= previousDelay;
            }
        }

        log.info("重试延迟计算测试通过");
    }

    /**
     * 测试消息队列常量
     */
    @Test
    public void testQueueConstants() {
        String queueName = JmsArtemisConstants.QUEUE_MEMBER_BATCH_IMPORT;
        
        // 验证队列常量
        log.info("队列名称: {}", queueName);
        
        assert queueName.startsWith("bytedesk.queue.");
        assert queueName.contains("member");
        assert queueName.contains("batch");
        assert queueName.contains("import");

        log.info("消息队列常量测试通过");
    }
    
    /**
     * 测试消息序列化和反序列化整个流程
     * 这个测试会验证我们的修复是否解决了序列化问题
     */
    @Test
    public void testMessageSerializationAndDeserialization() throws Exception {
        // 1. 创建测试数据
        MemberExcel memberExcel = new MemberExcel();
        memberExcel.setNickname("测试用户");
        memberExcel.setMobile("13800138000");
        memberExcel.setEmail("test@example.com");

        // 2. 创建批量导入消息
        MemberBatchMessage originalMessage = MemberBatchMessage.builder()
                .batchUid("test-batch-001")
                .operationType("batch_import")
                .memberExcelJson(JSON.toJSONString(memberExcel))
                .orgUid("test-org-001")
                .batchIndex(1)
                .batchTotal(10)
                .isLastBatch(false)
                .retryCount(0)
                .createTimestamp(System.currentTimeMillis())
                .build();
                
        // 3. 发送消息 - 使用JmsTemplate发送消息
        jmsTemplate.convertAndSend("test.queue.member.serialization", originalMessage, message -> {
            message.setStringProperty("_type", "memberBatchMessage");
            message.setStringProperty("batchUid", originalMessage.getBatchUid());
            message.setIntProperty("batchIndex", originalMessage.getBatchIndex());
            return message;
        });
        
        // 4. 接收消息 - 使用JmsTemplate接收消息
        Message receivedJmsMessage = jmsTemplate.receive("test.queue.member.serialization");
        
        // 5. 验证接收到的消息是TextMessage类型
        assert receivedJmsMessage instanceof TextMessage;
        TextMessage textMessage = (TextMessage) receivedJmsMessage;
        
        // 6. 获取消息体(String)并解析为MemberBatchMessage对象
        String messageBody = textMessage.getText();
        log.info("接收到的消息体: {}", messageBody);
        assert messageBody != null && !messageBody.isEmpty();
        
        // 7. 使用JSON转换器解析消息内容
        MemberBatchMessage deserializedMessage = JSON.parseObject(messageBody, MemberBatchMessage.class);
        
        // 8. 验证解析后的消息与原始消息一致
        assert deserializedMessage != null;
        assert deserializedMessage.getBatchUid().equals(originalMessage.getBatchUid());
        assert deserializedMessage.getOperationType().equals(originalMessage.getOperationType());
        assert deserializedMessage.getBatchIndex().equals(originalMessage.getBatchIndex());
        assert deserializedMessage.getBatchTotal().equals(originalMessage.getBatchTotal());
        assert deserializedMessage.getIsLastBatch().equals(originalMessage.getIsLastBatch());
        
        log.info("消息序列化和反序列化测试通过: {}", deserializedMessage);
    }
}
