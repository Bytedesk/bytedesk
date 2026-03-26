/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 08:11:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-09 13:13:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseExtra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class ThreadExtra extends BaseExtra {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private Boolean showQuickButtons = false;

    @Builder.Default
    private List<WorkflowQuickButton> quickButtons = new ArrayList<>();

    private String workflowCurrentNodeId;

    private String workflowWaitingChoiceNodeId;

    private String workflowWaitingQuestionNodeId;

    private String workflowQuestionVariable;

    private String workflowQuestionAnswer;

    private String workflowSelectedOptionValue;

    @Builder.Default
    private Boolean workflowCompleted = false;

    public static ThreadExtra fromJson(String json) {
        ThreadExtra result = BaseExtra.fromJson(json, ThreadExtra.class);
        return result != null ? result : ThreadExtra.builder().build();
    }

    @Data
    @Builder
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkflowQuickButton implements Serializable {

        private static final long serialVersionUID = 1L;

        private String uid;

        private String title;

        private String type;

        private String content;

        @Builder.Default
        private Boolean enabled = true;
    }
    
}
