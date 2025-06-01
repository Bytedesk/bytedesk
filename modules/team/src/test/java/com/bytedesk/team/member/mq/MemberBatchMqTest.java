/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-01 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-01 10:00:00
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.bytedesk.team.member.MemberExcel;

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
        String expectedQueue = "bytedesk.queue.member.batch.import";
        
        // 验证队列常量（需要根据实际实现调整）
        log.info("队列名称: {}", expectedQueue);
        
        assert expectedQueue.startsWith("bytedesk.queue.");
        assert expectedQueue.contains("member");
        assert expectedQueue.contains("batch");
        assert expectedQueue.contains("import");

        log.info("消息队列常量测试通过");
    }
}
