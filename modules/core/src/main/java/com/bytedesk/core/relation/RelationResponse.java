/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:36:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.relation;

import com.bytedesk.core.base.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RelationResponse extends BaseResponse {

    private String name;

    private String description;

    private String type;

    private String color;

    private Integer order;

    // ==================== 社交关系相关字段 ====================
    
    /**
     * 关系主体用户ID（发起关系的用户）
     */
    private String subjectUserUid;

    /**
     * 关系客体用户ID（被关系的用户）
     */
    private String objectUserUid;

    /**
     * 关系客体内容ID（被关系的内容，如文章、视频等）
     */
    private String objectContentUid;

    /**
     * 关系状态
     */
    private String status;

    /**
     * 关系强度/权重（0-100，用于算法推荐）
     */
    private Integer weight;

    /**
     * 关系标签列表
     */
    private List<String> tagList;

    /**
     * 关系分类
     */
    private String category;

    /**
     * 关系子类型（如：互相关注、单向关注等）
     */
    private String subtype;

    // ==================== 互动数据字段 ====================
    
    /**
     * 点赞数量
     */
    private Integer likeCount;

    /**
     * 收藏数量
     */
    private Integer favoriteCount;

    /**
     * 分享数量
     */
    private Integer shareCount;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 浏览数量
     */
    private Integer viewCount;

    /**
     * 关注数量
     */
    private Integer followCount;

    /**
     * 粉丝数量
     */
    private Integer fanCount;

    // ==================== 时间相关字段 ====================
    
    /**
     * 关系开始时间
     */
    private ZonedDateTime relationStartTime;

    /**
     * 关系结束时间
     */
    private ZonedDateTime relationEndTime;

    /**
     * 最后互动时间
     */
    private ZonedDateTime lastInteractionTime;

    // ==================== 设置字段 ====================
    
    /**
     * 是否置顶
     */
    private Boolean isPinned;

    /**
     * 是否公开（其他用户可见）
     */
    private Boolean isPublic;

    /**
     * 是否互相关注
     */
    private Boolean isMutual;

    /**
     * 是否特别关注
     */
    private Boolean isSpecial;

    /**
     * 是否免打扰
     */
    private Boolean isMuted;

    // ==================== 扩展字段 ====================
    
    /**
     * 关系备注
     */
    private String remark;

    /**
     * 关系来源（如：推荐、搜索、分享等）
     */
    private String source;

    /**
     * 关系额外信息（JSON格式）
     */
    private String extra;

    /**
     * 关系元数据（JSON格式，存储扩展信息）
     */
    private String metadata;

    // ==================== 统计字段 ====================
    
    /**
     * 互动频率（每月互动次数）
     */
    private Integer interactionFrequency;

    /**
     * 关系持续时间（天数）
     */
    private Integer relationDurationDays;

    /**
     * 关系质量评分（0-100）
     */
    private Integer relationQualityScore;
}
