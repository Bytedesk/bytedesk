/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-02-05 10:20:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-02-05 10:20:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member.mq;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberExcelImport;
import com.bytedesk.core.member.MemberRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberBatchMessageProcessor {

    public static final int MAX_RETRY_COUNT = 3;
    public static final long BASE_RETRY_DELAY = 2000L;

    private final MemberRestService memberRestService;

    public boolean processBatchImport(MemberBatchMessage batchMessage) {
        try {
            // 检查组织UID是否存在（必要参数）
            String orgUid = batchMessage.getOrgUid();
            if (orgUid == null || orgUid.isEmpty()) {
                log.error("批量导入缺少组织UID: 批次{}, 索引{}", 
                        batchMessage.getBatchUid(), batchMessage.getBatchIndex());
                return false;
            }

            // 解析Member Excel数据
            MemberExcelImport memberExcel = JSON.parseObject(batchMessage.getMemberExcelJson(), MemberExcelImport.class);
            if (memberExcel == null) {
                log.error("无法解析Member Excel数据: 批次{}, 索引{}", 
                         batchMessage.getBatchUid(), batchMessage.getBatchIndex());
                return false;
            }

            log.debug("准备导入成员: 批次{}, 索引{}, 姓名={}, 邮箱={}, 手机={}",
                    batchMessage.getBatchUid(), batchMessage.getBatchIndex(), 
                    memberExcel.getNickname(), 
                    (memberExcel.getEmail() != null ? memberExcel.getEmail() : "无"),
                    (memberExcel.getMobile() != null ? memberExcel.getMobile() : "无"));

            // 转换为Member实体
            MemberEntity member = null;
            try {
                member = memberRestService.convertExcelToMember(memberExcel, orgUid);
            } catch (Exception e) {
                // 捕获并记录详细错误信息
                log.error("转换Member实体失败: 批次{}, 索引{}, 错误类型={}, 详细信息={}", 
                        batchMessage.getBatchUid(), batchMessage.getBatchIndex(), 
                        e.getClass().getName(), e.getMessage(), e);
                return false;
            }

            if (member == null) {
                log.error("无法转换Member实体: 批次{}, 索引{}", 
                         batchMessage.getBatchUid(), batchMessage.getBatchIndex());
                return false;
            }

            // 保存Member（单个保存，避免批量冲突）
            MemberEntity savedMember = memberRestService.save(member);
            if (savedMember != null) {
                log.debug("Member创建成功: 批次{}, 索引{}, UID{}", 
                         batchMessage.getBatchUid(), batchMessage.getBatchIndex(), savedMember.getUid());
                return true;
            } else {
                log.error("Member保存失败: 批次{}, 索引{}", 
                         batchMessage.getBatchUid(), batchMessage.getBatchIndex());
                return false;
            }

        } catch (OptimisticLockingFailureException e) {
            // 乐观锁异常，这是我们要解决的主要问题
            log.warn("Member批量导入遇到乐观锁异常: 批次{}, 索引{}, 错误: {}", 
                    batchMessage.getBatchUid(), batchMessage.getBatchIndex(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Member批量导入处理失败: 批次{}, 索引{}, 错误: {}", 
                     batchMessage.getBatchUid(), batchMessage.getBatchIndex(), e.getMessage(), e);
            return false;
        }
    }

    public RetryDecision evaluateRetry(MemberBatchMessage batchMessage) {
        Integer retryCount = batchMessage.getRetryCount();
        int currentRetryCount = (retryCount != null) ? retryCount : 0;

        if (currentRetryCount < MAX_RETRY_COUNT) {
            long retryDelay = BASE_RETRY_DELAY * (long) Math.pow(2, currentRetryCount);
            retryDelay = Math.min(retryDelay, 30000);
            return new RetryDecision(true, retryDelay, currentRetryCount + 1);
        }

        return new RetryDecision(false, 0L, currentRetryCount);
    }

    public record RetryDecision(boolean shouldRetry, long delayMs, int nextRetryCount) {
    }
}