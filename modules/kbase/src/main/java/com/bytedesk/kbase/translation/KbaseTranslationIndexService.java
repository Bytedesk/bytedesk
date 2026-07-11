/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-10 16:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 16:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.kbase.translation;

import java.util.Map;
import java.util.LinkedHashMap;

import org.springframework.stereotype.Service;

import com.bytedesk.kbase.llm_chunk.ChunkEntity;
import com.bytedesk.kbase.llm_chunk.ChunkRestService;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElasticService;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVectorService;
import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.llm_faq.FaqRestService;
import com.bytedesk.kbase.llm_faq.elastic.FaqElasticService;
import com.bytedesk.kbase.llm_faq.vector.FaqVectorService;
import com.bytedesk.kbase.llm_text.TextEntity;
import com.bytedesk.kbase.llm_text.TextRestService;
import com.bytedesk.kbase.llm_text.elastic.TextElasticService;
import com.bytedesk.kbase.llm_text.vector.TextVectorService;
import com.bytedesk.kbase.llm_webpage.WebpageEntity;
import com.bytedesk.kbase.llm_webpage.WebpageRestService;
import com.bytedesk.kbase.llm_webpage.elastic.WebpageElasticService;
import com.bytedesk.kbase.llm_webpage.vector.WebpageVectorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 翻译内容自动更新索引服务
 * 当翻译记录状态变为 SUCCESS 时，自动触发对应源实体的全文索引和向量索引更新
 *
 * @author jackning
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KbaseTranslationIndexService {

    private final FaqRestService faqRestService;
    private final FaqElasticService faqElasticService;
    private final FaqVectorService faqVectorService;

    private final TextRestService textRestService;
    private final TextElasticService textElasticService;
    private final TextVectorService textVectorService;

    private final ChunkRestService chunkRestService;
    private final ChunkElasticService chunkElasticService;
    private final ChunkVectorService chunkVectorService;

    private final WebpageRestService webpageRestService;
    private final WebpageElasticService webpageElasticService;
    private final WebpageVectorService webpageVectorService;

    /**
     * 翻译成功后，自动更新源实体对应的全文索引和向量索引
     *
     * @param sourceUid  源实体UID
     * @param sourceType 源类型 (FAQ, TEXT, CHUNK, WEBPAGE)
     * @return 索引更新结果
     */
    public Map<String, Object> reindexTranslationSource(String sourceUid, String sourceType) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sourceUid", sourceUid);
        result.put("sourceType", sourceType);

        try {
            switch (KbaseTranslationSourceTypeEnum.valueOf(sourceType.toUpperCase())) {
                case FAQ -> reindexFaq(sourceUid, result);
                case TEXT -> reindexText(sourceUid, result);
                case CHUNK -> reindexChunk(sourceUid, result);
                case WEBPAGE -> reindexWebpage(sourceUid, result);
                default -> {
                    result.put("success", false);
                    result.put("message", "unsupported sourceType: " + sourceType);
                    log.warn("translation reindex skipped: unsupported sourceType={}", sourceType);
                    return result;
                }
            }
            result.put("success", true);
        } catch (Exception e) {
            log.error("translation reindex failed: sourceUid={}, sourceType={}, error={}",
                    sourceUid, sourceType, e.getMessage(), e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    private void reindexFaq(String sourceUid, Map<String, Object> result) {
        FaqEntity faq = faqRestService.findByUid(sourceUid)
                .orElseThrow(() -> new RuntimeException("FAQ not found: " + sourceUid));
        faqElasticService.indexFaq(faq);
        faqVectorService.indexFaqVector(faq);
        result.put("fulltextIndexed", true);
        result.put("vectorIndexed", true);
        log.info("translation reindex completed: faqUid={}", sourceUid);
    }

    private void reindexText(String sourceUid, Map<String, Object> result) {
        TextEntity text = textRestService.findByUid(sourceUid)
                .orElseThrow(() -> new RuntimeException("Text not found: " + sourceUid));
        textElasticService.indexText(text);
        textVectorService.indexTextVector(text);
        result.put("fulltextIndexed", true);
        result.put("vectorIndexed", true);
        log.info("translation reindex completed: textUid={}", sourceUid);
    }

    private void reindexChunk(String sourceUid, Map<String, Object> result) {
        ChunkEntity chunk = chunkRestService.findByUid(sourceUid)
                .orElseThrow(() -> new RuntimeException("Chunk not found: " + sourceUid));
        chunkElasticService.indexChunk(chunk);
        chunkVectorService.indexChunkVector(chunk);
        result.put("fulltextIndexed", true);
        result.put("vectorIndexed", true);
        log.info("translation reindex completed: chunkUid={}", sourceUid);
    }

    private void reindexWebpage(String sourceUid, Map<String, Object> result) {
        WebpageEntity webpage = webpageRestService.findByUid(sourceUid)
                .orElseThrow(() -> new RuntimeException("Webpage not found: " + sourceUid));
        webpageElasticService.indexWebpage(webpage);
        webpageVectorService.indexWebpageVector(webpage);
        result.put("fulltextIndexed", true);
        result.put("vectorIndexed", true);
        log.info("translation reindex completed: webpageUid={}", sourceUid);
    }
}
