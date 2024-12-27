/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 10:02:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-27 12:00:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RobotLlm {

    // 默认不起用llm问答，需要管理后台手动启动
    // 不起用llm的情况，默认使用关键词匹配
    @Builder.Default
    @Column(name = "is_llm_enabled")
    private boolean enabled = false;

    @Builder.Default
    @Column(name = "llm_top_k")
    private int topK = 3;

    @Builder.Default
    @Column(name = "llm_score_threshold")
    private Double scoreThreshold = 0.5;

    @Builder.Default
    @Column(name = "llm_provider")
    private String provider = "zhipu";
    
    @Builder.Default
    @Column(name = "llm_model")
    private String model = "glm-4-flash";
    
    @Builder.Default
    @Column(name = "llm_embedding")
    private String embedding = "m3e_base";

    @Builder.Default
    @Column(name = "llm_temperature")
    private float temperature = 0.9f;

    @Builder.Default
    @Column(name = "llm_top_p")
    private float topP = 0.7f;

    @Builder.Default
    @Column(name = "llm_prompt", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String prompt = I18Consts.I18N_ROBOT_LLM_PROMPT;
    // private String promptTemplate =
    // "请根据上下文信息回答问题：\n\n上下文信息：\n{context}\n\n问题：{question}\n\n答案：";

    // 上下文消息数，默认3条。一同传递给大模型
    @Builder.Default
    @Column(name = "llm_context_msg_count")
    private int contextMsgCount = 3;
}
