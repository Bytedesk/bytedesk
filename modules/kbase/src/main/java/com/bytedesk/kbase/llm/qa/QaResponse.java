/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-17 20:07:31
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
import java.util.List;

import com.bytedesk.core.base.BaseResponse;

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
public class QaResponse extends BaseResponse {

    private String question;

    private String answer;

    private List<QaAnswer> answerList;

    // 修改这里，使用简化版的响应对象避免循环依赖
    private List<SimpleQaResponse> relatedQas;

    // 是否是llm问答
    private Boolean isLlmQa;

    private String type;

    private String status;

    private Integer viewCount;

    private Integer clickCount;

    private Integer upCount;

    private Integer downCount;

    // private Boolean downShowTransferToAgentButton;

    // private Boolean valid;
    private Boolean enabled;

    private List<String> tagList;

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    private String categoryUid; // 分类

    private String kbUid; // 对应知识库

    private String fileUid; // 对应文件

    private String docUid; // 对应文档

    /**
     * 简化版FAQ响应，用于相关问题展示，避免循环依赖
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleQaResponse {
        private String uid;
        private String question;
        private String answer;
        private String type;
        private String status;
    }

}
