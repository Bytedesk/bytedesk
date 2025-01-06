/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:51:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 16:05:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 敏感词
 * 一般来说，敏感词主要涉及以下几个方面：
 * 政治敏感词：涉及政治领袖、政治制度、政治事件等，如特定政治人物的名字、政治敏感事件等。
 * 民族宗教敏感词：涉及不同民族和宗教的词汇，如宗教信仰、民族风俗等。
 * 色情暴力敏感词：涉及色情和暴力的词汇，如色情描写、暴力行为等。
 * 商业广告敏感词：涉及夸大宣传、虚假宣传的词汇，如虚假广告、夸大效果等。
 * 个人隐私敏感词：涉及个人隐私的词汇，如个人信息、个人隐私等。
 * 社会道德敏感词：涉及社会道德的词汇，如不道德行为、社会丑闻等。
 * 法律法规敏感词：涉及法律法规的词汇，如违法行为、法律禁止等。
 * 环境生态敏感词：涉及环境生态的词汇，如环境污染、生态破坏等。
 * 
 * 在商业广告中，不得夸大宣传和虚假宣传的词汇通常包括：
 * “疗效保证”
 * “100%有效”
 * “无副作用”
 * “独家秘方”
 * “国际认证”
 * “最佳”
 * “第一”
 * “唯一”
 * “绝对”
 * “最先进”
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_kbase_taboo")
public class TabooEntity extends BaseEntity {

    private String content;

    private String categoryUid;

    private String kbUid; // 对应知识库
    
}
