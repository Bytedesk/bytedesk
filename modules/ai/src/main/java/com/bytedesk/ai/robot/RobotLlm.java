/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 10:02:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-24 22:39:03
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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Builder.Default
    @Column(name = "is_enabled")
    private boolean enabled = false;

    @Builder.Default
    private int topK = 3;

    @Builder.Default
    private Double scoreThreshold = 0.5;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private RobotModelEnum model = RobotModelEnum.ZHIPUAI_GLM_3_TURBO;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    // private String embeddings = "m3e-base";
    private RobotEmbedingEnum embeddings = RobotEmbedingEnum.M3E_BASE;

    @Builder.Default
    private float temperature = 0.9f;

    @Builder.Default
    private float topP = 0.7f;

    @Builder.Default
    private String prompt = I18Consts.I18N_ROBOT_LLM_PROMPT;

    // 默认是true，表示使用自定义模型, false表示云服务（需要在配置文件中配置相关参数）
    @Builder.Default
    @Column(name = "is_custom")
    private boolean custom = true;

    private String apiKey;

    private String apiSecret;

    private String apiUrl;

}