package com.bytedesk.core.gray_release;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.BytedeskConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 灰度发布配置
 * 用于控制新功能的逐步放量
 */
@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class GrayReleaseConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // 功能开关
    @Builder.Default
    private boolean enableGrayRelease = false;  // 是否启用灰度发布

    // 功能列表
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String features = BytedeskConsts.EMPTY_JSON_STRING;  // 功能列表，JSON格式

    // 白名单用户
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String whitelistUsers = BytedeskConsts.EMPTY_JSON_STRING;  // 白名单用户列表，JSON格式

    // 灰度比例
    @Builder.Default
    private int grayReleasePercentage = 0;  // 灰度发布比例，0-100

    // 灰度时间
    private LocalDateTime startTime;  // 灰度开始时间
    private LocalDateTime endTime;    // 灰度结束时间

    // 灰度状态
    @Builder.Default
    private String status = "pending";  // 状态：pending/active/completed

    /**
     * 检查用户是否在灰度范围内
     * @param userUid 用户ID
     * @param feature 功能名称
     * @return 是否可以使用该功能
     */
    public boolean isUserInGrayRelease(String userUid, String feature) {
        // 1. 检查灰度发布是否启用
        if (!enableGrayRelease) {
            return true;  // 未启用灰度发布，所有用户都可以使用
        }

        // 2. 检查功能是否在灰度列表中
        FeatureConfig featureConfig = FeatureConfig.fromJson(features);
        if (!featureConfig.hasFeature(feature)) {
            return true;  // 功能不在灰度列表中，所有用户都可以使用
        }

        // 3. 检查用户是否在白名单中
        WhitelistConfig whitelistConfig = WhitelistConfig.fromJson(whitelistUsers);
        if (whitelistConfig.isUserInWhitelist(userUid)) {
            return true;  // 白名单用户可以使用
        }

        // 4. 检查灰度时间
        LocalDateTime now = LocalDateTime.now();
        if (startTime != null && now.isBefore(startTime)) {
            return false;  // 灰度未开始
        }
        if (endTime != null && now.isAfter(endTime)) {
            return true;   // 灰度已结束，所有用户都可以使用
        }

        // 5. 根据用户ID计算是否在灰度比例内
        return isUserInPercentage(userUid, grayReleasePercentage);
    }

    /**
     * 根据用户ID和灰度比例判断用户是否在灰度范围内
     */
    private boolean isUserInPercentage(String userUid, int percentage) {
        if (percentage >= 100) return true;
        if (percentage <= 0) return false;

        // 使用用户ID的哈希值来确保同一用户的结果一致
        int hash = Math.abs(userUid.hashCode());
        return hash % 100 < percentage;
    }

    @Data
    @NoArgsConstructor
    public static class FeatureConfig {
        private List<Feature> features = new ArrayList<>();

        public boolean hasFeature(String featureName) {
            return features.stream()
                .anyMatch(f -> f.getName().equals(featureName) && f.isEnabled());
        }

        public static FeatureConfig fromJson(String json) {
            try {
                return JSON.parseObject(json, FeatureConfig.class);
            } catch (Exception e) {
                return new FeatureConfig();
            }
        }

        @Data
        @NoArgsConstructor
        public static class Feature {
            private String name;
            private boolean enabled;
            private String description;
        }
    }

    @Data
    @NoArgsConstructor
    public static class WhitelistConfig {
        private List<String> userUids = new ArrayList<>();

        public boolean isUserInWhitelist(String userUid) {
            return userUids.contains(userUid);
        }

        public static WhitelistConfig fromJson(String json) {
            try {
                return JSON.parseObject(json, WhitelistConfig.class);
            } catch (Exception e) {
                return new WhitelistConfig();
            }
        }
    }
} 