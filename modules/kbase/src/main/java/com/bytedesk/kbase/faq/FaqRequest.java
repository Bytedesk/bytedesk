/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:25:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import com.bytedesk.core.utils.BdDateUtils;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class FaqRequest extends BaseRequest {

    private String question;

    @Builder.Default
    private List<String> similarQuestions = new ArrayList<>();

    private String answer;

    private String answerHtml;

    private String answerMarkdown;

    // 支持图片
    @Builder.Default
    private List<String> images = new ArrayList<>();

    // 支持附件
    @Builder.Default
    private List<String> attachments = new ArrayList<>();

    @Builder.Default
    private List<FaqAnswer> answerList = new ArrayList<>();

    @Builder.Default
    private List<String> relatedFaqUids = new ArrayList<>();

    @Builder.Default
    private String type = MessageTypeEnum.TEXT.name();

    @Builder.Default
    private String elasticStatus = FaqStatusEnum.NEW.name();

    @Builder.Default
    private String vectorStatus = ChunkStatusEnum.NEW.name();

    // 被展示次数
    @Builder.Default
    private Integer viewCount = 0;

    // 被点击次数
    @Builder.Default
    private Integer clickCount = 0;

    // 点赞次数
    @Builder.Default
    private Integer upCount = 0;

    // 点踩次数
    @Builder.Default
    private Integer downCount = 0;

     // 反馈次数
     @Builder.Default
     private Integer feedbackCount = 0;
 
     // 转人工次数
     @Builder.Default
     private Integer transferCount = 0;

    // 会员等级
	@Builder.Default
	private Integer vipLevel = 0;

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private List<String> tagList = new ArrayList<>();

    @Builder.Default
    private ZonedDateTime startDate = BdDateUtils.now();

    // 当前 + 100 年
    @Builder.Default
    private ZonedDateTime endDate = BdDateUtils.now().plusYears(100);

    private String categoryUid;

    private String kbUid; // 对应知识库

    private String fileUid; // 对应文件

    @Builder.Default
    private List<String> docIdList = new ArrayList<>();

    // 过滤掉前端拉取
    private Boolean onlyLoadValid; // 是否只加载有效的

    // 用于客户端点击uid
    private String threadUid; // 对应会话
    // rate message helpful/unhelpful
    private String messageUid; // 对应消息

    // 点踩的情况下的反馈意见
    @Builder.Default
    private List<String> rateDownTagList = new ArrayList<>();
    // 点踩的原因
    private String rateDownReason;
    // 点踩的用户
    private String user;

    // 
    // private String searchText;

    // used for client query
    private String componentType;
    
}
