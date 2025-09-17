/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-17 15:15:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-17 15:15:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.segment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 分词请求DTO
 * @author jackning
 */
@Data
public class SegmentRequest {
    
    /**
     * 待分词的文本
     */
    @NotBlank(message = "文本内容不能为空")
    private String text;
    
    /**
     * 分词类型：word（仅返回词语）、detail（返回详细信息）、count（返回词频统计）
     */
    private String type = "word";
    
    /**
     * 是否过滤标点符号
     */
    private Boolean filterPunctuation = false;
    
    /**
     * 最小词长度（过滤小于指定长度的词）
     */
    private Integer minWordLength = 1;
    
}