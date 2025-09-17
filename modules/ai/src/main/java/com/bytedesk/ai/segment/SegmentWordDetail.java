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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分词结果详细信息
 * @author jackning
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SegmentWordDetail {
    
    /**
     * 词语
     */
    private String word;
    
    /**
     * 起始位置
     */
    private Integer startIndex;
    
    /**
     * 结束位置
     */
    private Integer endIndex;
    
    /**
     * 词语长度
     */
    private Integer length;
    
    public SegmentWordDetail(String word, Integer startIndex, Integer endIndex) {
        this.word = word;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.length = endIndex - startIndex;
    }
    
}