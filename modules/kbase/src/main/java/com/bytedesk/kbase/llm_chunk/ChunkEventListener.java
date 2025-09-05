/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 19:46:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.kbase.llm_file.FileEntity;
import com.bytedesk.kbase.llm_file.FileResponse;
import com.bytedesk.kbase.llm_file.FileRestService;
import com.bytedesk.kbase.llm_file.event.FileChunkEvent;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElasticService;
import com.bytedesk.kbase.llm_chunk.event.ChunkCreateEvent;
import com.bytedesk.kbase.llm_chunk.event.ChunkDeleteEvent;
import com.bytedesk.kbase.llm_chunk.event.ChunkUpdateDocEvent;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVectorService;
import com.bytedesk.core.utils.BdDateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChunkEventListener {

    private final ChunkElasticService chunkElasticService;

    private final ChunkRestService chunkRestService;

    private final FileRestService fileRestService;

    @Autowired(required = false)
    private ChunkVectorService chunkVectorService;

    public ChunkEventListener(ChunkElasticService chunkElasticService, ChunkRestService chunkRestService, FileRestService fileRestService) {
        this.chunkElasticService = chunkElasticService;
        this.chunkRestService = chunkRestService;
        this.fileRestService = fileRestService;
    }


    @EventListener
    public void onFileChunkEvent(FileChunkEvent event) {
        List<Document> documents = event.getDocuments();
        FileResponse fileResponse = event.getFileResponse();
        log.info("ChunkEventListener onFileCreateEvent: {}", fileResponse.getFileName());
        
        // 使用更安全的文本分割逻辑
        List<Document> docList = new ArrayList<>();
        try {
            // 继续原有的分割和存储逻辑，但添加错误处理
            var tokenTextSplitter = new TokenTextSplitter();
            docList = tokenTextSplitter.split(documents);
            log.info("TokenTextSplitter 分割成功，生成 {} 个文档块", docList.size());
        } catch (UnsupportedOperationException e) {
            log.warn("TokenTextSplitter 分割失败，使用替代方案: {}", e.getMessage());
            // 使用简单的字符数分割作为备选方案
            docList = fallbackSplitDocuments(documents);
        } catch (Exception e) {
            log.error("文档分割过程中发生错误: {}", e.getMessage(), e);
            // 使用简单的字符数分割作为备选方案
            docList = fallbackSplitDocuments(documents);
        }
        
        // 
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
        Optional<FileEntity> fileEntity = fileRestService.findByUid(fileResponse.getUid());
        if (fileEntity.isPresent()) {
            FileEntity file = fileEntity.get();
            file.setSuccess();
            file.setDocIdList(docIdList);
            fileRestService.save(file);
        } else {
            log.warn("文件实体不存在: {}", fileResponse.getUid());
        }
    }
    
    /**
     * 备选的文档分割方案 - 基于字符数的简单分割
     * 当TokenTextSplitter失败时使用
     */
    private List<Document> fallbackSplitDocuments(List<Document> documents) {
        List<Document> result = new ArrayList<>();
        int maxChunkSize = 2000; // 每个块的最大字符数
        int overlap = 200; // 重叠字符数
        
        for (Document doc : documents) {
            String text = doc.getText();
            if (text == null || text.trim().isEmpty()) {
                continue;
            }
            
            // 清理文本中可能导致问题的特殊字符
            text = cleanSpecialCharacters(text);
            
            if (text.length() <= maxChunkSize) {
                // 如果文档足够小，直接添加
                Document newDoc = new Document(text, doc.getMetadata());
                result.add(newDoc);
            } else {
                // 分割大文档
                int start = 0;
                while (start < text.length()) {
                    int end = Math.min(start + maxChunkSize, text.length());
                    
                    // 尝试在句子边界处分割
                    if (end < text.length()) {
                        int lastSentence = findLastSentenceBoundary(text, start, end);
                        if (lastSentence > start + maxChunkSize / 2) {
                            end = lastSentence + 1;
                        }
                    }
                    
                    String chunk = text.substring(start, end);
                    if (!chunk.trim().isEmpty()) {
                        Document newDoc = new Document(chunk, doc.getMetadata());
                        result.add(newDoc);
                    }
                    
                    start = end - overlap; // 保持重叠
                    if (start >= text.length()) break;
                }
            }
        }
        
        log.info("fallback分割完成，生成 {} 个文档块", result.size());
        return result;
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
    
    
    // Chunk仅用于全文搜索
    @EventListener
    public void onChunkCreateEvent(ChunkCreateEvent event) {
        ChunkEntity chunk = event.getChunk();
        log.info("ChunkEventListener onChunkCreateEvent: {}", chunk.getName());
        // 仅做全文索引
        chunkElasticService.indexChunk(chunk);
        /// 索引向量
        if (chunkVectorService != null) {
            try {
                chunkVectorService.indexChunkVector(chunk);
            } catch (Exception e) {
                log.error("Chunk向量索引失败: {}, 错误: {}", chunk.getName(), e.getMessage());
            }
        }
    }

    // Chunk仅用于全文搜索
    @EventListener
    public void onChunkUpdateDocEvent(ChunkUpdateDocEvent event) {
        ChunkEntity chunk = event.getChunk();
        log.info("ChunkEventListener ChunkUpdateDocEvent: {}", chunk.getName());
        // 更新全文索引
        chunkElasticService.indexChunk(chunk);
        // 更新向量索引
        if (chunkVectorService != null) {
            try {
                // 先删除旧的向量索引
                chunkVectorService.deleteChunkVector(chunk);
                // 再创建新的向量索引
                chunkVectorService.indexChunkVector(chunk);
                
            } catch (Exception e) {
                log.error("文本向量索引更新失败: {}, 错误: {}", chunk.getContent(), e.getMessage());
            }
        }
    }

    @EventListener
    public void onChunkDeleteEvent(ChunkDeleteEvent event) {
        ChunkEntity chunk = event.getChunk();
        log.info("ChunkEventListener onChunkDeleteEvent: {}", chunk.getName());
        // 从全文索引中删除
        boolean deleted = chunkElasticService.deleteChunk(chunk.getUid());
        if (!deleted) {
            log.warn("从Elasticsearch中删除Chunk索引失败: {}", chunk.getUid());
            // 可以考虑添加重试逻辑或者其他错误处理
        }
        try {
            chunkVectorService.deleteChunkVector(chunk);
        } catch (Exception e) {
            log.error("删除Chunk向量索引失败: {}, 错误: {}", chunk.getName(), e.getMessage());
        }
    }
}


