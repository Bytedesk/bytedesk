/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-19 22:59:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-06 18:07:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.statistic;

import java.math.BigDecimal;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ai_statistic")
public class StatisticRobotEntity extends BaseEntity {

    // 基础信息
    @Column(nullable = false)
    private String robotUid;  // 机器人ID
    
    @Column(nullable = false)
    private String userUid;   // 用户ID

    @Column(nullable = false)
    private String modelType; // 模型类型：gpt-4/gpt-3.5/claude/gemini等

    // Token统计
    @Builder.Default
    @Column(nullable = false)
    private Long promptTokens = 0L;    // 输入消耗的tokens

    @Builder.Default
    @Column(nullable = false)
    private Long completionTokens = 0L; // 输出消耗的tokens

    @Builder.Default
    @Column(nullable = false)
    private Long totalTokens = 0L;      // 总消耗tokens

    // 请求统计
    @Builder.Default
    @Column(nullable = false)
    private Long totalRequests = 0L;    // 总请求次数

    @Builder.Default
    @Column(nullable = false)
    private Long successRequests = 0L;  // 成功请求次数

    @Builder.Default
    @Column(nullable = false)
    private Long failedRequests = 0L;   // 失败请求次数

    // 费用统计 (使用BigDecimal确保精确计算)
    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal tokenUnitPrice = BigDecimal.ZERO;  // token单价（美元）

    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal totalCost = BigDecimal.ZERO;       // 总费用（美元）

    // 统计时间维度
    @Builder.Default
    @Column(nullable = false)
    private int hour = 0;  // 小时维度统计
    
    @Column(nullable = false)
    private String date;   // 日期维度统计 (YYYY-MM-DD)

    // 辅助字段
    @Builder.Default
    @Column(nullable = false)
    private Long avgResponseTime = 0L;  // 平均响应时间(ms)

    @Builder.Default
    @Column(nullable = false, length = 32)
    private String status = "active";   // 统计状态：active/archived

    // 更新统计信息的方法
    public void incrementTokens(long promptTokens, long completionTokens) {
        this.promptTokens += promptTokens;
        this.completionTokens += completionTokens;
        this.totalTokens = this.promptTokens + this.completionTokens;
        // 更新费用
        this.totalCost = this.tokenUnitPrice.multiply(BigDecimal.valueOf(this.totalTokens));
    }

    public void recordRequest(boolean success, long responseTime) {
        this.totalRequests++;
        if (success) {
            this.successRequests++;
        } else {
            this.failedRequests++;
        }
        // 更新平均响应时间
        this.avgResponseTime = ((this.avgResponseTime * (this.totalRequests - 1)) + responseTime) / this.totalRequests;
    }
}
