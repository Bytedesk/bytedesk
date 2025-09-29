/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-28 15:33:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-29 10:27:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.ocr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.BdUploadUtils;
import com.bytedesk.kbase.llm_chunk.ChunkRequest;
import com.bytedesk.kbase.llm_chunk.ChunkRestService;
import com.bytedesk.kbase.llm_chunk.ChunkTypeEnum;
import com.bytedesk.kbase.llm_file.FileEntity;
import com.bytedesk.kbase.llm_file.FileRequest;
import com.bytedesk.kbase.llm_file.FileResponse;
import com.bytedesk.kbase.llm_file.FileRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class OcrEventListener {

    private final RobotService robotService;
    
    private final FileRestService fileRestService;
    
    private final ChunkRestService chunkRestService;

    /**
     * 监听上传创建事件，判断是否图片类型，进行OCR识别
     * 
     * @param event 上传创建事件
     */
    @EventListener
    @Async
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        
        // 检查是否为LLM相关文件上传
        if (!UploadTypeEnum.LLM_FILE.name().equalsIgnoreCase(upload.getType()) 
            && !UploadTypeEnum.LLM.name().equalsIgnoreCase(upload.getType())) {
            log.debug("跳过非LLM类型文件: {}", upload.getType());
            return;
        }
        
        // 检查是否为图片文件
        if (!isImageFile(upload)) {
            log.debug("跳过非图片文件: {} ({})", upload.getFileName(), upload.getFileType());
            return;
        }
        
        // 获取组织UID，如果没有则使用默认值
        String orgUid = upload.getOrgUid();
        if (orgUid == null || orgUid.isEmpty()) {
            log.warn("UploadEntity缺少orgUid，跳过OCR处理: {}", upload.getFileName());
            return;
        }
        
        log.info("开始OCR处理图片: {} (URL: {})", upload.getFileName(), upload.getFileUrl());
        
        try {
            // 调用OCR服务
            String extractedText = robotService.ocrExtraction(upload.getFileUrl(), orgUid);
            
            if (extractedText != null && !extractedText.isEmpty()) {
                log.info("OCR提取成功，文件: {}，提取文字长度: {} 字符", 
                    upload.getFileName(), extractedText.length());
                
                log.debug("OCR提取结果预览: {}", 
                    extractedText.length() > 100 ? extractedText.substring(0, 100) + "..." : extractedText);
                
                // 将 OCR 提取的文字转换为 Documents 并存储到 chunks
                processOcrResultToChunks(extractedText, upload);
                
            } else {
                log.info("OCR提取结果为空，文件: {}", upload.getFileName());
            }
        } catch (Exception e) {
            log.error("OCR处理失败，文件: {} - 错误: {}", upload.getFileName(), e.getMessage(), e);
        }
    }

    /**
     * 判断是否为图片文件
     * 
     * @param upload 上传实体
     * @return 是否为图片文件
     */
    private boolean isImageFile(UploadEntity upload) {
        return BdUploadUtils.isImageFile(upload.getFileName(), upload.getFileType());
    }
    
    /**
     * 处理OCR结果，将提取的文字转换为Documents并存储到chunks
     * 
     * @param extractedText OCR提取的文字
     * @param upload 上传实体
     */
    private void processOcrResultToChunks(String extractedText, UploadEntity upload) {
        try {
            // 1. 首先需要确保文件记录已经创建
            FileResponse fileResponse = findOrCreateFileRecord(upload, extractedText);
            
            // 2. 将OCR文字转换为Document列表
            List<Document> documents = convertTextToDocuments(extractedText, upload);
            
            // 3. 创建chunks并存储
            List<String> chunkUids = createChunksFromDocuments(documents, fileResponse);
            
            // 4. 更新文件记录的docIdList
            updateFileDocIdList(fileResponse.getUid(), chunkUids);
            
            log.info("OCR结果成功转换为chunks，文件: {}，创建chunks数量: {}", 
                upload.getFileName(), chunkUids.size());
                
        } catch (Exception e) {
            log.error("处理OCR结果到chunks失败，文件: {} - 错误: {}", upload.getFileName(), e.getMessage(), e);
        }
    }
    
    /**
     * 查找或创建文件记录
     */
    private FileResponse findOrCreateFileRecord(UploadEntity upload, String extractedText) {
        // 现在图片文件不会在 FileEventListener 中创建文件记录（已跳过），
        // 所以我们安全地为OCR结果创建文件记录
        UserProtobuf userProtobuf = UserProtobuf.fromJson(upload.getUser());
        
        // 构建包含OCR结果的文件内容
        String ocrContent = String.format("OCR提取结果:\n%s", 
            extractedText.length() > 60000 ? 
                extractedText.substring(0, 60000) + "...[OCR内容已截断，完整内容将在文档分块中处理]" : 
                extractedText);
        
        FileRequest fileRequest = FileRequest.builder()
            .uploadUid(upload.getUid())
            .fileName(upload.getFileName())  // 保持原文件名，不添加(OCR)后缀
            .fileUrl(upload.getFileUrl())
            .fileType("text/plain") // OCR结果作为文本类型
            .content(ocrContent)
            .categoryUid(upload.getCategoryUid())
            .kbUid(upload.getKbUid())
            .userUid(userProtobuf.getUid())
            .orgUid(upload.getOrgUid())
            .build();
            
        return fileRestService.create(fileRequest);
    }
    
    /**
     * 将OCR提取的文字转换为Document列表
     */
    private List<Document> convertTextToDocuments(String extractedText, UploadEntity upload) {
        List<Document> documents = new ArrayList<>();
        
        // 清理文本
        String cleanedText = cleanOcrText(extractedText);
        
        if (cleanedText != null && !cleanedText.trim().isEmpty()) {
            // 创建元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("source", "ocr");
            metadata.put("fileName", upload.getFileName());
            metadata.put("fileType", upload.getFileType());
            metadata.put("uploadUid", upload.getUid());
            
            // 创建初始Document
            Document initialDoc = new Document(cleanedText, metadata);
            documents.add(initialDoc);
            
            // 使用TokenTextSplitter进行分割
            try {
                TokenTextSplitter splitter = new TokenTextSplitter(2000, 200, 5, 10000, true);
                documents = splitter.split(documents);
                log.info("OCR文字分割完成，原始1个文档，分割后{}个文档", documents.size());
            } catch (Exception e) {
                log.warn("使用TokenTextSplitter分割失败，使用简单分割: {}", e.getMessage());
                documents = simpleTextSplit(cleanedText, metadata);
            }
        }
        
        return documents;
    }
    
    /**
     * 简单的文字分割方法（fallback）
     */
    private List<Document> simpleTextSplit(String text, Map<String, Object> metadata) {
        List<Document> documents = new ArrayList<>();
        
        int chunkSize = 2000;
        int overlap = 200;
        
        if (text.length() <= chunkSize) {
            documents.add(new Document(text, metadata));
        } else {
            for (int start = 0; start < text.length(); start += chunkSize - overlap) {
                int end = Math.min(start + chunkSize, text.length());
                String chunk = text.substring(start, end);
                
                Map<String, Object> chunkMetadata = new HashMap<>(metadata);
                chunkMetadata.put("chunk_index", documents.size());
                
                documents.add(new Document(chunk, chunkMetadata));
                
                if (end >= text.length()) break;
            }
        }
        
        return documents;
    }
    
    /**
     * 清理OCR文字
     */
    private String cleanOcrText(String text) {
        if (text == null) return "";
        
        return text
            // 移除控制字符（除了常见的换行、回车、制表符）
            .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", " ")
            // 规范化空白字符
            .replaceAll("\\s+", " ")
            // 移除可能的特殊Unicode字符
            .replaceAll("[\\uFFF0-\\uFFFF]", "")
            // 移除一些可能导致token编码问题的特殊字符
            .replaceAll("[\\uE000-\\uF8FF]", "") // 私用区字符
            .trim();
    }
    
    /**
     * 从Documents创建Chunk记录
     */
    private List<String> createChunksFromDocuments(List<Document> documents, FileResponse fileResponse) {
        List<String> chunkUids = new ArrayList<>();
        
        for (Document doc : documents) {
            String content = doc.getText();
            
            // 过滤掉过短或无意义的内容
            if (!isValidOcrContent(content)) {
                log.debug("跳过无效OCR内容: {}", content.substring(0, Math.min(50, content.length())));
                continue;
            }
            
            // 创建Chunk记录
            ChunkRequest chunkRequest = ChunkRequest.builder()
                .name(fileResponse.getFileName())  // 使用原文件名，不添加(OCR)后缀
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
                log.debug("创建OCR chunk成功: {}", chunkResponse.getUid());
            } catch (Exception e) {
                log.error("创建OCR chunk失败: {}", e.getMessage(), e);
            }
        }
        
        return chunkUids;
    }
    
    /**
     * 验证OCR内容是否有效
     */
    private boolean isValidOcrContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = content.trim();
        
        // OCR内容相对宽松一些，最少50个字符
        if (trimmed.length() < 50) {
            return false;
        }
        
        // 检查是否包含足够的有意义字符（至少5个单词或字符组合）
        String[] words = trimmed.split("\\s+");
        if (words.length < 5) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 更新文件记录的docIdList
     */
    private void updateFileDocIdList(String fileUid, List<String> chunkUids) {
        try {
            Optional<FileEntity> fileEntity = fileRestService.findByUid(fileUid);
            if (fileEntity.isPresent()) {
                FileEntity file = fileEntity.get();
                file.setDocIdList(chunkUids);
                fileRestService.save(file);
                log.info("更新文件docIdList成功: {}, chunks数量: {}", file.getFileName(), chunkUids.size());
            } else {
                log.warn("更新docIdList时找不到文件实体: {}", fileUid);
            }
        } catch (Exception e) {
            log.error("更新文件docIdList失败: {}, 错误: {}", fileUid, e.getMessage(), e);
        }
    }
    
}
