/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 14:38:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 14:46:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.utils.reader;

// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;
// import org.springframework.ai.document.DocumentReader;

// import java.io.IOException;
// import java.net.URI;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;

// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// public class WebDocumentReader implements DocumentReader {
//     private final URI uri;
//     private final Map<String, String> metadata;
//     private static final int TIMEOUT = 10000; // 10 seconds

//     public WebDocumentReader(URI uri) {
//         this(uri, Map.of());
//     }

//     public WebDocumentReader(URI uri, Map<String, String> metadata) {
//         this.uri = uri;
//         this.metadata = metadata;
//     }

//     @Override
//     public List<org.springframework.ai.document.Document> get() {
//         List<org.springframework.ai.document.Document> documents = new ArrayList<>();
//         try {
//             // 使用JSoup获取网页内容
//             Document jsoupDoc = Jsoup.connect(uri.toString())
//                     .timeout(TIMEOUT)
//                     .userAgent("Mozilla/5.0 (compatible; BytedeskBot/1.0; +https://www.bytedesk.com/bot)")
//                     .get();

//             // 提取有用的文本内容（标题、正文等）
//             String title = jsoupDoc.title();
//             // 移除script、style等标签，只保留有用的文本
//             jsoupDoc.select("script, style, meta, link").remove();
//             String content = jsoupDoc.body().text();

//             // 创建Spring AI Document对象
//             org.springframework.ai.document.Document aiDocument = new org.springframework.ai.document.Document(content);
            
//             // 添加元数据
//             aiDocument.getMetadata().putAll(metadata);
//             aiDocument.getMetadata().put("title", title);
//             aiDocument.getMetadata().put("url", uri.toString());
//             aiDocument.getMetadata().put("type", "webpage");

//             documents.add(aiDocument);

//         } catch (IOException e) {
//             log.error("Error fetching webpage {}: {}", uri, e.getMessage());
//             throw new RuntimeException("Failed to fetch webpage: " + e.getMessage(), e);
//         }
//         return documents;
//     }

// } 