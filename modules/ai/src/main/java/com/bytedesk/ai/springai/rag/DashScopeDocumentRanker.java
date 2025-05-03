/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-25 10:15:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-04 10:22:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.rag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.util.StringUtils;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Post-Retrieval
 * 处理从联网搜索种获得的 Document，以获得最佳输出。
 * https://java2ai.com/blog/spring-ai-alibaba-module-rag/?spm=0.29160081.0.0.75c73b5blqQmqQ
 */
@Slf4j
public class DashScopeDocumentRanker implements DocumentPostProcessor {

    // ...

    @NotNull
    @Override
    public List<Document> process(
          @Nullable Query query,
          @Nullable List<Document> documents
    ) {
    // ...
       try {
          List<Document> reorderDocs = new ArrayList<>();

          // 由调用者控制文档数
        //   DashScopeRerankOptions rerankOptions = DashScopeRerankOptions.builder()
        //         .withTopN(documents.size())
        //         .build();

          if (Objects.nonNull(query) && StringUtils.hasText(query.text())) {
             // 组装参数调用 rankModel
            //  RerankRequest rerankRequest = new RerankRequest(
            //        query.text(),
            //        documents,
            //        rerankOptions
            //  );
            //  RerankResponse rerankResp = rerankModel.call(rerankRequest);

            //  rerankResp.getResults().forEach(res -> {
            //     Document outputDocs = res.getOutput();

            //     // 查找并添加到新的 list 中
            //     Optional<Document> foundDocsOptional = documents.stream()
            //           .filter(doc ->
            //           {
            //              // debug rerank output.
            //              log.debug("DashScopeDocumentRanker#rank() doc id: {}, outputDocs id: {}", doc.getId(), outputDocs.getId());
            //              return Objects.equals(doc.getId(), outputDocs.getId());
            //           })
            //           .findFirst();

            //     foundDocsOptional.ifPresent(reorderDocs::add);
            //  });

          }

          return reorderDocs;
       }
       catch (Exception e) {
          // 根据异常类型做进一步处理
        //   throw new SAAAppException(e.getMessage());
       }
       return null;
    }
}
