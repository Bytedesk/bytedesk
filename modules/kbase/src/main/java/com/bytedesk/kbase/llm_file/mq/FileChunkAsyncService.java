/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 12:45:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 13:17:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file.mq;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bytedesk.kbase.llm_file.FileChunkService;
import com.bytedesk.kbase.llm_file.FileResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 文件Chunk异步处理服务
 * 用于异步处理大文件的chunk切分，避免内存溢出和阻塞
 */
@Slf4j
@Service
public class FileChunkAsyncService {

    @Autowired
    private FileChunkService fileChunkService;
    
    @Autowired
    private FileChunkMessageService messageService;
    
    private static final int BATCH_SIZE = 10; // 每批处理的文档数量
    private static final int MAX_DOCUMENT_SIZE = 30000; // 单个文档最大字符数
    
    /**
     * 异步处理文件chunk切分
     * 将大文档集合分批处理，避免内存问题
     * 
     * @param documents 文档列表
     * @param fileResponse 文件响应对象
     * @return 异步处理结果
     */
    @Async("fileChunkTaskExecutor")
    public CompletableFuture<List<String>> processFileChunksAsync(List<Document> documents, FileResponse fileResponse) {
        log.info("开始异步处理文件chunk切分: {}, 文档数量: {}", fileResponse.getFileName(), documents.size());
        
        try {
            // 预处理：过滤和拆分过大的文档
            List<Document> processedDocuments = preprocessDocuments(documents);
            log.info("文档预处理完成，原始数量: {}, 处理后数量: {}", documents.size(), processedDocuments.size());
            
            // 分批处理文档
            List<String> allDocIds = new ArrayList<>();
            int totalBatches = (int) Math.ceil((double) processedDocuments.size() / BATCH_SIZE);
            
            for (int batchIndex = 0; batchIndex < totalBatches; batchIndex++) {
                int start = batchIndex * BATCH_SIZE;
                int end = Math.min(start + BATCH_SIZE, processedDocuments.size());
                List<Document> batch = processedDocuments.subList(start, end);
                
                log.info("处理第 {}/{} 批次，文档范围: {}-{}", batchIndex + 1, totalBatches, start, end - 1);
                
                try {
                    // 处理当前批次
                    List<String> batchDocIds = processBatch(batch, fileResponse, batchIndex + 1);
                    allDocIds.addAll(batchDocIds);
                    
                    // 批次间休眠，避免系统压力过大
                    if (batchIndex < totalBatches - 1) {
                        Thread.sleep(100 + (batchIndex * 50)); // 递增延迟
                    }
                    
                } catch (Exception e) {
                    log.error("处理第 {} 批次时发生错误: {}", batchIndex + 1, e.getMessage(), e);
                    // 继续处理下一批次，不中断整个流程
                }
            }
            
            log.info("文件chunk切分完成: {}, 总共生成 {} 个chunk", fileResponse.getFileName(), allDocIds.size());
            
            // 发送完成通知到消息队列
            messageService.sendChunkProcessCompleteMessage(fileResponse.getUid(), allDocIds.size());
            
            return CompletableFuture.completedFuture(allDocIds);
            
        } catch (Exception e) {
            log.error("异步处理文件chunk切分失败: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * 预处理文档：过滤空文档，拆分过大文档
     */
    private List<Document> preprocessDocuments(List<Document> documents) {
        List<Document> result = new ArrayList<>();
        
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            String text = doc.getText();
            
            if (text == null || text.trim().isEmpty()) {
                log.debug("跳过空文档: {}", i);
                continue;
            }
            
            // 清理文本
            text = cleanText(text);
            
            if (text.length() <= MAX_DOCUMENT_SIZE) {
                // 文档大小适中，直接添加
                result.add(new Document(text, doc.getMetadata()));
            } else {
                // 文档过大，需要拆分
                log.info("文档 {} 过大 ({} 字符)，进行拆分", i, text.length());
                List<Document> splitDocs = splitLargeDocument(text, doc.getMetadata());
                result.addAll(splitDocs);
                log.info("文档 {} 拆分完成，生成 {} 个子文档", i, splitDocs.size());
            }
        }
        
        return result;
    }
    
    /**
     * 清理文本内容
     */
    private String cleanText(String text) {
        if (text == null) return "";
        
        return text
            // 移除控制字符
            .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", " ")
            // 规范化空白字符
            .replaceAll("\\s+", " ")
            // 移除特殊Unicode字符
            .replaceAll("[\\uFFF0-\\uFFFF]", "")
            // 移除多余的空行
            .replaceAll("\\n\\s*\\n\\s*\\n", "\n\n")
            .trim();
    }
    
    /**
     * 拆分大文档
     */
    private List<Document> splitLargeDocument(String text, java.util.Map<String, Object> metadata) {
        List<Document> result = new ArrayList<>();
        int chunkSize = MAX_DOCUMENT_SIZE / 2; // 使用较小的块大小
        int overlap = 200;
        
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            
            // 尝试在句子边界处分割
            if (end < text.length()) {
                int lastSentence = findLastSentenceBoundary(text, start, end);
                if (lastSentence > start + chunkSize / 2) {
                    end = lastSentence + 1;
                }
            }
            
            String chunk = text.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                result.add(new Document(chunk, metadata));
            }
            
            start = end - overlap;
            if (start >= text.length()) break;
        }
        
        return result;
    }
    
    /**
     * 查找句子边界
     */
    private int findLastSentenceBoundary(String text, int start, int end) {
        String searchText = text.substring(start, end);
        int lastDot = searchText.lastIndexOf('。');
        int lastExclamation = searchText.lastIndexOf('！');
        int lastQuestion = searchText.lastIndexOf('？');
        int lastPeriod = searchText.lastIndexOf('.');
        int lastExcl = searchText.lastIndexOf('!');
        int lastQuest = searchText.lastIndexOf('?');
        
        int maxPos = Math.max(lastDot, Math.max(lastExclamation, Math.max(lastQuestion, 
                     Math.max(lastPeriod, Math.max(lastExcl, lastQuest)))));
        
        return maxPos > 0 ? start + maxPos : end - 1;
    }
    
    /**
     * 处理单个批次
     */
    private List<String> processBatch(List<Document> batch, FileResponse fileResponse, int batchNumber) {
        log.debug("开始处理批次 {}, 包含 {} 个文档", batchNumber, batch.size());
        
        try {
            // 调用内部处理方法，避免循环调用
            List<String> docIds = fileChunkService.processFileChunksInternal(batch, fileResponse);
            log.debug("批次 {} 处理完成，生成 {} 个chunk", batchNumber, docIds.size());
            return docIds;
            
        } catch (Exception e) {
            log.error("批次 {} 处理失败: {}", batchNumber, e.getMessage(), e);
            
            // 如果批次处理失败，尝试逐个处理
            return processDocumentsIndividually(batch, fileResponse, batchNumber);
        }
    }
    
    /**
     * 逐个处理文档（当批次处理失败时的兜底方案）
     */
    private List<String> processDocumentsIndividually(List<Document> documents, FileResponse fileResponse, int batchNumber) {
        log.info("批次 {} 进入逐个处理模式", batchNumber);
        List<String> docIds = new ArrayList<>();
        
        for (int i = 0; i < documents.size(); i++) {
            try {
                List<Document> singleDoc = List.of(documents.get(i));
                List<String> singleDocIds = fileChunkService.processFileChunksInternal(singleDoc, fileResponse);
                docIds.addAll(singleDocIds);
                log.debug("批次 {} 中第 {} 个文档处理成功", batchNumber, i + 1);
                
                // 文档间小延迟
                Thread.sleep(50);
                
            } catch (Exception e) {
                log.error("批次 {} 中第 {} 个文档处理失败: {}", batchNumber, i + 1, e.getMessage());
                // 发送失败文档到消息队列进行重试
                messageService.sendChunkRetryMessage(fileResponse.getUid(), i, e.getMessage());
            }
        }
        
        return docIds;
    }
}
