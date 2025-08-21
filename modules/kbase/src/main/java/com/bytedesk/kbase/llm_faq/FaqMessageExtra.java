/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-18 18:09:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-12 15:39:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_faq;

import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.MessageExtra;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FaqMessageExtra extends MessageExtra {

    private static final long serialVersionUID = 1L;
    // 
    private String faqUid;
    // 
    private List<String> images;
    // 
    private List<String> attachments;
    // 
    private List<FaqAnswer> answerList;
    // 
    private List<FaqResponseSimple> relatedFaqs;
    // 

    public static FaqMessageExtra fromJson(String json) {
        return JSON.parseObject(json, FaqMessageExtra.class);
    }
}
