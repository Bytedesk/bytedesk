/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:45:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 11:05:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text.vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.kbase.config.KbaseConst;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;
import com.bytedesk.kbase.llm_text.TextEntity;
import com.bytedesk.kbase.llm_text.TextRequest;
import com.bytedesk.kbase.llm_text.TextRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Text向量检索服务
 * 用于处理Text的向量存储和相似度搜索
 * @author jackning
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TextVectorService {
    
    private final ElasticsearchVectorStore vectorStore;
    
    private final TextRestService textRestService;
    
    /**
     * 将文本内容添加到向量存储中
     * @param text 文本实体
     */
    @Transactional
    public void indexTextVector(TextEntity text) {
        log.info("向量索引文本: {}", text.getTitle());
        
        try {
            // 1. 为标题和内容创建文档（带有元数据）
            String id = "text_" + text.getUid();
            // 将标题和内容合并，以便一起索引，确保标题具有更大的权重
            String content = text.getTitle() + "\n\n" + text.getContent();
            
            // 处理标签，将其转化为字符串便于索引
            String tags = String.join(",", text.getTagList());
            
            // 元数据
            Map<String, Object> metadata = Map.of(
                "uid", text.getUid(),
                "title", text.getTitle(),
                KbaseConst.KBASE_KB_UID, text.getKbase() != null ? text.getKbase().getUid() : "",
                "categoryUid", text.getCategoryUid() != null ? text.getCategoryUid() : "",
                "orgUid", text.getOrgUid(),
                "enabled", Boolean.toString(text.getEnabled()),
                "tags", tags,
                "type", text.getType()
            );
            
            // 创建文档
            Document document = new Document(id, content, metadata);
            
            // 2. 添加到向量存储
            // 检查是否已存在该文档ID
            checkAndDeleteExistingDoc(id);
            
            // 添加新文档
            vectorStore.add(List.of(document));
            
            // 3. 更新Text实体中的文档ID列表
            List<String> docIdList = text.getDocIdList();
            if (docIdList == null) {
                docIdList = new ArrayList<>();
            }
            if (!docIdList.contains(id)) {
                docIdList.add(id);
                text.setDocIdList(docIdList);
                
                // 设置向量索引状态为成功
                text.setVectorStatus(ChunkStatusEnum.SUCCESS.name());
                
                // 更新Text实体
                textRestService.save(text);
            }
            
            log.info("文本向量索引成功: {}", text.getTitle());
        } catch (Exception e) {
            log.error("文本向量索引失败: {}, 错误: {}", text.getTitle(), e.getMessage());
            
            // 设置向量索引状态为失败
            text.setVectorStatus(ChunkStatusEnum.ERROR.name());
            textRestService.save(text);
            
            throw e;
        }
    }
    
    /**
     * 更新文本的向量索引
     * @param request 文本请求对象
     */
    public void updateVectorIndex(TextRequest request) {
        Optional<TextEntity> textOpt = textRestService.findByUid(request.getUid());
        if (textOpt.isPresent()) {
            TextEntity text = textOpt.get();
            // 删除旧的向量索引
            deleteTextVector(text);
            // 创建新的向量索引
            indexTextVector(text);
        } else {
            log.warn("未找到要更新向量索引的文本: {}", request.getUid());
        }
    }
    
    /**
     * 更新所有文本的向量索引
     * @param request 文本请求对象，包含知识库ID
     */
    public void updateAllVectorIndex(TextRequest request) {
        List<TextEntity> textList = textRestService.findByKbUid(request.getKbUid());
        textList.forEach(text -> {
            try {
                // 删除旧的向量索引
                deleteTextVector(text);
                // 创建新的向量索引
                indexTextVector(text);
            } catch (Exception e) {
                log.error("更新文本向量索引失败: {}, 错误: {}", text.getTitle(), e.getMessage());
            }
        });
    }
    
    /**
     * 从向量存储中删除文本
     * @param text 要删除的文本实体
     * @return 是否删除成功
     */
    public Boolean deleteTextVector(TextEntity text) {
        log.info("从向量索引中删除文本: {}", text.getTitle());
        
        try {
            // 获取文档ID列表
            List<String> docIdList = text.getDocIdList();
            if (docIdList != null && !docIdList.isEmpty()) {
                // 删除所有关联的向量文档
                vectorStore.delete(docIdList);
                
                // 清空文档ID列表并更新状态
                text.setDocIdList(new ArrayList<>());
                text.setVectorStatus(ChunkStatusEnum.NEW.name());
                textRestService.save(text);
                
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("从向量存储中删除文本失败: {}, 错误: {}", text.getTitle(), e.getMessage());
            return false;
        }
    }
    
    /**
     * 在向量存储中进行语义搜索
     * @param query 搜索关键词
     * @param kbUid 知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid 组织UID（可选）
     * @param limit 返回结果数量限制
     * @return 相似度搜索结果列表
     */
    public List<TextVectorSearchResult> searchTextVector(String query, String kbUid, String categoryUid, String orgUid, int limit) {
        log.info("向量搜索文本: query={}, kbUid={}, categoryUid={}, orgUid={}", query, kbUid, categoryUid, orgUid);
        
        // 创建过滤表达式构建器
        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
        
        // 构建查询条件
        FilterExpressionBuilder.Op enabledOp = expressionBuilder.eq("enabled", "true");
        
        // 添加可选的过滤条件
        FilterExpressionBuilder.Op finalOp = enabledOp;
        
        if (kbUid != null && !kbUid.isEmpty()) {
            FilterExpressionBuilder.Op kbUidOp = expressionBuilder.eq(KbaseConst.KBASE_KB_UID, kbUid);
            finalOp = expressionBuilder.and(finalOp, kbUidOp);
        }
        
        if (categoryUid != null && !categoryUid.isEmpty()) {
            FilterExpressionBuilder.Op categoryUidOp = expressionBuilder.eq("categoryUid", categoryUid);
            finalOp = expressionBuilder.and(finalOp, categoryUidOp);
        }
        
        if (orgUid != null && !orgUid.isEmpty()) {
            FilterExpressionBuilder.Op orgUidOp = expressionBuilder.eq("orgUid", orgUid);
            finalOp = expressionBuilder.and(finalOp, orgUidOp);
        }
        
        // 构建最终的过滤表达式
        Expression expression = finalOp.build();
        
        // 构建搜索请求
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .filterExpression(expression)
                .topK(limit) // 限制返回的结果数量
                .build();
        
        // 执行相似度搜索
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
        
        // 解析结果
        List<TextVectorSearchResult> resultList = new ArrayList<>();
        
        for (Document doc : similarDocuments) {
            // 从文档元数据构建 TextVector 对象
            Map<String, Object> metadata = doc.getMetadata();
            String docUid = (String) metadata.getOrDefault("uid", "");
            String docTitle = (String) metadata.getOrDefault("title", "");
            String docContent = doc.getText();
            String docType = (String) metadata.getOrDefault("type", "");
            String docKbUid = (String) metadata.getOrDefault(KbaseConst.KBASE_KB_UID, "");
            String docCategoryUid = (String) metadata.getOrDefault("categoryUid", "");
            String docOrgUid = (String) metadata.getOrDefault("orgUid", "");
            
            // 从标签字符串还原为列表
            String tagsStr = (String) metadata.getOrDefault("tags", "");
            List<String> tagList = new ArrayList<>();
            if (tagsStr != null && !tagsStr.isEmpty()) {
                tagList = List.of(tagsStr.split(","));
            }
            
            // 构建 TextVector 对象
            TextVector textVector = TextVector.builder()
                .uid(docUid)
                .title(docTitle)
                .content(docContent)
                .type(docType)
                .kbUid(docKbUid)
                .categoryUid(docCategoryUid)
                .orgUid(docOrgUid)
                .tagList(tagList)
                .build();
            
            // 构建搜索结果对象
            TextVectorSearchResult result = TextVectorSearchResult.builder()
                .textVector(textVector)
                .score(doc.getScore().floatValue()) // 获取相似度分数
                .distance((float)(1.0 - doc.getScore())) // 根据相似度计算距离（简化实现）
                .highlightedTitle(docTitle) // 基础实现，实际应用中可能需要真正的高亮处理
                .highlightedContent(docContent) // 基础实现，实际应用中可能需要真正的高亮处理
                .build();
            
            resultList.add(result);
        }
        
        return resultList;
    }
    
    /**
     * 检查并删除已存在的文档
     * @param docId 文档ID
     */
    private void checkAndDeleteExistingDoc(String docId) {
        try {
            // 使用过滤表达式查询已存在的文档
            FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
            FilterExpressionBuilder.Op idOp = expressionBuilder.eq("id", docId);
            Expression expression = idOp.build();
            
            // 构建搜索请求，只查询是否存在，不关心内容
            SearchRequest searchRequest = SearchRequest.builder()
                    .query("")
                    .filterExpression(expression)
                    .build();
            
            List<Document> existingDocs = vectorStore.similaritySearch(searchRequest);
            
            // 如果文档存在，则删除
            if (existingDocs != null && !existingDocs.isEmpty()) {
                log.info("删除已存在的文本向量文档: {}", docId);
                vectorStore.delete(List.of(docId));
            }
        } catch (Exception e) {
            log.warn("检查文档存在性时出错: {}, 错误: {}", docId, e.getMessage());
            // 安全起见，尝试直接删除
            try {
                vectorStore.delete(List.of(docId));
            } catch (Exception ex) {
                log.warn("删除可能存在的文档时出错: {}", ex.getMessage());
            }
        }
    }
}
