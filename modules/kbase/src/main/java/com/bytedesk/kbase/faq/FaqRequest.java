/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 15:19:22
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
public class FaqRequest extends BaseRequest {

    private String question;

    private String answer;

    @Builder.Default
    private List<String> answerList = new ArrayList<>();

    @Builder.Default
    private List<String> relatedFaqUids = new ArrayList<>();

    private String type;

    // 被点击次数
    @Builder.Default
    private Integer clickCount = 0;

    // 点赞次数
    @Builder.Default
    private Integer upCount = 0;

    // 点踩次数
    @Builder.Default
    private Integer downCount = 0;

    // 是否有效
    // @Builder.Default
    // private Boolean valid = true;

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private String tags = "[]";

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String categoryUid;

    private String kbUid; // 对应知识库

    private String fileUid; // 对应文件

    private String docUid; // 对应文档

    // 是否是常见问题/
    @Builder.Default
    private Boolean isCommon = false;

    // 是否是快捷按钮
    @Builder.Default
    private Boolean isShortcut = false;

    // 是否是猜你相问
    @Builder.Default
    private Boolean isGuess = false;

    // 是否是热门问题
    @Builder.Default
    private Boolean isHot = false;

    // 是否是快捷路径
    @Builder.Default
    private Boolean isShortcutPath = false;
}
