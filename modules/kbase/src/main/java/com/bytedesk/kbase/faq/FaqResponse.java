/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 16:17:58
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
import java.util.List;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class FaqResponse extends BaseResponse {

    private String question;

    private String answer;

    private List<String> answerList;

    private List<FaqResponse> relatedFaqs;

    private String type;

    // private Integer viewCount;

    private Integer clickCount;

    private Integer upCount;

    private Integer downCount;

    private Boolean downShowTransferToAgentButton;

    // private Boolean valid;
    private Boolean enabled;

    private String tags;

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    private String categoryUid; // 分类

    private String kbUid; // 对应知识库

    private String fileUid; // 对应文件

    private String docUid; // 对应文档

    // 是否是常见问题/
    private Boolean isCommon;

    // 是否是快捷按钮
    private Boolean isShortcut;

    // 是否是猜你相问
    private Boolean isGuess;

    // 是否是热门问题
    private Boolean isHot;

    // 是否是快捷路径
    private Boolean isShortcutPath;

    // private String orgUid;

    // private LocalDateTime createdAt;

    // private LocalDateTime updatedAt;
}
