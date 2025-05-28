/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-24 13:33:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 16:01:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.kbase.chunk.ChunkElastic;
import com.bytedesk.kbase.chunk.ChunkVector;
import com.bytedesk.kbase.faq.elastic.FaqElastic;
import com.bytedesk.kbase.faq.vector.FaqVector;
import com.bytedesk.kbase.llm_text.TextElastic;
import com.bytedesk.kbase.llm_text.TextVector;

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
public class FaqProtobuf implements Serializable {

	private static final long serialVersionUID = 1L;

    private String uid;

    private String question;

    private String answer;

    private String type;

    @Builder.Default
    private List<FaqProtobuf> relatedFaqs = new ArrayList<>();

    public static FaqProtobuf fromJson(String faq) {
        return JSON.parseObject(faq, FaqProtobuf.class);
    }

    public static FaqProtobuf fromElastic(FaqElastic faq) {
        return FaqProtobuf.builder()
                .uid(faq.getUid())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .type(FaqProtobufTypeEnum.FAQ.name())
                .build();
    }

    public static FaqProtobuf fromFaqVector(FaqVector faq) {
        return FaqProtobuf.builder()
                .uid(faq.getUid())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .type(FaqProtobufTypeEnum.FAQ.name())
                .build();
    }

    public static FaqProtobuf fromText(TextElastic text) {
        return FaqProtobuf.builder()
                .uid(text.getUid())
                .question(text.getTitle())
                .answer(text.getContent())
                .type(FaqProtobufTypeEnum.TEXT.name())
                .build();
    }

    // from text vector
    public static FaqProtobuf fromTextVector(TextVector text) {
    	return FaqProtobuf.builder()
                .uid(text.getUid())
                .question(text.getTitle())
                .answer(text.getContent())
                .type(FaqProtobufTypeEnum.TEXT.name())
                .build();
    }

    public static FaqProtobuf fromChunk(ChunkElastic chunk) {
        return FaqProtobuf.builder()
               .uid(chunk.getUid())
               .question(chunk.getName())
               .answer(chunk.getContent())
               .type(FaqProtobufTypeEnum.CHUNK.name())
               .build();
    }

    // from chunk vector
    public static FaqProtobuf fromChunkVector(ChunkVector chunk) {
    	return FaqProtobuf.builder()
                .uid(chunk.getUid())
                .question(chunk.getName())
                .answer(chunk.getContent())
                .type(FaqProtobufTypeEnum.CHUNK.name())
                .build();
    }
    
    public String toJson() {
        return JSON.toJSONString(this);
    }
}
