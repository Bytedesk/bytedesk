/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:16:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 13:47:01
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

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.kbase.kbase.KbaseEntity;

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
 * faq: Frequently Asked Questions
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true, exclude = { "relatedFaqs" })
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({FaqEntityListener.class})
@Table(name = "bytedesk_kbase_faq")
public class FaqEntity extends BaseEntity {

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

    // 答案格式类型
    // 暂时忽略掉，因为答案里面可以同时包含：文本、图片、附件等
    @Builder.Default
    @Column(name = "faq_type", nullable = false)
    private String type = MessageTypeEnum.TEXT.name();

    // 默认答案（对所有用户级别展示的通用答案），等级为0时答案
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String answer;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String answerHtml;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String answerMarkdown;

    // 支持图片
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> images = new ArrayList<>();

    // 附件
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> attachments = new ArrayList<>();
    
    // 支持一问多答，根据不同VIP等级对应不同答案
    @Builder.Default
    @Convert(converter = FaqAnswerListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<FaqAnswer> answerList = new ArrayList<>();

    // 支持设置关联问题
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private List<FaqEntity> relatedFaqs = new ArrayList<>();

    @Builder.Default
    private String status = FaqStatusEnum.NEW.name();
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

    // 是否开启自动同步到llm_qa问答
    // @Builder.Default
    // @Column(name = "is_auto_sync_llm_qa")
    // private boolean autoSyncLlmQa = false;

    // // 是否已经同步llm问答
    // @Builder.Default
    // @Column(name = "is_llm_qa_synced")
    // private boolean llmQaSynced = false;

    // // 同步到llm qa kbUid 
    // @Column(name = "llm_kb_uid")
    // private String llmQaKbUid;

    // // 同步到llm qa uid
    // @Column(name = "llm_qa_uid")
    // private String llmQaUid;

    // 有效开始日期
    @Builder.Default
    private LocalDateTime startDate = LocalDateTime.now();

    // 有效结束日期
    // 当前 + 100 年
    @Builder.Default
    private LocalDateTime endDate = LocalDateTime.now().plusYears(100);

    // 分类
    private String categoryUid;

    // 替换kbUid为KbaseEntity
    @ManyToOne(fetch = FetchType.LAZY)
    private KbaseEntity kbase;

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

    // set Success
    public FaqEntity setSuccess() {
        this.setStatus(FaqStatusEnum.SUCCESS.name());
        return this;
    }

    // set Error
    public FaqEntity setError() {
        this.setStatus(FaqStatusEnum.ERROR.name());
        return this;
    }

    /**
     * 获取指定VIP等级的答案，如果没有对应等级的答案，则返回默认答案
     * @param vipLevel 用户VIP等级
     * @return 根据VIP等级返回的答案
     */
    public String getAnswerForVipLevel(String vipLevel) {
        if (answerList != null && !answerList.isEmpty()) {
            // 先查找是否有完全匹配的VIP等级答案
            for (FaqAnswer vipAnswer : answerList) {
                if (vipAnswer.getVipLevel().equalsIgnoreCase(vipLevel)) {
                    return vipAnswer.getAnswer();
                }
            }
            
            // 如果没有完全匹配的，返回默认答案answer
            return answer;

            // FaqAnswer highestMatch = null;
            // for (FaqAnswer vipAnswer : answerList) {
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

    // toJson
    public String toJson() {
        return JSON.toJSONString(this);
    }

    /**
     * 判断问题和答案是否有变化
     * @param request QaRequest 请求
     * @return 如果问题或答案有变化返回 true，否则返回 false
     */
    public boolean hasChanged(FaqRequest request) {
        // 比较问题是否变化
        if ((question == null && request.getQuestion() != null) ||
            (question != null && !question.equals(request.getQuestion()))) {
            return true;
        }
        
        // 比较答案是否变化
        if ((answer == null && request.getAnswer() != null) ||
            (answer != null && !answer.equals(request.getAnswer()))) {
            return true;
        }
        
        // 比较问题列表是否变化
        if ((questionList == null && request.getQuestionList() != null && !request.getQuestionList().isEmpty()) ||
            (questionList != null && request.getQuestionList() == null) ||
            (questionList != null && request.getQuestionList() != null && !questionList.equals(request.getQuestionList()))) {
            return true;
        }
        
        // 比较答案列表是否变化
        if ((answerList == null && request.getAnswerList() != null && !request.getAnswerList().isEmpty()) ||
            (answerList != null && request.getAnswerList() == null) ||
            (answerList != null && request.getAnswerList() != null && !answerList.equals(request.getAnswerList()))) {
            return true;
        }
        
        // 比较标签列表是否变化
        if ((tagList == null && request.getTagList() != null && !request.getTagList().isEmpty()) ||
            (tagList != null && request.getTagList() == null) ||
            (tagList != null && request.getTagList() != null && !tagList.equals(request.getTagList()))) {
            return true;
        }

        // enabled
        if (enabled != request.getEnabled()) {
            return true;
        }

        // StartDate
        if (startDate!= null && request.getStartDate()!= null &&!startDate.equals(request.getStartDate())) {
            return true;
        }
        
        // EndDate
        if (endDate!= null && request.getEndDate()!= null &&!endDate.equals(request.getEndDate())) {
            return true;
        }

        // 所有字段都没有变化
        return false;
    }

}
