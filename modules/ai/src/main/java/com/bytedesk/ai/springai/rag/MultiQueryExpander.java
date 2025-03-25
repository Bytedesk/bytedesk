/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-25 10:09:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-25 10:11:51
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
// import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

/**
 * QueryExpander 
 * Pre-Retrieval: QueryExpander
 * https://java2ai.com/blog/spring-ai-alibaba-module-rag/?spm=0.29160081.0.0.75c73b5blqQmqQ
 */
@Slf4j
public class MultiQueryExpander implements QueryExpander {

    // private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate(
    //         "Please enter [[number]] queries related to [[query]]"
    // );

    @NotNull
    @Override
    public List<Query> expand(@Nullable Query query) {

    // ...

    //    String resp = this.chatClient.prompt()
    //          .user(user -> user.text(this.promptTemplate.getTemplate())
    //                .param("number", this.numberOfQueries)
    //                .param("query", query.text()))
    //          .call()
    //          .content();

        // ...

    //    List<String> queryVariants = Arrays.stream(resp.split("\n")).filter(StringUtils::hasText).toList();

    //    if (CollectionUtils.isEmpty(queryVariants) || this.numberOfQueries != queryVariants.size()) {
    //       return List.of(query);
    //    }

    //    List<Query> queries = queryVariants.stream()
    //          .filter(StringUtils::hasText)
    //          .map(queryText -> query.mutate().text(queryText).build())
    //          .collect(Collectors.toList());

    //     // 是否引入原查询
    //    if (this.includeOriginal) {

    //       log.debug("Including original query in the expanded queries for query: {}", query.text());
    //       queries.add(0, query);
    //    }

    //    return queries;

        return null;
    }

    public static final class Builder {
    // ......
    }

}
