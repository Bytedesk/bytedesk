/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:47:47
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

import com.bytedesk.kbase.file.FileEntity;
import com.bytedesk.kbase.file.FileResponse;
import com.bytedesk.kbase.file.FileRestService;
import com.bytedesk.kbase.file.event.FileChunkEvent;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElasticService;
import com.bytedesk.kbase.llm_chunk.event.ChunkCreateEvent;
import com.bytedesk.kbase.llm_chunk.event.ChunkDeleteEvent;
import com.bytedesk.kbase.llm_chunk.event.ChunkUpdateDocEvent;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVectorService;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ChunkEventListener {

    private final ChunkElasticService chunkElasticService;

    private final ChunkRestService chunkRestService;

    private final FileRestService fileRestService;

    private final ChunkVectorService chunkVectorService;


    @EventListener
    public void onFileChunkEvent(FileChunkEvent event) {
        List<Document> documents = event.getDocuments();
        FileResponse fileResponse = event.getFileResponse();
        log.info("ChunkEventListener onFileCreateEvent: {}", fileResponse.getFileName());
        // 
        // 继续原有的分割和存储逻辑
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(documents);
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
			chunkRestService.initVisitor(splitRequest);
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
    
    
    // Chunk仅用于全文搜索
    @EventListener
    public void onChunkCreateEvent(ChunkCreateEvent event) {
        ChunkEntity chunk = event.getChunk();
        log.info("ChunkEventListener onChunkCreateEvent: {}", chunk.getName());
        // 仅做全文索引
        chunkElasticService.indexChunk(chunk);
        /// 索引向量
        try {
            chunkVectorService.indexChunkVector(chunk);
        } catch (Exception e) {
            log.error("Chunk向量索引失败: {}, 错误: {}", chunk.getName(), e.getMessage());
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
        try {
            // 先删除旧的向量索引
            chunkVectorService.deleteChunkVector(chunk);
            // 再创建新的向量索引
            chunkVectorService.indexChunkVector(chunk);
            
        } catch (Exception e) {
            log.error("文本向量索引更新失败: {}, 错误: {}", chunk.getContent(), e.getMessage());
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


