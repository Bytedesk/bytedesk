/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 10:02:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-26 17:27:17
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
    @Column(name = "is_enabled")
    private boolean enabled = false;

    @Builder.Default
    private int topK = 3;

    @Builder.Default
    private Double scoreThreshold = 0.5;

    @Builder.Default
    private String provider = "zhipu";
    
    @Builder.Default
    private String model = "glm-4-flash";
    
    @Builder.Default
    private String embedding = "m3e_base";

    @Builder.Default
    private float temperature = 0.9f;

    @Builder.Default
    private float topP = 0.7f;

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String prompt = I18Consts.I18N_ROBOT_LLM_PROMPT;
    // private String promptTemplate =
    // "请根据上下文信息回答问题：\n\n上下文信息：\n{context}\n\n问题：{question}\n\n答案：";

    // 上下文消息数，默认3条。一同传递给大模型
    @Builder.Default
    private int contextMsgCount = 3;

    // 等待时间，单位为秒。超过此等待时间大模型没有回复，则自动转接人工
    // @Builder.Default
    // private int waitSeconds = 10;

    // 默认是true，表示使用自定义模型, false表示云服务（需要在配置文件中配置相关参数）
    // @Builder.Default
    // @Column(name = "is_custom")
    // private boolean custom = true;

    // private String apiUrl;

    // private String apiKey;

    // private String apiSecret;

}