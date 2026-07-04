/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:36:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_embedding;


import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class LlmEmbeddingResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private String type;

    /** 向量化来源类型：FAQ / CHUNK / TEXT / WEBPAGE */
    private String sourceType;

    /** 来源实体 UID */
    private String sourceUid;

    /** 向量化提供商 */
    private String provider;

    /** 向量化模型 */
    private String model;

    /** 向量化维度 */
    private Integer dimensions;

    /** 向量化内容摘要 */
    private String content;

    /** 向量化状态 */
    private String status;

    /** 错误信息 */
    private String errorMessage;

    /** 耗时（毫秒） */
    private Long costMs;

    /** 创建时间 */
    // private String createdAt;
}
