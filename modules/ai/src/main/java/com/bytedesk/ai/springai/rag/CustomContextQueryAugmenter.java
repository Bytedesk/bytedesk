/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-25 10:03:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-21 18:21:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.rag;

// import java.util.List;
// import java.util.Map;
// import java.util.concurrent.atomic.AtomicInteger;
// import java.util.stream.Collectors;

// import org.springframework.ai.chat.client.ChatClient;
// import org.springframework.ai.chat.prompt.PromptTemplate;
// import org.springframework.ai.document.Document;
// import org.springframework.ai.rag.Query;
// import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
// import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
// import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.context.annotation.Bean;

// import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

// import jakarta.annotation.Nullable;
// import jakarta.validation.constraints.NotNull;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * Pre-Retrieval: QueryAugmenter
//  * https://java2ai.com/blog/spring-ai-alibaba-module-rag/?spm=0.29160081.0.0.75c73b5blqQmqQ
//  */
// @Slf4j
// public class CustomContextQueryAugmenter implements QueryAugmenter {

//     // 定义 prompt template.
//     private static final PromptTemplate promptTemplate = new PromptTemplate(
//             "Given the query: [[query]], here is the context: [[context]]");

//     private static final PromptTemplate emptyPromptTemplate = new PromptTemplate(
//             "Given the query: [[query]], there is no context available.");

//     @NotNull
//     @Override
//     public Query augment(@Nullable Query query, @Nullable List<Document> documents) {
//         // 1. collect content from documents.
//         AtomicInteger idCounter = new AtomicInteger(1);
//         String documentContext = documents.stream()
//                 .map(document -> {
//                     String text = document.getText();
//                     return "[[" + (idCounter.getAndIncrement()) + "]]" + text;
//                 })
//                 .collect(Collectors.joining("\n-----------------------------------------------\n"));

//         // 2. Define prompt parameters.
//         Map<String, Object> promptParameters = Map.of(
//                 "query", query.text(),
//                 "context", documentContext);

//         // 3. Augment user prompt with document context.
//         return new Query(CustomContextQueryAugmenter.promptTemplate.render(promptParameters));
//     }

//     // 当上下文为空时，返回 DEFAULT_EMPTY_PROMPT_TEMPLATE
//     public Query augmentQueryWhenEmptyContext(Query query) {

//         // if (this.allowEmptyContext) {
//         // log.debug("Empty context is allowed. Returning the original query.");
//         // return query;
//         // }

//         log.debug("Empty context is not allowed. Returning a specific query for empty context.");
//         return new Query(CustomContextQueryAugmenter.emptyPromptTemplate.render());
//     }

//     public static final class Builder {
//         // ......
//     }

//     // QueryTransformer 配置 bean，用于 rewrite 用户 query：
//     // 1. 基于 ChatClient 调用 DashScope API，将用户 query 改写为更适合检索的 query；
//     @Bean
//     public QueryTransformer queryTransformer(
//             ChatClient.Builder chatClientBuilder,
//             @Qualifier("transformerPromptTemplate") PromptTemplate transformerPromptTemplate) {

//         ChatClient chatClient = chatClientBuilder.defaultOptions(
//                 DashScopeChatOptions.builder()
//                         .withModel("qwen-plus")
//                         .build())
//                 .build();

//         return RewriteQueryTransformer.builder()
//                 .chatClientBuilder(chatClient.mutate())
//                 .promptTemplate(transformerPromptTemplate)
//                 .targetSearchSystem("联网搜索")
//                 .build();
//     }
// }