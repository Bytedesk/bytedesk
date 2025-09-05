/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 11:42:59
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
import java.util.Optional;

import org.springframework.ai.document.Document;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class FileEventListener {

    private final FileRestService fileRestService;

    private final FileService fileService;

    private final FileChunkService fileChunkService;
    
    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        // 专门存储大模型上传文件记录
        if (UploadTypeEnum.LLM_FILE.name().equalsIgnoreCase(upload.getType())) {
            log.info("UploadEventListener LLM_FILE: {}", upload.getFileName());
            // 
            List<Document> documents = fileService.parseFileContent(upload);
            
            // 使用智能摘要提取方法
            int maxContentLength = 60000; // 设置最大内容长度，预留一些空间
            String content = fileService.extractContentSummary(documents, maxContentLength);
            // 
            UserProtobuf userProtobuf = UserProtobuf.fromJson(upload.getUser());
            // 转换为 fileEntity，并保存
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
            // 读取文件内容，写入到content字段，迁移到 ai 模块 SpringAIEventListener中读取
            FileResponse fileResponse = fileRestService.create(fileRequest);
            
            // 根据文档数量选择处理方式
            if (shouldUseAsyncProcessing(documents)) {
                // 大文件使用异步批处理
                log.info("大文件({} 个文档)使用异步批处理: {}", 
                        documents.size(), upload.getFileName());
                processLargeFileAsync(documents, fileResponse);
            } else {
                // 小文件使用同步处理
                log.info("小文件({} 个文档)使用同步处理: {}", 
                        documents.size(), upload.getFileName());
                processSmallFileSync(documents, fileResponse);
            }
        }
    }
    
    /**
     * 处理小文件（同步）
     */
    private void processSmallFileSync(List<Document> documents, FileResponse fileResponse) {
        try {
            // 直接在这里进行chunk切分，使用专门的服务
            List<String> docIdList = fileChunkService.processFileChunks(documents, fileResponse);
            
            // 更新文件状态和docIdList
            updateFileStatus(fileResponse.getUid(), docIdList);
            
        } catch (Exception e) {
            log.error("小文件同步处理失败: {}", e.getMessage(), e);
            markFileAsFailed(fileResponse.getUid(), e.getMessage());
        }
    }
    
    /**
     * 处理大文件（异步）
     */
    private void processLargeFileAsync(List<Document> documents, FileResponse fileResponse) {
        try {
            // 使用异步批处理
            fileChunkService.processFileChunksAsync(documents, fileResponse)
                .thenAccept(docIdList -> {
                    log.info("大文件异步处理完成: {}, 生成chunks: {}", fileResponse.getFileName(), docIdList.size());
                    updateFileStatus(fileResponse.getUid(), docIdList);
                })
                .exceptionally(throwable -> {
                    log.error("大文件异步处理失败: {}", throwable.getMessage(), throwable);
                    markFileAsFailed(fileResponse.getUid(), throwable.getMessage());
                    return null;
                });
                
            log.info("大文件已提交异步处理: {}", fileResponse.getFileName());
            
        } catch (Exception e) {
            log.error("提交大文件异步处理失败: {}", e.getMessage(), e);
            markFileAsFailed(fileResponse.getUid(), e.getMessage());
        }
    }
    
    /**
     * 更新文件状态为成功
     */
    private void updateFileStatus(String fileUid, List<String> docIdList) {
        Optional<FileEntity> fileEntity = fileRestService.findByUid(fileUid);
        if (fileEntity.isPresent()) {
            FileEntity file = fileEntity.get();
            file.setSuccess();
            file.setDocIdList(docIdList);
            fileRestService.save(file);
            log.info("文件处理成功: {}, chunks: {}", file.getFileName(), docIdList.size());
        } else {
            log.warn("文件实体不存在: {}", fileUid);
        }
    }
    
    /**
     * 标记文件处理失败
     */
    private void markFileAsFailed(String fileUid, String errorMessage) {
        Optional<FileEntity> fileEntity = fileRestService.findByUid(fileUid);
        if (fileEntity.isPresent()) {
            FileEntity file = fileEntity.get();
            file.setError();
            // 如果有错误信息字段，可以设置
            fileRestService.save(file);
            log.error("文件处理失败: {}, 错误: {}", file.getFileName(), errorMessage);
        } else {
            log.warn("标记失败时文件实体不存在: {}", fileUid);
        }
    }
    
    /**
     * 判断是否应该使用异步处理
     * 基于文档数量和内容复杂度
     */
    private boolean shouldUseAsyncProcessing(List<Document> documents) {
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


}

