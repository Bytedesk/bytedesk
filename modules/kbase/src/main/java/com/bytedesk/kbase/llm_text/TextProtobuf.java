/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-24 13:33:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 19:01:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.kbase.llm_text.elastic.TextElastic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TextProtobuf implements Serializable {

	private static final long serialVersionUID = 1L;

    private String uid;

    private String title;

    private String content;

    @Builder.Default
    private List<TextProtobuf> relatedTexts = new ArrayList<>();

    public static TextProtobuf fromJson(String text) {
        return JSON.parseObject(text, TextProtobuf.class);
    }

    public static TextProtobuf fromElastic(TextElastic text) {
        return TextProtobuf.builder()
                .uid(text.getUid())
                .title(text.getTitle())
                .content(text.getContent())
                .build();
    }
    
    public String toJson() {
        return JSON.toJSONString(this);
    }
}
