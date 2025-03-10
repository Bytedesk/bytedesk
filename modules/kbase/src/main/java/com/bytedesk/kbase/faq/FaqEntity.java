/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 12:13:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.utils.StringListConverter;
import com.bytedesk.kbase.split.SplitStatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * faq: Frequently Asked Questions
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({FaqEntityListener.class})
@Table(name = "bytedesk_kbase_faq")
public class FaqEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 问题
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String question;

    /**
     * 答案
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String answer;

    // 支持一问多答，根据访客不同身份，显示不同答案
    @Builder.Default
    @Convert(converter = FaqListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> answerList = new ArrayList<>();

    // 支持设置关联问题
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private List<FaqEntity> relatedFaqs = new ArrayList<>();


    @Builder.Default
    @Column(name = "faq_type", nullable = false)
    private String type = MessageTypeEnum.TEXT.name();

    @Builder.Default
    private String status = SplitStatusEnum.NEW.name();

    @Builder.Default
    private String level = LevelEnum.ORGANIZATION.name();

    @Builder.Default
    private String platform = PlatformEnum.BYTEDESK.name();

    /**
     * 标签，多个标签用逗号分隔
     */
    @Builder.Default
    private String tags = "[]";

    // 被浏览次数
    // @Builder.Default
    // private int viewCount = 0;

    // 被点击次数
    @Builder.Default
    private int clickCount = 0;

    // 点赞次数
    @Builder.Default
    private int upCount = 0;

    // 点踩次数
    @Builder.Default
    private int downCount = 0;

    // 是否启用，状态：启用/禁用
    @Builder.Default
    @Column(name = "is_enabled")
    private boolean enabled = true;

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    // 分类
    private String categoryUid;

    // 对应知识库
    private String kbUid;

    // used for auto-generate faq
    private String docId; // 对应文档

    private String fileUid; // 对应文件

    private String userUid;

    // 是否是常见问题/
    @Builder.Default
    private boolean isCommon = false;

    // 是否是快捷按钮
    @Builder.Default
    private boolean isShortcut = false;

    // 是否是猜你相问
    @Builder.Default
    private boolean isGuess = false;

    // 是否是热门问题
    @Builder.Default
    private boolean isHot = false;

    // 是否是快捷路径
    @Builder.Default
    private boolean isShortcutPath = false;

    // vector store id
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> docIdList = new ArrayList<>();

    public void up() {
        this.setUpCount(this.upCount + 1);
    }

    public void down() {
        this.setDownCount(this.downCount + 1);
    }

    /**
     * 排序权重
     */
    @Builder.Default
    private int weight = 0;

    /**
     * 点击次数
     */
    @Builder.Default
    private int hits = 0;
}
