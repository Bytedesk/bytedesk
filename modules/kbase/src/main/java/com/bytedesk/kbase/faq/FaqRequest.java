/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-28 17:56:53
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

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class FaqRequest extends BaseRequest {

    private String question;

    @Builder.Default
    private List<String> questionList = new ArrayList<>();

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
    private String status = FaqStatusEnum.NEW.name();

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

    // 当用户点踩的时候，是否显示转人工按钮
    // @Builder.Default
    // private Boolean downShowTransferToAgentButton = true;

    // 会员等级
	@Builder.Default
	private Integer vipLevel = 0;

    // 是否有效
    // @Builder.Default
    // private Boolean valid = true;
    @Builder.Default
    private Boolean enabled = true;

    // 是否开启自动同步到llm_qa问答
    // @Builder.Default
    // private Boolean autoSyncLlmQa = false;

    // // 是否已经同步llm问答
    // @Builder.Default
    // private Boolean llmQaSynced = false;

    // // 同步到llm qa kbUid 
    // private String llmQaKbUid;

    // // 同步到llm qa uid
    // private String llmQaUid;

    @Builder.Default
    private List<String> tagList = new ArrayList<>();

    @Builder.Default
    private LocalDateTime startDate = LocalDateTime.now();

    // 当前 + 100 年
    @Builder.Default
    private LocalDateTime endDate = LocalDateTime.now().plusYears(100);

    private String categoryUid;

    private String kbUid; // 对应知识库

    private String fileUid; // 对应文件

    @Builder.Default
    private List<String> docIdList = new ArrayList<>();


    // 用于客户端点击uid
    private String threadUid; // 对应会话
    // rate message helpful/unhelpful
    private String messageUid; // 对应消息
}
