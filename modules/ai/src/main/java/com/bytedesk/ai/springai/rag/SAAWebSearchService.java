/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-25 10:22:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-25 10:22:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.postretrieval.ranking.DocumentRanker;
import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

import reactor.core.publisher.Flux;

/**
 * https://java2ai.com/blog/spring-ai-alibaba-module-rag/?spm=0.29160081.0.0.75c73b5blqQmqQ
 */
@Service
public class SAAWebSearchService {

  // ...

    // private static final String DEFAULT_WEB_SEARCH_MODEL = "deepseek-r1";

    // public SAAWebSearchService(
    //       ChatClient.Builder chatClientBuilder,
    //       QueryTransformer queryTransformer,
    //       QueryExpander queryExpander,
    //       IQSSearchEngine searchEngine,
    //       DataClean dataCleaner,
    //       DocumentRanker documentRanker,
    //       @Qualifier("queryArgumentPromptTemplate") PromptTemplate queryArgumentPromptTemplate
    // ) {

    //    this.queryTransformer = queryTransformer;
    //    this.queryExpander = queryExpander;
    //    this.queryArgumentPromptTemplate = queryArgumentPromptTemplate;

    //    // 用于 DeepSeek-r1 的 reasoning content 整合到输出中
    //    this.reasoningContentAdvisor = new ReasoningContentAdvisor(1);

    //    // 构建 chatClient
    //    this.chatClient = chatClientBuilder
    //          .defaultOptions(
    //                DashScopeChatOptions.builder()
    //                      .withModel(DEFAULT_WEB_SEARCH_MODEL)
    //                      // stream 模式下是否开启增量输出
    //                      .withIncrementalOutput(true)
    //                      .build())
    //          .build();

    //    // 日志
    //    this.simpleLoggerAdvisor = new SimpleLoggerAdvisor(100);

    //    this.webSearchRetriever = WebSearchRetriever.builder()
    //          .searchEngine(searchEngine)
    //          .dataCleaner(dataCleaner)
    //          .maxResults(2)
    //          .enableRanker(true)
    //          .documentRanker(documentRanker)
    //          .build();
    // }

    // // 处理用户输入
    // public Flux<String> chat(String prompt) {

    //    return chatClient.prompt()
    //          .advisors(
    //                createRetrievalAugmentationAdvisor(),
    //                // 整合到 reasoning content 输出中
    //                reasoningContentAdvisor,
    //                simpleLoggerAdvisor
    //          ).user(prompt)
    //          .stream()
    //          .content();
    // }

    // // 创建 advisor
    // private RetrievalAugmentationAdvisor createRetrievalAugmentationAdvisor() {

    //    return RetrievalAugmentationAdvisor.builder()
    //          .documentRetriever(webSearchRetriever)
    //          .queryTransformers(queryTransformer)
    //          .queryAugmenter(
    //                new CustomContextQueryAugmenter(
    //                      queryArgumentPromptTemplate,
    //                      null,
    //                      true)
    //          ).queryExpander(queryExpander)
    //          .documentJoiner(new ConcatenationDocumentJoiner())
    //          .build();
    // }

}
