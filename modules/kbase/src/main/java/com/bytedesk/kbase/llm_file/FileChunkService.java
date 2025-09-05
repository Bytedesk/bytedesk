/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 11:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 11:12:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.kbase.llm_chunk.ChunkRequest;
import com.bytedesk.kbase.llm_chunk.ChunkRestService;
import com.bytedesk.kbase.llm_chunk.ChunkTypeEnum;
import com.bytedesk.kbase.llm_file.mq.FileChunkMessageService;

import org.springframework.scheduling.annotation.Async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件Chunk处理服务
 * 负责将文档分割成chunks并创建相应的记录
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileChunkService {

    private final ChunkRestService chunkRestService;
    
    private final FileChunkMessageService messageService;
    
    private static final int BATCH_SIZE = 10; // 每批处理的文档数量
    private static final int MAX_DOCUMENT_SIZE = 30000; // 单个文档最大字符数

    /**
     * 处理文件chunk切分 - 同步版本
     * 直接处理文档，不进行大小判断
     */
    public List<String> processFileChunks(List<Document> documents, FileResponse fileResponse) {
        log.info("同步处理文件chunk切分: {}, 文档数量: {}", fileResponse.getFileName(), documents.size());
        return processFileChunksInternal(documents, fileResponse);
    }
    
    /**
     * 处理文件chunk切分 - 异步版本（推荐用于大文件）
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
            if (messageService != null) {
                messageService.sendChunkProcessCompleteMessage(fileResponse.getUid(), allDocIds.size());
            }
            
            return CompletableFuture.completedFuture(allDocIds);
            
        } catch (Exception e) {
            log.error("异步处理文件chunk切分失败: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * 内部同步处理方法 - 被异步服务调用
     * 专注于核心的文档切分和存储逻辑
     */
    public List<String> processFileChunksInternal(List<Document> documents, FileResponse fileResponse) {
        
        // 使用更安全的文本分割逻辑
        List<Document> docList = new ArrayList<>();
        try {
            // 继续原有的分割和存储逻辑，但添加错误处理
            var tokenTextSplitter = new TokenTextSplitter();
            docList = tokenTextSplitter.split(documents);
            log.info("TokenTextSplitter 分割成功，生成 {} 个文档块", docList.size());
        } catch (UnsupportedOperationException e) {
            log.warn("TokenTextSplitter 分割失败，使用替代方案: {}", e.getMessage());
            log.info("开始执行fallback分割，输入文档数量: {}", documents.size());
            // 使用简单的字符数分割作为备选方案
            docList = fallbackSplitDocuments(documents);
            log.info("fallback分割执行完成");
        } catch (Exception e) {
            log.error("文档分割过程中发生错误: {}", e.getMessage(), e);
            log.info("开始执行fallback分割，输入文档数量: {}", documents.size());
            // 使用简单的字符数分割作为备选方案
            docList = fallbackSplitDocuments(documents);
            log.info("fallback分割执行完成");
        }
        log.info("最终生成 {} 个文档块", docList.size());
        
        // 创建chunk记录
        List<String> docIdList = new ArrayList<>();
        Iterator<Document> iterator = docList.iterator();
        while (iterator.hasNext()) {
            Document doc = iterator.next();
            log.info("doc id: {}", doc.getId());
            docIdList.add(doc.getId());
            // 
            ChunkRequest splitRequest = ChunkRequest.builder()
                    .name(fileResponse.getFileName())
                    .content(doc.getText())
                    .type(ChunkTypeEnum.FILE.name())
                    .docId(doc.getId())
                    .fileUid(fileResponse.getUid())
                    .categoryUid(fileResponse.getCategoryUid())
                    .kbUid(fileResponse.getKbase() != null ? fileResponse.getKbase().getUid() : "")
                    .userUid(fileResponse.getUserUid())
                    .orgUid(fileResponse.getOrgUid())
                    .enabled(true) // 使用文件的启用状态，默认为true
                    .startDate(BdDateUtils.now()) // 使用文件的开始日期，默认为当前时间
                    .endDate(BdDateUtils.now().plusYears(100)) // 使用文件的结束日期，默认为100年后
                    .build();
            chunkRestService.create(splitRequest);
        }
        
        return docIdList;
    }

    /**
     * 备选的文档分割方案 - 基于字符数的简单分割
     * 当TokenTextSplitter失败时使用
     */
    private List<Document> fallbackSplitDocuments(List<Document> documents) {
        log.info("开始执行fallback分割，输入文档数量: {}", documents.size());
        List<Document> result = new ArrayList<>();
        int maxChunkSize = 1000; // 减小每个块的最大字符数，避免内存问题
        int overlap = 100; // 减小重叠字符数
        
        int docIndex = 0;
        int totalChunks = 0;
        
        for (Document doc : documents) {
            docIndex++;
            log.info("处理第 {} 个文档，文档ID: {}", docIndex, doc.getId());
            
            try {
                String text = doc.getText();
                if (text == null || text.trim().isEmpty()) {
                    log.debug("第 {} 个文档内容为空，跳过", docIndex);
                    continue;
                }
                
                int originalLength = text.length();
                log.info("第 {} 个文档原始长度: {} 字符", docIndex, originalLength);
                
                // 如果文档太大，先进行预处理以减少内存使用
                if (originalLength > 50000) {
                    log.warn("第 {} 个文档过大 ({} 字符)，进行预处理", docIndex, originalLength);
                    text = preprocessLargeText(text);
                    log.info("第 {} 个文档预处理后长度: {} 字符", docIndex, text.length());
                }
                
                // 清理文本中可能导致问题的特殊字符
                text = cleanSpecialCharacters(text);
                log.debug("第 {} 个文档清理后长度: {} 字符", docIndex, text.length());
                
                // 分割文档
                List<Document> chunks = splitSingleDocument(text, doc.getMetadata(), maxChunkSize, overlap);
                result.addAll(chunks);
                totalChunks += chunks.size();
                
                log.info("第 {} 个文档分割完成，生成 {} 个块，累计 {} 个块", docIndex, chunks.size(), totalChunks);
                
                // 强制垃圾回收以释放内存
                if (docIndex % 10 == 0) {
                    System.gc();
                    log.debug("已处理 {} 个文档，执行垃圾回收", docIndex);
                }
                
            } catch (Exception e) {
                log.error("处理第 {} 个文档时发生错误: {}", docIndex, e.getMessage(), e);
                // 继续处理下一个文档
                continue;
            }
        }
        
        log.info("fallback分割完成，总共处理 {} 个文档，生成 {} 个文档块", docIndex, result.size());
        return result;
    }
    
    /**
     * 预处理大文本，移除多余的空白和重复内容
     */
    private String preprocessLargeText(String text) {
        if (text == null) return "";
        
        return text
            // 移除多余的空行
            .replaceAll("\\n\\s*\\n\\s*\\n", "\n\n")
            // 移除多余的空格
            .replaceAll(" +", " ")
            // 移除行首行尾的空格
            .replaceAll("(?m)^\\s+|\\s+$", "")
            .trim();
    }
    
    /**
     * 分割单个文档
     */
    private List<Document> splitSingleDocument(String text, java.util.Map<String, Object> metadata, int maxChunkSize, int overlap) {
        log.debug("开始分割单个文档，原始长度: {} 字符，最大块大小: {}，重叠: {}", text.length(), maxChunkSize, overlap);
        List<Document> chunks = new ArrayList<>();
        
        if (text.length() <= maxChunkSize) {
            // 如果文档足够小，直接添加
            log.debug("文档长度 {} <= 最大块大小 {}，直接创建单个块", text.length(), maxChunkSize);
            Document newDoc = new Document(text, metadata);
            chunks.add(newDoc);
            log.debug("创建单个块完成，块ID: {}", newDoc.getId());
        } else {
            // 分割大文档
            log.info("文档过大 ({} 字符)，开始分割处理", text.length());
            int start = 0;
            int chunkIndex = 0;
            
            while (start < text.length()) {
                chunkIndex++;
                int end = Math.min(start + maxChunkSize, text.length());
                log.debug("处理第 {} 个块，起始位置: {}，预计结束位置: {}", chunkIndex, start, end);
                
                // 尝试在句子边界处分割
                if (end < text.length()) {
                    int originalEnd = end;
                    int lastSentence = findLastSentenceBoundary(text, start, end);
                    if (lastSentence > start + maxChunkSize / 2) {
                        end = lastSentence + 1;
                        log.debug("第 {} 个块在句子边界调整：{} -> {}", chunkIndex, originalEnd, end);
                    } else {
                        log.debug("第 {} 个块未找到合适的句子边界，保持原结束位置: {}", chunkIndex, end);
                    }
                }
                
                String chunk = text.substring(start, end);
                int chunkLength = chunk.length();
                log.debug("第 {} 个块提取完成，长度: {} 字符", chunkIndex, chunkLength);
                
                if (!chunk.trim().isEmpty()) {
                    Document newDoc = new Document(chunk, metadata);
                    chunks.add(newDoc);
                    log.debug("第 {} 个块创建成功，块ID: {}，累计块数: {}", chunkIndex, newDoc.getId(), chunks.size());
                } else {
                    log.debug("第 {} 个块内容为空，跳过", chunkIndex);
                }
                
                int newStart = end - overlap;
                log.debug("第 {} 个块处理完成，下一个起始位置: {} (当前结束: {}，重叠: {})", chunkIndex, newStart, end, overlap);
                start = newStart;
                
                if (start >= text.length()) {
                    log.debug("已到达文档末尾，停止分割");
                    break;
                }
                
                // 防止无限循环
                if (chunkIndex > 1000) {
                    log.warn("文档分割块数过多 ({}块)，停止分割以防止内存问题", chunkIndex);
                    break;
                }
                
                // 每50个块输出一次进度
                if (chunkIndex % 50 == 0) {
                    log.info("分割进度：已处理 {} 个块，当前位置: {}/{} ({:.2f}%)", 
                            chunkIndex, start, text.length(), (double)start/text.length()*100);
                }
            }
            
            log.info("大文档分割完成，总共生成 {} 个块", chunks.size());
        }
        
        log.debug("splitSingleDocument 完成，返回 {} 个文档块", chunks.size());
        return chunks;
    }
    
    /**
     * 清理可能导致编码问题的特殊字符
     */
    private String cleanSpecialCharacters(String text) {
        if (text == null) return "";
        
        // 移除或替换可能导致问题的特殊字符
        return text
            // 移除控制字符（除了常见的换行、回车、制表符）
            .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", " ")
            // 规范化空白字符
            .replaceAll("\\s+", " ")
            // 移除可能的特殊Unicode字符
            .replaceAll("[\\uFFF0-\\uFFFF]", "")
            .trim();
    }
    
    /**
     * 在指定范围内查找最后一个句子边界
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
     * 处理单个批次
     */
    private List<String> processBatch(List<Document> batch, FileResponse fileResponse, int batchNumber) {
        log.debug("开始处理批次 {}, 包含 {} 个文档", batchNumber, batch.size());
        
        try {
            // 调用内部处理方法
            List<String> docIds = processFileChunksInternal(batch, fileResponse);
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
                List<String> singleDocIds = processFileChunksInternal(singleDoc, fileResponse);
                docIds.addAll(singleDocIds);
                log.debug("批次 {} 中第 {} 个文档处理成功", batchNumber, i + 1);
                
                // 文档间小延迟
                Thread.sleep(50);
                
            } catch (Exception e) {
                log.error("批次 {} 中第 {} 个文档处理失败: {}", batchNumber, i + 1, e.getMessage());
                // 发送失败文档到消息队列进行重试
                if (messageService != null) {
                    messageService.sendChunkRetryMessage(fileResponse.getUid(), i, e.getMessage());
                }
            }
        }
        
        return docIds;
    }
}
