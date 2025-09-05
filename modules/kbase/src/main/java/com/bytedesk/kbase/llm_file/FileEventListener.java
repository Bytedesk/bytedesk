/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 15:00:00
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
import java.util.List;
import java.util.Optional;

import org.springframework.ai.document.Document;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.kbase.llm_chunk.ChunkRequest;
import com.bytedesk.kbase.llm_chunk.ChunkRestService;
import com.bytedesk.kbase.llm_chunk.ChunkTypeEnum;
import com.bytedesk.kbase.llm_chunk.mq.ChunkMessageService;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class FileEventListener {

    private final FileRestService fileRestService;

    private final FileService fileService;

    private final ChunkRestService chunkRestService;
    
    private final ChunkMessageService chunkMessageService;
    
    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        // 专门存储大模型上传文件记录
        if (UploadTypeEnum.LLM_FILE.name().equalsIgnoreCase(upload.getType())) {
            log.info("UploadEventListener LLM_FILE: {} - 开始使用MQ处理", upload.getFileName());
            
            try {
                // 解析文件内容
                List<Document> documents = fileService.parseFileContent(upload);
                
                // 使用智能摘要提取方法
                int maxContentLength = 60000; // 设置最大内容长度，预留一些空间
                String content = fileService.extractContentSummary(documents, maxContentLength);
                
                // 转换为 fileEntity，并保存
                UserProtobuf userProtobuf = UserProtobuf.fromJson(upload.getUser());
                FileRequest fileRequest = FileRequest.builder()
                    .uploadUid(upload.getUid())
                    .fileName(upload.getFileName())
                    .fileUrl(upload.getFileUrl())
                    .content(content)
                    .categoryUid(upload.getCategoryUid())
                    .kbUid(upload.getKbUid())
                    .userUid(userProtobuf.getUid())
                    .orgUid(upload.getOrgUid())
                    .build();
                    
                FileResponse fileResponse = fileRestService.create(fileRequest);
                
                // 新的MQ处理方式：先创建chunks记录，再发送到MQ异步索引
                log.info("使用MQ方式处理文件chunks: {}, 文档数量: {}", upload.getFileName(), documents.size());
                
                List<String> chunkUids = createChunksFromDocuments(documents, fileResponse);
                
                if (!chunkUids.isEmpty()) {
                    // 使用批量MQ发送，避免并发问题
                    log.info("批量发送{}个chunks到MQ队列进行索引处理", chunkUids.size());
                    chunkMessageService.batchSendToIndexQueue(chunkUids, fileResponse.getUid());
                    
                    // 更新文件状态为处理中
                    updateFileStatusToProcessing(fileResponse.getUid(), chunkUids);
                    
                    log.info("文件chunks已创建并发送到MQ队列: {}, chunks数量: {}", 
                            upload.getFileName(), chunkUids.size());
                } else {
                    log.warn("文件没有生成任何chunks: {}", upload.getFileName());
                    markFileAsFailed(fileResponse.getUid(), "没有生成任何有效的chunks");
                }
                
            } catch (Exception e) {
                log.error("处理文件上传事件失败: {}, 错误: {}", upload.getFileName(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * 从文档创建Chunk记录（同步创建，避免重复）
     * 改进了重复检测和内容过滤逻辑
     */
    private List<String> createChunksFromDocuments(List<Document> documents, FileResponse fileResponse) {
        List<String> chunkUids = new ArrayList<>();
        
        try {
            // 使用改进的文本分割器，避免产生重复内容
            var splittedDocs = splitDocumentsWithDeduplication(documents);
            log.info("文档分割完成，原始文档: {}, 分割后: {}", documents.size(), splittedDocs.size());
            
            for (Document doc : splittedDocs) {
                String content = doc.getText();
                
                // 过滤掉过短或无意义的内容
                if (!isValidContent(content)) {
                    log.debug("跳过无效内容，长度: {}", content != null ? content.length() : 0);
                    continue;
                }
                
                // 创建Chunk记录
                ChunkRequest chunkRequest = ChunkRequest.builder()
                        .name(fileResponse.getFileName())
                        .content(content.trim())
                        .type(ChunkTypeEnum.FILE.name())
                        .docId(doc.getId())
                        .fileUid(fileResponse.getUid())
                        .categoryUid(fileResponse.getCategoryUid())
                        .kbUid(fileResponse.getKbase() != null ? fileResponse.getKbase().getUid() : "")
                        .userUid(fileResponse.getUserUid())
                        .orgUid(fileResponse.getOrgUid())
                        .enabled(true)
                        .startDate(BdDateUtils.now())
                        .endDate(BdDateUtils.now().plusYears(100))
                        .build();
                
                try {
                    var chunkResponse = chunkRestService.create(chunkRequest);
                    chunkUids.add(chunkResponse.getUid());
                    log.debug("创建chunk成功: {}, 内容长度: {}", chunkResponse.getUid(), content.length());
                } catch (Exception e) {
                    log.error("创建chunk失败: {}, 错误: {}", content.substring(0, Math.min(50, content.length())), e.getMessage());
                }
            }
            
        } catch (Exception e) {
            log.error("创建chunks时发生错误: {}", e.getMessage(), e);
        }
        
        return chunkUids;
    }
    
    /**
     * 改进的文档分割器，避免产生重复内容
     */
    private List<Document> splitDocumentsWithDeduplication(List<Document> documents) {
        List<Document> result = new ArrayList<>();
        
        try {
            // 使用TokenTextSplitter进行分割
            var tokenTextSplitter = new org.springframework.ai.transformer.splitter.TokenTextSplitter();
            result = tokenTextSplitter.split(documents);
            
            // 去重处理
            result = deduplicateDocuments(result);
            
        } catch (Exception e) {
            log.warn("TokenTextSplitter失败，使用备选方案: {}", e.getMessage());
            result = fallbackSplitDocuments(documents);
        }
        
        return result;
    }
    
    /**
     * 文档去重
     */
    private List<Document> deduplicateDocuments(List<Document> documents) {
        List<Document> deduped = new ArrayList<>();
        List<String> seenContent = new ArrayList<>();
        
        for (Document doc : documents) {
            String content = doc.getText();
            if (content == null || content.trim().isEmpty()) {
                continue;
            }
            
            String normalizedContent = normalizeContent(content);
            
            // 检查是否已存在相似内容
            boolean isDuplicate = seenContent.stream()
                    .anyMatch(seen -> calculateSimilarity(seen, normalizedContent) > 0.85);
                    
            if (!isDuplicate) {
                deduped.add(doc);
                seenContent.add(normalizedContent);
            } else {
                log.debug("跳过重复内容: {}", content.substring(0, Math.min(50, content.length())));
            }
        }
        
        log.info("去重完成: 原始{}个文档，去重后{}个文档", documents.size(), deduped.size());
        return deduped;
    }
    
    /**
     * 内容标准化
     */
    private String normalizeContent(String content) {
        if (content == null) return "";
        return content.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }
    
    /**
     * 简单的相似度计算（基于编辑距离）
     */
    private double calculateSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0;
        if (s1.equals(s2)) return 1.0;
        
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        
        int editDistance = calculateEditDistance(s1, s2);
        return (maxLen - editDistance) / (double) maxLen;
    }
    
    /**
     * 计算编辑距离
     */
    private int calculateEditDistance(String s1, String s2) {
        // 为了性能考虑，只对前200个字符进行比较
        s1 = s1.length() > 200 ? s1.substring(0, 200) : s1;
        s2 = s2.length() > 200 ? s2.substring(0, 200) : s2;
        
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                }
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
    
    /**
     * 备选文档分割方案
     */
    private List<Document> fallbackSplitDocuments(List<Document> documents) {
        List<Document> result = new ArrayList<>();
        int chunkSize = 2000; // 增加chunk大小
        int overlap = 200;
        
        for (Document doc : documents) {
            String text = doc.getText();
            if (text == null || text.trim().isEmpty()) {
                continue;
            }
            
            if (text.length() <= chunkSize) {
                result.add(doc);
            } else {
                for (int start = 0; start < text.length(); start += chunkSize - overlap) {
                    int end = Math.min(start + chunkSize, text.length());
                    String chunk = text.substring(start, end);
                    
                    if (isValidContent(chunk)) {
                        Document newDoc = new Document(chunk, doc.getMetadata());
                        result.add(newDoc);
                    }
                }
            }
        }
        
        return deduplicateDocuments(result);
    }
    
    /**
     * 验证内容是否有效
     */
    private boolean isValidContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = content.trim();
        
        // 内容太短
        if (trimmed.length() < 100) {
            return false;
        }
        
        // 检查是否包含足够的单词
        String[] words = trimmed.split("\\s+");
        if (words.length < 10) {
            return false;
        }
        
        // 检查是否主要包含重复字符
        if (isRepetitiveContent(trimmed)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 检查是否为重复内容
     */
    private boolean isRepetitiveContent(String content) {
        if (content == null || content.length() < 10) {
            return true;
        }
        
        // 检查是否有太多重复的单词
        String[] words = content.split("\\s+");
        if (words.length < 5) {
            return true;
        }
        
        // 简单的重复检测：检查前几个单词是否重复出现
        String firstWords = String.join(" ", 
                java.util.Arrays.copyOfRange(words, 0, Math.min(3, words.length)));
        
        long occurrences = java.util.Arrays.stream(words)
                .filter(word -> firstWords.contains(word))
                .count();
                
        // 如果重复率超过50%，认为是重复内容
        return (double) occurrences / words.length > 0.5;
    }
    
    /**
     * 更新文件状态为处理中
     */
    private void updateFileStatusToProcessing(String fileUid, List<String> chunkUids) {
        try {
            Optional<FileEntity> fileEntity = fileRestService.findByUid(fileUid);
            if (fileEntity.isPresent()) {
                FileEntity file = fileEntity.get();
                // 这里可以设置处理中状态，或者记录chunk数量
                file.setDocIdList(chunkUids);
                fileRestService.save(file);
                log.info("文件状态已更新为处理中: {}, chunks: {}", file.getFileName(), chunkUids.size());
            } else {
                log.warn("更新文件状态时找不到文件实体: {}", fileUid);
            }
        } catch (Exception e) {
            log.error("更新文件状态失败: {}, 错误: {}", fileUid, e.getMessage(), e);
        }
    }
    
    /**
     * 标记文件处理失败
     */
    private void markFileAsFailed(String fileUid, String errorMessage) {
        try {
            Optional<FileEntity> fileEntity = fileRestService.findByUid(fileUid);
            if (fileEntity.isPresent()) {
                FileEntity file = fileEntity.get();
                file.setElasticError();
                file.setVectorError();
                fileRestService.save(file);
                log.error("文件处理失败: {}, 错误: {}", file.getFileName(), errorMessage);
            } else {
                log.warn("标记失败时文件实体不存在: {}", fileUid);
            }
        } catch (Exception e) {
            log.error("标记文件失败状态时出错: {}, 错误: {}", fileUid, e.getMessage(), e);
        }
    }
}

