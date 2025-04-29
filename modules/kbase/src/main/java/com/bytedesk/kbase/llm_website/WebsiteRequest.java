/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 17:11:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_website;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteRequest extends BaseRequest {

    private String name;

    private String url;

    private String description;

    @Builder.Default
    private List<String> tagList = new ArrayList<>();
    
    // 是否启用，状态：启用/禁用
    @Builder.Default
    private Boolean enabled = true;

    // 是否开启自动生成enable_llm_qa问答
    @Builder.Default
    private Boolean autoGenerateLlmQa = false;

    // 是否已经生成llm问答
    @Builder.Default
    private Boolean llmQaGenerated = false;

    // 是否开启自动删除llm问答
    @Builder.Default
    private Boolean autoDeleteLlmQa = false;

    // 是否已经删除llm问答
    @Builder.Default
    private Boolean llmQaDeleted = false;

    // 是否开启自动llm split切块
    @Builder.Default
    private Boolean autoLlmSplit = false;

    // 是否已经自动llm split切块
    @Builder.Default
    private Boolean llmSplitted = false;

    // 是否开启自动删除llm split切块
    @Builder.Default
    private Boolean autoDeleteLlmSplit = false;

    // 是否已经删除llm split切块
    @Builder.Default
    private Boolean llmSplitDeleted = false;

    // 有效开始日期
    @Builder.Default
    private LocalDateTime startDate = LocalDateTime.now();

    // 有效结束日期
    // 当前 + 100 年
    @Builder.Default
    private LocalDateTime endDate = LocalDateTime.now().plusYears(100);

    private String categoryUid; // 所属分类

    private String kbUid; // 所属知识库

}
