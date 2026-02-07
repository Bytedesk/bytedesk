/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-01 15:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 09:55:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member.mq;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.mq.MqSendOptions;
import com.bytedesk.core.mq.MqSender;
import com.bytedesk.core.mq.jms.JmsArtemisConsts;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.member.MemberExcelImport;

import lombok.extern.slf4j.Slf4j;

/**
 * Member批量导入消息服务
 * 参考FAQ的异步处理模式，用于发送Member批量导入消息到消息队列
 */
@Slf4j
@Service
public class MemberBatchMessageService {

    @Autowired
    private MqSender mqSender;

    /**
     * 发送批量导入消息
     * 将Member列表分批发送到消息队列，避免大批量数据的并发冲突
     * 
     * @param memberExcelList Member Excel数据列表
     * @param orgUid 组织唯一标识
     */
    public void sendBatchImportMessages(List<MemberExcelImport> memberExcelList, String orgUid) {
        if (memberExcelList == null || memberExcelList.isEmpty()) {
            log.warn("Member列表为空，无需发送批量导入消息");
            return;
        }
        
        // 安全检查 - 确保orgUid不为空
        if (orgUid == null || orgUid.isEmpty()) {
            log.error("发送批量导入消息失败：组织UID为空");
            return;
        }

        String batchUid = UidUtils.getInstance().getUid();
        int total = memberExcelList.size();
        AtomicInteger index = new AtomicInteger(0);

        log.info("开始发送Member批量导入消息，批次ID: {}, 总数: {}", batchUid, total);

        // 分批发送消息，每个Member一个消息，避免大批量并发
        memberExcelList.forEach(memberExcel -> {
            int currentIndex = index.incrementAndGet();
            boolean isLastBatch = currentIndex == total;

            try {
                // 创建批量导入消息
                MemberBatchMessage message = MemberBatchMessage.builder()
                        .batchUid(batchUid)
                        .operationType("batch_import")
                        .memberExcelJson(JSON.toJSONString(memberExcel))
                        .orgUid(orgUid)
                        .batchIndex(currentIndex)
                        .batchTotal(total)
                        .isLastBatch(isLastBatch)
                        .retryCount(0)
                        .createTimestamp(System.currentTimeMillis())
                        .build();

                // 每个消息间隔50-200ms，错峰处理
                long randomDelay = (currentIndex * 100L) + new Random().nextInt(150);

                MqSendOptions options = MqSendOptions.builder()
                        .delayMs(randomDelay)
                        .priority(5)
                        .headers(buildHeaders(batchUid, orgUid, currentIndex, total, isLastBatch, 0, "member-batch-" + batchUid))
                        .build();

                mqSender.send(JmsArtemisConsts.QUEUE_MEMBER_BATCH_IMPORT, message, options);

                log.debug("Member批量导入消息已发送: 批次{}, 索引{}/{}", 
                         batchUid, currentIndex, total);

            } catch (Exception e) {
                log.error("发送Member批量导入消息失败: 批次{}, 索引{}/{}, 错误: {}", 
                         batchUid, currentIndex, total, e.getMessage(), e);
            }
        });

        log.info("Member批量导入消息发送完成，批次ID: {}, 总数: {}", batchUid, total);
    }

    /**
     * 发送重试消息
     * 当Member创建失败时，重新发送到队列进行重试
     * 
     * @param originalMessage 原始消息
     * @param retryDelay 重试延迟（毫秒）
     */
    public void sendRetryMessage(MemberBatchMessage originalMessage, long retryDelay) {
        try {
            // 安全检查 - 确保关键字段不为null
            if (originalMessage.getBatchUid() == null) {
                log.error("重试消息缺少批次UID，无法发送");
                return;
            }
            
            if (originalMessage.getOrgUid() == null) {
                log.error("重试消息缺少组织UID，无法发送: 批次{}, 索引{}", 
                        originalMessage.getBatchUid(), originalMessage.getBatchIndex());
                return;
            }
            
            // 增加重试次数
            Integer retryCount = originalMessage.getRetryCount();
            originalMessage.setRetryCount(retryCount != null ? retryCount + 1 : 1);

            MqSendOptions options = MqSendOptions.builder()
                    .delayMs(retryDelay)
                    .priority(3)
                    .headers(buildHeaders(
                            originalMessage.getBatchUid(),
                            originalMessage.getOrgUid(),
                            originalMessage.getBatchIndex(),
                            originalMessage.getBatchTotal(),
                            originalMessage.getIsLastBatch(),
                            originalMessage.getRetryCount(),
                            "member-batch-retry-" + originalMessage.getBatchUid()))
                    .build();

            mqSender.send(JmsArtemisConsts.QUEUE_MEMBER_BATCH_IMPORT, originalMessage, options);

            log.info("Member批量导入重试消息已发送: 批次{}, 索引{}, 重试次数{}, 延迟{}ms",
                    originalMessage.getBatchUid(), originalMessage.getBatchIndex(), 
                    originalMessage.getRetryCount(), retryDelay);

        } catch (Exception e) {
            log.error("发送Member批量导入重试消息失败: 批次{}, 索引{}, 错误: {}",
                     originalMessage.getBatchUid(), originalMessage.getBatchIndex(), e.getMessage(), e);
        }
    }

    private Map<String, Object> buildHeaders(String batchUid, String orgUid, Integer batchIndex, Integer batchTotal,
                                             Boolean isLastBatch, Integer deliveryCount, String groupId) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("_type", "memberBatchMessage");
        headers.put("JMSXDeliveryCount", deliveryCount != null ? deliveryCount : 0);
        headers.put("JMSXGroupID", groupId);
        if (batchUid != null) {
            headers.put("batchUid", batchUid);
        }
        if (orgUid != null) {
            headers.put("orgUid", orgUid);
        }
        if (batchIndex != null) {
            headers.put("batchIndex", batchIndex);
        }
        if (batchTotal != null) {
            headers.put("batchTotal", batchTotal);
        }
        if (isLastBatch != null) {
            headers.put("isLastBatch", isLastBatch);
        }
        return headers;
    }
}
