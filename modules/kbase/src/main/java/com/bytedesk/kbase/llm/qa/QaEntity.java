/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-20 08:49:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.qa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.llm.split.SplitStatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * qa: Frequently Asked Questions
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true, exclude = { "relatedQas" })
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({QaEntityListener.class})
@Table(name = "bytedesk_kbase_llm_qa")
public class QaEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // 问题
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String question;

    // 同一个问题，支持多种问法
    // 支持AI生成，手动编辑
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> questionList = new ArrayList<>();

    // 默认答案（对所有用户级别展示的通用答案），等级为0时答案
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String answer;
    
    // 支持一问多答，根据不同VIP等级对应不同答案
    @Builder.Default
    @Convert(converter = QaAnswerListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<QaAnswer> answerList = new ArrayList<>();

    // 支持设置关联问题
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private List<QaEntity> relatedQas = new ArrayList<>();

    @Builder.Default
    @Column(name = "qa_type", nullable = false)
    private String type = MessageTypeEnum.TEXT.name();

    @Builder.Default
    private String status = SplitStatusEnum.NEW.name();
    /**
     * 标签列表
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    // 被展示次数
    @Builder.Default
    private int viewCount = 0;

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
    @Builder.Default
    private LocalDateTime startDate = LocalDateTime.now();

    // 有效结束日期
    // 当前 + 100 年
    @Builder.Default
    private LocalDateTime endDate = LocalDateTime.now().plusYears(100);

    // 分类
    private String categoryUid;

    // 对应知识库
    // private String kbUid;
    @ManyToOne(fetch = FetchType.LAZY)
    private KbaseEntity kbaseEntity;

    private String fileUid; // 对应文件
    
    // vector store id
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> docIdList = new ArrayList<>();

    public int increaseViewCount() {
        this.viewCount++;
        return this.viewCount;
    }

    public int increaseClickCount() {
        this.clickCount++;
        return this.clickCount;
    }

    public int increaseUpCount() {
        this.upCount++;
        return this.upCount;
    }

    public int increaseDownCount() {
        this.downCount++;
        return this.downCount;
    }

    // public void up() {
    //     this.setUpCount(this.upCount + 1);
    // }

    // public void down() {
    //     this.setDownCount(this.downCount + 1);
    // }

    /**
     * 获取指定VIP等级的答案，如果没有对应等级的答案，则返回默认答案
     * @param vipLevel 用户VIP等级
     * @return 根据VIP等级返回的答案
     */
    public String getAnswerForVipLevel(String vipLevel) {
        if (answerList != null && !answerList.isEmpty()) {
            // 先查找是否有完全匹配的VIP等级答案
            for (QaAnswer vipAnswer : answerList) {
                if (vipAnswer.getVipLevel().equalsIgnoreCase(vipLevel)) {
                    return vipAnswer.getAnswer();
                }
            }
            
            // 如果没有完全匹配的，返回默认答案answer
            return answer;

            // QaAnswer highestMatch = null;
            // for (QaAnswer vipAnswer : answerList) {
            //     if (vipAnswer.getVipLevel() < vipLevel && 
            //         (highestMatch == null || vipAnswer.getVipLevel() > highestMatch.getVipLevel())) {
            //         highestMatch = vipAnswer;
            //     }
            // }
            
            // if (highestMatch != null) {
            //     return highestMatch.getAnswer();
            // }
        }
        
        // 如果没有找到匹配的VIP答案，返回默认答案
        return answer;
    }

    /**
     * 添加标签
     * @param tag 要添加的标签
     */
    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty() && !tagList.contains(tag.trim())) {
            tagList.add(tag.trim());
        }
    }

    /**
     * 移除标签
     * @param tag 要移除的标签
     */
    public void removeTag(String tag) {
        if (tag != null) {
            tagList.remove(tag.trim());
        }
    }

    /**
     * 检查是否包含特定标签
     * @param tag 要检查的标签
     * @return 如果包含该标签则返回true
     */
    public boolean hasTag(String tag) {
        return tag != null && tagList.contains(tag.trim());
    }
}
