package com.bytedesk.core.gray_release;

import java.time.ZonedDateTime;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 灰度发布配置
 * 用于控制新功能的逐步放量
 */
@Slf4j
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bytedesk_core_gray_release")
public class GrayReleaseEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // 功能开关
    @Builder.Default
    @Column(name = "gray_release_enabled")
    private Boolean enableGrayRelease = false; // 是否启用灰度发布

    // 功能列表 - JSON格式存储
    @Builder.Default
    @Column(name = "gray_release_features", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String features = "[]"; // 功能列表，JSON格式

    // 白名单用户 - JSON格式存储
    @Builder.Default
    @Column(name = "gray_release_whitelist", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String whitelistUsers = "[]"; // 白名单用户列表，JSON格式

    // 灰度比例
    @Builder.Default
    @Column(name = "gray_release_percentage")
    private Integer grayReleasePercentage = 0; // 灰度发布比例，0-100

    // 灰度时间
    @Column(name = "gray_release_start_time")
    private ZonedDateTime startTime; // 灰度开始时间

    @Column(name = "gray_release_end_time")
    private ZonedDateTime endTime; // 灰度结束时间

    // 灰度状态
    @Builder.Default
    @Column(name = "gray_release_status", length = 32)
    private String status = "pending"; // 状态：pending/active/completed

    /**
     * 检查用户是否在灰度范围内
     * 
     * @param userUid 用户ID
     * @param feature 功能名称
     * @return 是否可以使用该功能
     */
    public Boolean isUserInGrayRelease(String userUid, String feature) {
        // 1. 检查灰度发布是否启用
        if (!enableGrayRelease) {
            return true; // 未启用灰度发布，所有用户都可以使用
        }

        // 2. 检查功能是否在灰度列表中
        if (!hasFeature(feature)) {
            return true; // 功能不在灰度列表中，所有用户都可以使用
        }

        // 3. 检查用户是否在白名单中
        if (isUserInWhitelist(userUid)) {
            return true; // 白名单用户可以使用
        }

        // 4. 检查灰度时间
        ZonedDateTime now = ZonedDateTime.now();
        if (startTime != null && now.isBefore(startTime)) {
            return false; // 灰度未开始
        }
        if (endTime != null && now.isAfter(endTime)) {
            return true; // 灰度已结束，所有用户都可以使用
        }

        // 5. 根据用户ID计算是否在灰度比例内
        return isUserInPercentage(userUid);
    }

    /**
     * 检查功能是否在灰度列表中
     */
    private Boolean hasFeature(String feature) {
        List<Feature> featureList = JSON.parseArray(features, Feature.class);
        return featureList.stream()
                .anyMatch(f -> f.getName().equals(feature) && f.getEnabled());
    }

    /**
     * 检查用户是否在白名单中
     */
    private Boolean isUserInWhitelist(String userUid) {
        List<String> whitelist = JSON.parseArray(whitelistUsers, String.class);
        return whitelist.contains(userUid);
    }

    /**
     * 根据用户ID判断是否在灰度比例内
     */
    private Boolean isUserInPercentage(String userUid) {
        if (grayReleasePercentage >= 100)
            return true;
        if (grayReleasePercentage <= 0)
            return false;

        int hash = Math.abs(userUid.hashCode());
        return hash % 100 < grayReleasePercentage;
    }

    /**
     * 添加功能到灰度列表
     */
    public void addFeature(String name, String description) {
        List<Feature> featureList = JSON.parseArray(features, Feature.class);
        Feature feature = new Feature(name, true, description);
        featureList.add(feature);
        this.features = JSON.toJSONString(featureList);
    }

    /**
     * 添加用户到白名单
     */
    public void addToWhitelist(String userUid) {
        List<String> whitelist = JSON.parseArray(whitelistUsers, String.class);
        new TypeReference<List<String>>() {
        };

        if (!whitelist.contains(userUid)) {
            whitelist.add(userUid);
            this.whitelistUsers = JSON.toJSONString(whitelist);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Feature {
        private String name;
        private Boolean enabled;
        private String description;
    }
}