/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-25 10:12:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-25 10:13:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.rag;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import lombok.extern.slf4j.Slf4j;

/**
 * DocumentRetriever
 * Pre-Retrieval: DocumentRetriever 用于在检索之前对查询进行预处理，例如通过自定义逻辑过滤或修改查询。
 * https://java2ai.com/blog/spring-ai-alibaba-module-rag/?spm=0.29160081.0.0.75c73b5blqQmqQ
 */
@Slf4j
public class WebSearchRetriever implements DocumentRetriever {

    // 注入 IQS 搜索引擎
    // private final IQSSearchEngine searchEngine;

    // @NotNull
    // @Override
    // public List<Document> retrieve(
    //       @Nullable Query query
    // ) {

    //    // 搜索
    //    GenericSearchResult searchResp = searchEngine.search(query.text());

    //    // 清洗数据，将数据转换为 Spring AI 的 Document 对象
    //    List<Document> cleanerData = dataCleaner.getData(searchResp);
    //    logger.debug("cleaner data: {}", cleanerData);

    //    // 返回结果
    //    List<Document> documents = dataCleaner.limitResults(cleanerData, maxResults);

    //    logger.debug("WebSearchRetriever#retrieve() document size: {}, raw documents: {}",
    //          documents.size(),
    //          documents.stream().map(Document::getId).toArray()
    //    );

    //    return enableRanker ? ranking(query, documents) : documents;
    // }

    // private List<Document> ranking(Query query, List<Document> documents) {

    //    if (documents.size() == 1) {
    //       // 只有一个时，不需要 rank
    //       return documents;
    //    }

    //    try {
    //       List<Document> rankedDocuments = documentRanker.rank(query, documents);
    //       logger.debug("WebSearchRetriever#ranking() Ranked documents: {}", rankedDocuments.stream().map(Document::getId).toArray());
    //       return rankedDocuments;
    //    } catch (Exception e) {
    //       // 降级返回原始结果
    //       logger.error("ranking error", e);
    //       return documents;
    //    }
    // }


    public static final class Builder {
    // ...
    }

    @Override
    public List<Document> retrieve(Query query) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'retrieve'");
    }
}
