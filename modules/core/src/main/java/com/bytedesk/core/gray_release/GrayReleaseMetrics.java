package com.bytedesk.core.gray_release;

import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 灰度发布指标实体
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bytedesk_gray_release_metrics")
@EqualsAndHashCode(callSuper = true)
public class GrayReleaseMetrics extends BaseEntity {

    @Column(nullable = false)
    private String userUid;      // 用户ID

    @Column(nullable = false)
    private String feature;      // 功能代码

    @Column(nullable = false)
    private boolean success;     // 是否成功

    @Column(nullable = false)
    private LocalDateTime timestamp;  // 记录时间

    @Column(length = 512)
    private String errorMessage;  // 错误信息（如果有）
} 