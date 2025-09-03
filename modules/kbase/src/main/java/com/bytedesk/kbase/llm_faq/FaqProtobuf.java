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
package com.bytedesk.kbase.llm_faq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.kbase.article.elastic.ArticleElastic;
import com.bytedesk.kbase.article.vector.ArticleVector;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElastic;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVector;
import com.bytedesk.kbase.llm_faq.elastic.FaqElastic;
import com.bytedesk.kbase.llm_faq.vector.FaqVector;
import com.bytedesk.kbase.llm_text.elastic.TextElastic;
import com.bytedesk.kbase.llm_text.vector.TextVector;
import com.bytedesk.kbase.llm_webpage.elastic.WebpageElastic;
import com.bytedesk.kbase.llm_webpage.vector.WebpageVector;

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

    public static FaqProtobuf fromArticle(ArticleElastic article) {
        return FaqProtobuf.builder()
                .uid(article.getUid())
                .question(article.getTitle())
                .answer(article.getContentMarkdown() != null ? article.getContentMarkdown() : article.getSummary())
                .type(FaqProtobufTypeEnum.ARTICLE.name())
                .build();
    }

    // from article vector
    public static FaqProtobuf fromArticleVector(ArticleVector article) {
        return FaqProtobuf.builder()
                .uid(article.getUid())
                .question(article.getTitle())
                .answer(article.getContentMarkdown() != null ? article.getContentMarkdown() : article.getSummary())
                .type(FaqProtobufTypeEnum.ARTICLE.name())
                .build();
    }

    public static FaqProtobuf fromWebpage(WebpageElastic webpage) {
        return FaqProtobuf.builder()
                .uid(webpage.getUid())
                .question(webpage.getTitle())
                .answer(webpage.getContent())
                .type(FaqProtobufTypeEnum.WEBPAGE.name())
                .build();
    }

    // from webpage vector
    public static FaqProtobuf fromWebpageVector(WebpageVector webpage) {
        return FaqProtobuf.builder()
                .uid(webpage.getUid())
                .question(webpage.getTitle())
                .answer(webpage.getContent())
                .type(FaqProtobufTypeEnum.WEBPAGE.name())
                .build();
    }
    
    public String toJson() {
        return JSON.toJSONString(this);
    }
}
