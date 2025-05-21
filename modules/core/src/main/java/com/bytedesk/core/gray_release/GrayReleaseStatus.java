package com.bytedesk.core.gray_release;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 灰度发布状态
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class GrayReleaseStatus {
    
    @Column(name = "gray_feature")
    private String feature;           // 功能代码

    @Column(name = "gray_enabled")
    private Boolean enabled;          // 是否启用

    @Column(name = "gray_percentage")
    private Integer percentage;           // 灰度比例

    @Column(name = "gray_status")
    private String status;            // 状态：pending/active/paused/completed

    @Column(name = "gray_active_users")
    private long activeUsers;         // 当前活跃用户数

    @Column(name = "gray_total_users")
    private long totalUsers;          // 总用户数

    @Column(name = "gray_success_rate")
    private double successRate;       // 成功率

    @Column(name = "gray_failure_rate")
    private double failureRate;       // 失败率

    @Column(name = "gray_start_time")
    private LocalDateTime startTime;  // 开始时间

    @Column(name = "gray_end_time")
    private LocalDateTime endTime;    // 结束时间

    // 状态常量
    public static final String STATUS_PENDING = "pending";     // 待发布
    public static final String STATUS_ACTIVE = "active";       // 发布中
    public static final String STATUS_PAUSED = "paused";       // 已暂停
    public static final String STATUS_COMPLETED = "completed"; // 已完成

    /**
     * 检查是否可以继续放量
     */
    public Boolean canIncreaseRollout() {
        return STATUS_ACTIVE.equals(status) && 
               percentage < 100 && 
               successRate >= 0.95;
    }

    /**
     * 检查是否需要暂停放量
     */
    public Boolean shouldPauseRollout() {
        return STATUS_ACTIVE.equals(status) && 
               failureRate >= 0.20;
    }

    /**
     * 获取覆盖率
     */
    public double getCoverageRate() {
        return totalUsers > 0 ? (double) activeUsers / totalUsers : 0.0;
    }
} 