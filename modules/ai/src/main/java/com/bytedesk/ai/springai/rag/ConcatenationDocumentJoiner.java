/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-25 10:14:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-25 10:15:19
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.join.DocumentJoiner;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

/**
 * ConcatenationDocumentJoiner
 * Pre-Retrieval: DocumentJoiner
 * https://java2ai.com/blog/spring-ai-alibaba-module-rag/?spm=0.29160081.0.0.75c73b5blqQmqQ
 */
@Slf4j
public class ConcatenationDocumentJoiner implements DocumentJoiner {

    @NotNull
    @Override
    public List<Document> join(
          @Nullable Map<Query, List<List<Document>>> documentsForQuery
    ) {
    // ...
       Map<Query, List<List<Document>>> selectDocuments = selectDocuments(documentsForQuery, 10);

       Set<String> seen = new HashSet<>();

       return selectDocuments.values().stream()
             // Flatten List<List<Documents>> to Stream<List<Documents>.
             .flatMap(List::stream)
             // Flatten Stream<List<Documents> to Stream<Documents>.
             .flatMap(List::stream)
             .filter(doc -> {
                List<String> keys = extractKeys(doc);
                for (String key : keys) {
                   if (!seen.add(key)) {
                      log.info("Duplicate document metadata: {}",doc.getMetadata());
                      // Duplicate keys found.
                      return false;
                   }
                }
                // All keys are unique.
                return true;
             })
             .collect(Collectors.toList());
    }

    private Map<Query, List<List<Document>>> selectDocuments(
          Map<Query, List<List<Document>>> documentsForQuery,
          int totalDocuments
    ) {

       Map<Query, List<List<Document>>> selectDocumentsForQuery = new HashMap<>();

       int numberOfQueries = documentsForQuery.size();

       if (Objects.equals(0, numberOfQueries)) {

          return selectDocumentsForQuery;
       }

       int baseCount = totalDocuments / numberOfQueries;
       int remainder = totalDocuments % numberOfQueries;

       // To ensure consistent distribution. sort the keys (optional)
       List<Query> sortedQueries = new ArrayList<>(documentsForQuery.keySet());
       // Other sort
       // sortedQueries.sort(Comparator.comparing(Query::getSomeProperty));
    //    Iterator<Query> iterator = sortedQueries.iterator();

       for (int i = 0; i < numberOfQueries; i ++) {
          Query query = sortedQueries.get(i);
          int documentToSelect = baseCount + (i < remainder ? 1 : 0);
          List<List<Document>> originalDocuments = documentsForQuery.get(query);
          List<List<Document>> selectedNestLists = new ArrayList<>();

          int remainingDocuments = documentToSelect;
          for (List<Document> documentList : originalDocuments) {
             if (remainingDocuments <= 0) {
                break;
             }
             List<Document> selectSubList = new ArrayList<>();
             for (Document docs : documentList) {
                if (remainingDocuments <= 0) {
                   break;
                }
                selectSubList.add(docs);
                remainingDocuments --;
             }
             if (!selectSubList.isEmpty()) {
                selectedNestLists.add(selectSubList);
             }
          }
          selectDocumentsForQuery.put(query, selectedNestLists);
       }
       return selectDocumentsForQuery;
    }

    private List<String> extractKeys(Document document) {
    // 提取 key
    //    return keys;
       return List.of(document.getId(), document.getText());
    }
}
