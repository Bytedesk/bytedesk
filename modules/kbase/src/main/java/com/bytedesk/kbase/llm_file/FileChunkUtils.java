/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 12:15:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 12:15:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file;

import java.util.List;

import org.springframework.ai.document.Document;

import lombok.extern.slf4j.Slf4j;

/**
 * 文件chunk处理工具类
 * 提供文件处理相关的静态工具方法
 */
@Slf4j
public class FileChunkUtils {
    
    /**
     * 判断是否应该使用异步处理
     * 基于文档数量和内容复杂度
     * 
     * @param documents 文档列表
     * @return true 如果应该使用异步处理，false 使用同步处理
     */
    public static boolean shouldUseAsyncProcessing(List<Document> documents) {
        // 文档数量阈值
        int LARGE_FILE_THRESHOLD = 30;
        
        // 基础判断：文档数量
        if (documents.size() > LARGE_FILE_THRESHOLD) {
            return true;
        }
        
        // 高级判断：文档总内容长度
        int totalContentLength = documents.stream()
            .mapToInt(doc -> doc.getText() != null ? doc.getText().length() : 0)
            .sum();
        
        // 如果总内容超过100KB，使用异步处理
        int LARGE_CONTENT_THRESHOLD = 100000;
        if (totalContentLength > LARGE_CONTENT_THRESHOLD) {
            log.info("文档总内容长度过大({} 字符)，使用异步处理", totalContentLength);
            return true;
        }
        
        // 检查是否有单个超大文档
        boolean hasLargeDocument = documents.stream()
            .anyMatch(doc -> doc.getText() != null && doc.getText().length() > 20000);
        
        if (hasLargeDocument) {
            log.info("存在超大文档，使用异步处理");
            return true;
        }
        
        return false;
    }
    
    /**
     * 计算文档总内容长度
     * 
     * @param documents 文档列表
     * @return 总字符数
     */
    public static int calculateTotalContentLength(List<Document> documents) {
        return documents.stream()
            .mapToInt(doc -> doc.getText() != null ? doc.getText().length() : 0)
            .sum();
    }
    
    /**
     * 检查是否包含超大文档
     * 
     * @param documents 文档列表
     * @param threshold 单个文档大小阈值（字符数）
     * @return true 如果包含超大文档
     */
    public static boolean hasLargeDocument(List<Document> documents, int threshold) {
        return documents.stream()
            .anyMatch(doc -> doc.getText() != null && doc.getText().length() > threshold);
    }
}
