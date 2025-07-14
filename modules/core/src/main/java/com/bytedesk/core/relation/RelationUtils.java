/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-14 09:50:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-14 09:50:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.relation;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 社交关系工具类
 * 提供社交关系相关的工具方法
 */
public class RelationUtils {

    /**
     * 生成关系名称
     * 
     * @param type 关系类型
     * @param subjectUserUid 主体用户ID
     * @param objectUserUid 客体用户ID
     * @return 关系名称
     */
    public static String generateRelationName(String type, String subjectUserUid, String objectUserUid) {
        if (type == null) {
            return "未知关系";
        }
        
        RelationTypeEnum relationType = RelationTypeEnum.fromValue(type);
        switch (relationType) {
            case FOLLOW:
                return "关注关系";
            case FAN:
                return "粉丝关系";
            case FRIEND:
                return "好友关系";
            case LIKE:
                return "点赞关系";
            case FAVORITE:
                return "收藏关系";
            case SHARE:
                return "分享关系";
            case COMMENT:
                return "评论关系";
            case MENTION:
                return "提及关系";
            case REPLY:
                return "回复关系";
            case QUOTE:
                return "引用关系";
            case REPOST:
                return "转发关系";
            default:
                return relationType.getChineseName();
        }
    }

    /**
     * 计算关系持续时间（天数）
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间（可为null，表示当前时间）
     * @return 持续天数
     */
    public static Integer calculateRelationDurationDays(ZonedDateTime startTime, ZonedDateTime endTime) {
        if (startTime == null) {
            return 0;
        }
        
        ZonedDateTime end = endTime != null ? endTime : ZonedDateTime.now();
        return (int) ChronoUnit.DAYS.between(startTime, end);
    }

    /**
     * 计算关系质量评分
     * 
     * @param likeCount 点赞数
     * @param favoriteCount 收藏数
     * @param shareCount 分享数
     * @param commentCount 评论数
     * @param interactionFrequency 互动频率
     * @return 质量评分（0-100）
     */
    public static Integer calculateRelationQualityScore(Integer likeCount, Integer favoriteCount, 
                                                       Integer shareCount, Integer commentCount, 
                                                       Integer interactionFrequency) {
        int score = 0;
        
        // 基础分数：互动数量
        if (likeCount != null && likeCount > 0) {
            score += Math.min(likeCount * 2, 20); // 最多20分
        }
        if (favoriteCount != null && favoriteCount > 0) {
            score += Math.min(favoriteCount * 3, 25); // 最多25分
        }
        if (shareCount != null && shareCount > 0) {
            score += Math.min(shareCount * 4, 20); // 最多20分
        }
        if (commentCount != null && commentCount > 0) {
            score += Math.min(commentCount * 2, 15); // 最多15分
        }
        
        // 互动频率分数
        if (interactionFrequency != null && interactionFrequency > 0) {
            score += Math.min(interactionFrequency, 20); // 最多20分
        }
        
        return Math.min(score, 100);
    }

    /**
     * 验证关系请求参数
     * 
     * @param type 关系类型
     * @param subjectUserUid 主体用户ID
     * @param objectUserUid 客体用户ID
     * @param objectContentUid 客体内容ID
     * @return 验证结果
     */
    public static boolean validateRelationRequest(String type, String subjectUserUid, 
                                                 String objectUserUid, String objectContentUid) {
        // 关系类型不能为空
        if (type == null || type.trim().isEmpty()) {
            return false;
        }
        
        // 主体用户ID不能为空
        if (subjectUserUid == null || subjectUserUid.trim().isEmpty()) {
            return false;
        }
        
        // 客体用户ID和客体内容ID至少有一个不为空
        if ((objectUserUid == null || objectUserUid.trim().isEmpty()) && 
            (objectContentUid == null || objectContentUid.trim().isEmpty())) {
            return false;
        }
        
        // 主体和客体不能是同一个用户
        if (subjectUserUid.equals(objectUserUid)) {
            return false;
        }
        
        return true;
    }

    /**
     * 获取关系类型的显示名称
     * 
     * @param type 关系类型
     * @return 显示名称
     */
    public static String getRelationTypeDisplayName(String type) {
        if (type == null) {
            return "未知";
        }
        
        try {
            RelationTypeEnum relationType = RelationTypeEnum.fromValue(type);
            return relationType.getChineseName();
        } catch (IllegalArgumentException e) {
            return "未知";
        }
    }

    /**
     * 获取关系状态的显示名称
     * 
     * @param status 关系状态
     * @return 显示名称
     */
    public static String getRelationStatusDisplayName(String status) {
        if (status == null) {
            return "未知";
        }
        
        try {
            RelationStatusEnum relationStatus = RelationStatusEnum.fromValue(status);
            return relationStatus.getChineseName();
        } catch (IllegalArgumentException e) {
            return "未知";
        }
    }

    /**
     * 判断关系类型是否为社交关系
     * 
     * @param type 关系类型
     * @return 是否为社交关系
     */
    public static boolean isSocialRelation(String type) {
        if (type == null) {
            return false;
        }
        
        try {
            RelationTypeEnum relationType = RelationTypeEnum.fromValue(type);
            return relationType.isSocialRelation();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 判断关系类型是否为内容关系
     * 
     * @param type 关系类型
     * @return 是否为内容关系
     */
    public static boolean isContentRelation(String type) {
        if (type == null) {
            return false;
        }
        
        try {
            RelationTypeEnum relationType = RelationTypeEnum.fromValue(type);
            return relationType.isContentRelation();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 判断关系状态是否有效
     * 
     * @param status 关系状态
     * @return 是否有效
     */
    public static boolean isRelationStatusValid(String status) {
        if (status == null) {
            return false;
        }
        
        try {
            RelationStatusEnum relationStatus = RelationStatusEnum.fromValue(status);
            return relationStatus.isValid();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 格式化互动数量显示
     * 
     * @param count 数量
     * @return 格式化后的字符串
     */
    public static String formatInteractionCount(Integer count) {
        if (count == null || count == 0) {
            return "0";
        }
        
        if (count < 1000) {
            return String.valueOf(count);
        } else if (count < 10000) {
            return String.format("%.1fK", count / 1000.0);
        } else if (count < 1000000) {
            return String.format("%.1fW", count / 10000.0);
        } else {
            return String.format("%.1fM", count / 1000000.0);
        }
    }

    /**
     * 生成关系描述
     * 
     * @param type 关系类型
     * @param subjectUserUid 主体用户ID
     * @param objectUserUid 客体用户ID
     * @param objectContentUid 客体内容ID
     * @return 关系描述
     */
    public static String generateRelationDescription(String type, String subjectUserUid, 
                                                    String objectUserUid, String objectContentUid) {
        if (type == null) {
            return "未知关系";
        }
        
        RelationTypeEnum relationType = RelationTypeEnum.fromValue(type);
        String description = relationType.getChineseName();
        
        if (objectUserUid != null && !objectUserUid.trim().isEmpty()) {
            description += " - 用户关系";
        } else if (objectContentUid != null && !objectContentUid.trim().isEmpty()) {
            description += " - 内容关系";
        }
        
        return description;
    }
} 