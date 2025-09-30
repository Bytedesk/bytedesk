/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-14 17:09:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.relation;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Relation entity for social relationships and content categorization
 * Provides comprehensive relation functionality for various system entities
 * Reference: Xiaohongshu (Little Red Book) social platform design
 * 
 * Database Table: bytedesk_core_relation
 * Purpose: Stores relation definitions, social interactions, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({RelationEntityListener.class})
@Table(name = "bytedesk_core_relation")
public class RelationEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;



    /**
     * Type of relation (FOLLOW, LIKE, FAVORITE, etc.)
     */
    @Builder.Default
    @Column(name = "relation_type")
    private String type = RelationTypeEnum.CUSTOMER.name();

    // ==================== 社交关系相关字段 ====================
    
    /**
     * 关系主体用户ID（发起关系的用户）
     */
    @Column(name = "subject_user_uid")
    private String subjectUserUid;

    /**
     * 关系客体用户ID（被关系的用户）
     */
    @Column(name = "object_user_uid")
    private String objectUserUid;

    /**
     * 关系客体内容ID（被关系的内容，如文章、视频等）
     */
    @Column(name = "object_content_uid")
    private String objectContentUid;

    /**
     * 关系状态：ACTIVE(活跃)、INACTIVE(非活跃)、BLOCKED(屏蔽)、DELETED(删除)
     */
    @Builder.Default
    @Column(name = "relation_status")
    private String status = RelationStatusEnum.ACTIVE.name();

    /**
     * 关系强度/权重（0-100，用于算法推荐）
     */
    @Builder.Default
    @Column(name = "relation_weight")
    private Integer weight = 50;

    /**
     * 关系标签列表
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    /**
     * 关系分类
     */
    private String category;

    /**
     * 关系子类型（如：互相关注、单向关注等）
     */
    @Column(name = "relation_subtype")
    private String subtype;

    // ==================== 互动数据字段 ====================
    
    /**
     * 点赞数量
     */
    @Builder.Default
    @Column(name = "like_count")
    private Integer likeCount = 0;

    /**
     * 收藏数量
     */
    @Builder.Default
    @Column(name = "favorite_count")
    private Integer favoriteCount = 0;

    /**
     * 分享数量
     */
    @Builder.Default
    @Column(name = "share_count")
    private Integer shareCount = 0;

    /**
     * 评论数量
     */
    @Builder.Default
    @Column(name = "comment_count")
    private Integer commentCount = 0;

    /**
     * 浏览数量
     */
    @Builder.Default
    @Column(name = "view_count")
    private Integer viewCount = 0;

    /**
     * 关注数量
     */
    @Builder.Default
    @Column(name = "follow_count")
    private Integer followCount = 0;

    /**
     * 粉丝数量
     */
    @Builder.Default
    @Column(name = "fan_count")
    private Integer fanCount = 0;

    // ==================== 时间相关字段 ====================
    
    /**
     * 关系开始时间
     */
    @Column(name = "relation_start_time")
    private java.time.ZonedDateTime relationStartTime;

    /**
     * 关系结束时间
     */
    @Column(name = "relation_end_time")
    private java.time.ZonedDateTime relationEndTime;

    /**
     * 最后互动时间
     */
    @Column(name = "last_interaction_time")
    private java.time.ZonedDateTime lastInteractionTime;

    // ==================== 设置字段 ====================
    
    /**
     * 是否置顶
     */
    @Builder.Default
    @Column(name = "is_pinned")
    private Boolean isPinned = false;

    /**
     * 是否公开（其他用户可见）
     */
    @Builder.Default
    @Column(name = "is_public")
    private Boolean isPublic = true;

    /**
     * 是否互相关注
     */
    @Builder.Default
    @Column(name = "is_mutual")
    private Boolean isMutual = false;

    /**
     * 是否特别关注
     */
    @Builder.Default
    @Column(name = "is_special")
    private Boolean isSpecial = false;

    /**
     * 是否免打扰
     */
    @Builder.Default
    @Column(name = "is_muted")
    private Boolean isMuted = false;

    // ==================== 扩展字段 ====================
    
    /**
     * 关系备注
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String remark;

    /**
     * 关系来源（如：推荐、搜索、分享等）
     */
    @Column(name = "relation_source")
    private String source;

    /**
     * 关系额外信息（JSON格式）
     */
    @Builder.Default
    @Column(name = "relation_extra", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 关系元数据（JSON格式，存储扩展信息）
     */
    @Builder.Default
    @Column(name = "relation_metadata", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String metadata = BytedeskConsts.EMPTY_JSON_STRING;

    // ==================== 统计字段 ====================
    
    /**
     * 互动频率（每月互动次数）
     */
    @Builder.Default
    @Column(name = "interaction_frequency")
    private Integer interactionFrequency = 0;

    /**
     * 关系持续时间（天数）
     */
    @Builder.Default
    @Column(name = "relation_duration_days")
    private Integer relationDurationDays = 0;

    /**
     * 关系质量评分（0-100）
     */
    @Builder.Default
    @Column(name = "relation_quality_score")
    private Integer relationQualityScore = 50;
}
