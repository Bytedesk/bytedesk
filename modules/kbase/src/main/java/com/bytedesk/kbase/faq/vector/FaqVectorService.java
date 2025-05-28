/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:20:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-20 11:08:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq.vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.kbase.config.KbaseConst;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.FaqRequest;
import com.bytedesk.kbase.faq.FaqRestService;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FAQ向量检索服务
 * 用于处理FAQ的向量存储和相似度搜索
 * @author jackning
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FaqVectorService {
    
    private final ElasticsearchVectorStore vectorStore;
    
    private final FaqRestService faqRestService;

    @Qualifier("bytedeskOllamaEmbeddingModel") 
    private final EmbeddingModel embeddingModel;
    
    /**
     * 将FAQ内容添加到向量存储中
     * @param faq FAQ实体
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void indexFaqVector(FaqEntity faq) {
        String faqUid = faq.getUid();
        log.info("开始向量索引FAQ: {}, ID: {}, 初始状态: {}", faq.getQuestion(), faqUid, faq.getVectorStatus());
        
        try {
            // 先从数据库获取最新状态的实体
            Optional<FaqEntity> freshFaqOpt = faqRestService.findByUid(faqUid);
            if (freshFaqOpt.isEmpty()) {
                log.warn("无法找到要创建向量的FAQ实体: {}", faqUid);
                throw new RuntimeException("FAQ实体不存在: " + faqUid);
            }
            
            FaqEntity freshFaq = freshFaqOpt.get();
            log.info("获取到最新的FAQ实体: {}, 状态: {}, 文档ID列表: {}", 
                    freshFaq.getUid(), freshFaq.getVectorStatus(), freshFaq.getDocIdList());
            
            // 1. 为问题和答案创建文档（带有元数据）
            String id = "faq_" + freshFaq.getUid();
            String content = freshFaq.getQuestion() + "\n" + freshFaq.getAnswer();
            
            // 处理标签，将其转化为字符串便于索引
            String tags = String.join(",", freshFaq.getTagList());
            
            // 元数据
            Map<String, Object> metadata = Map.of(
                "uid", freshFaq.getUid(),
                "question", freshFaq.getQuestion(),
                KbaseConst.KBASE_KB_UID, freshFaq.getKbase() != null ? freshFaq.getKbase().getUid() : "",
                "categoryUid", freshFaq.getCategoryUid() != null ? freshFaq.getCategoryUid() : "",
                "orgUid", freshFaq.getOrgUid(),
                "enabled", Boolean.toString(freshFaq.getEnabled()),
                "tags", tags
            );
            
            // 创建文档
            Document document = new Document(id, content, metadata);
            
            // 2. 添加到向量存储
            // 检查是否已存在该文档ID
            log.info("检查并删除可能存在的文档: {}", id);
            checkAndDeleteExistingDoc(id);
            
            log.info("向向量存储添加文档: {}", id);
            // 添加新文档
            vectorStore.add(List.of(document));
            log.info("向量存储添加文档成功: {}", id);
            
            // 3. 更新FAQ实体中的文档ID列表
            List<String> docIdList = freshFaq.getDocIdList();
            if (docIdList == null) {
                docIdList = new ArrayList<>();
            }
            
            // 无论是否已存在，都更新状态并保存
            docIdList.add(id);
            freshFaq.setDocIdList(docIdList);
            
            // 设置向量索引状态为成功
            log.info("设置FAQ向量状态为成功: {} -> {}", freshFaq.getVectorStatus(), ChunkStatusEnum.SUCCESS.name());
            freshFaq.setVectorStatus(ChunkStatusEnum.SUCCESS.name());
            
            // 更新FAQ实体
            log.info("保存FAQ实体更新，设置向量索引状态为成功: {}, 文档ID数量: {}", 
                    freshFaq.getUid(), freshFaq.getDocIdList().size());
            FaqEntity savedFaq = faqRestService.save(freshFaq);
            
            // 确认保存后状态
            log.info("FAQ向量索引保存成功确认：{}, 状态: {}, 文档ID列表: {}", 
                    savedFaq.getUid(), savedFaq.getVectorStatus(), savedFaq.getDocIdList());
            
            // 更新原始传入的对象，确保调用者也能看到变化
            if (faq != null) {
                faq.setVectorStatus(ChunkStatusEnum.SUCCESS.name());
                faq.setDocIdList(docIdList);
            }
            
        } catch (Exception e) {
            log.error("FAQ向量索引失败: {}, 错误: {}", faqUid, e.getMessage(), e);
            
            try {
                // 重新获取实体以确保我们修改的是最新版本
                Optional<FaqEntity> errorFaqOpt = faqRestService.findByUid(faqUid);
                if (errorFaqOpt.isPresent()) {
                    FaqEntity errorFaq = errorFaqOpt.get();
                    // 设置向量索引状态为失败
                    log.warn("设置FAQ向量状态为错误: {} -> {}", errorFaq.getVectorStatus(), ChunkStatusEnum.ERROR.name());
                    errorFaq.setVectorStatus(ChunkStatusEnum.ERROR.name());
                    FaqEntity savedErrorFaq = faqRestService.save(errorFaq);
                    log.info("保存FAQ错误状态成功: {}, 新状态: {}", savedErrorFaq.getUid(), savedErrorFaq.getVectorStatus());
                    
                    // 更新原始传入的对象
                    if (faq != null) {
                        faq.setVectorStatus(ChunkStatusEnum.ERROR.name());
                    }
                } else {
                    log.warn("无法找到FAQ实体进行错误状态更新: {}", faqUid);
                }
            } catch (Exception saveEx) {
                log.error("更新FAQ向量索引错误状态失败: {}, 错误: {}", faqUid, saveEx.getMessage());
            }
            
            throw e;
        }
    }
    
    /**
     * 更新FAQ的向量索引
     * @param request FAQ请求对象
     */
    public void updateVectorIndex(FaqRequest request) {
        Optional<FaqEntity> faqOpt = faqRestService.findByUid(request.getUid());
        if (faqOpt.isPresent()) {
            FaqEntity faq = faqOpt.get();
            // 删除旧的向量索引
            deleteFaqVector(faq);
            // 创建新的向量索引
            indexFaqVector(faq);
        } else {
            log.warn("未找到要更新向量索引的FAQ: {}", request.getUid());
        }
    }
    
    /**
     * 更新所有FAQ的向量索引
     * @param request FAQ请求对象，包含知识库ID
     */
    public void updateAllVectorIndex(FaqRequest request) {
        List<FaqEntity> faqList = faqRestService.findByKbUid(request.getKbUid());
        faqList.forEach(faq -> {
            try {
                // 删除旧的向量索引
                deleteFaqVector(faq);
                // 创建新的向量索引
                indexFaqVector(faq);
            } catch (Exception e) {
                log.error("更新FAQ向量索引失败: {}, 错误: {}", faq.getQuestion(), e.getMessage());
            }
        });
    }
    
    /**
     * 从向量存储中删除FAQ
     * @param faq 要删除的FAQ实体
     * @return 是否删除成功
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public Boolean deleteFaqVector(FaqEntity faq) {
        String faqUid = faq.getUid();
        log.info("从向量索引中删除FAQ: {}, 文档ID列表: {}", faq.getQuestion(), faq.getDocIdList());
        
        try {
            // 先从数据库获取最新状态
            Optional<FaqEntity> freshFaqOpt = faqRestService.findByUid(faqUid);
            if (freshFaqOpt.isEmpty()) {
                log.warn("无法找到要删除向量的FAQ实体: {}", faqUid);
                return false;
            }
            
            FaqEntity freshFaq = freshFaqOpt.get();
            log.info("获取到最新的FAQ实体用于删除向量: {}, 状态: {}, 文档ID列表: {}", 
                    freshFaq.getUid(), freshFaq.getVectorStatus(), freshFaq.getDocIdList());
            
            // 获取文档ID列表
            List<String> docIdList = freshFaq.getDocIdList();
            if (docIdList != null && !docIdList.isEmpty()) {
                try {
                    // 删除所有关联的向量文档
                    log.info("删除向量文档: {}", docIdList);
                    vectorStore.delete(docIdList);
                    log.info("成功从向量存储中删除文档: {}", docIdList);
                } catch (Exception e) {
                    log.warn("从向量存储删除文档时出错，但将继续处理: {}, 错误: {}", docIdList, e.getMessage());
                    // 即使删除失败，我们仍继续更新状态，因为重要的是将状态重置为NEW
                }
                
                // 清空文档ID列表并更新状态
                freshFaq.setDocIdList(new ArrayList<>());
                log.info("设置FAQ向量状态为NEW: {} -> {}", freshFaq.getVectorStatus(), ChunkStatusEnum.NEW.name());
                freshFaq.setVectorStatus(ChunkStatusEnum.NEW.name());
                
                log.info("更新FAQ状态为NEW，清空文档ID列表: {}", freshFaq.getUid());
                FaqEntity savedFaq = faqRestService.save(freshFaq);
                log.info("保存FAQ删除向量后状态成功: {}, 新状态: {}, 文档ID列表: {}", 
                        savedFaq.getUid(), savedFaq.getVectorStatus(), savedFaq.getDocIdList());
                
                // 更新原始传入的对象，确保调用者能看到变化
                if (faq != null) {
                    faq.setDocIdList(new ArrayList<>());
                    faq.setVectorStatus(ChunkStatusEnum.NEW.name());
                }
                
                return true;
            } else {
                log.info("FAQ没有关联的向量文档，无需删除向量存储: {}", freshFaq.getUid());
                
                // 仍然将状态设置为NEW，以便后续可以创建新的向量索引
                if (!ChunkStatusEnum.NEW.name().equals(freshFaq.getVectorStatus())) {
                    log.info("更新FAQ向量状态为NEW: {} -> {}", freshFaq.getVectorStatus(), ChunkStatusEnum.NEW.name());
                    freshFaq.setVectorStatus(ChunkStatusEnum.NEW.name());
                    FaqEntity savedFaq = faqRestService.save(freshFaq);
                    log.info("保存FAQ更新状态成功: {}, 新状态: {}", savedFaq.getUid(), savedFaq.getVectorStatus());
                    
                    // 更新原始传入的对象
                    if (faq != null) {
                        faq.setVectorStatus(ChunkStatusEnum.NEW.name());
                    }
                } else {
                    log.info("FAQ向量状态已经是NEW，无需更新: {}", freshFaq.getUid());
                }
                
                return true;
            }
        } catch (Exception e) {
            log.error("从向量存储中删除FAQ失败: {}, 错误: {}", faq.getQuestion(), e.getMessage(), e);
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
    public List<FaqVectorSearchResult> searchFaqVector(String query, String kbUid, String categoryUid, String orgUid, int limit) {
        log.info("向量搜索FAQ: query={}, kbUid={}, categoryUid={}, orgUid={}", query, kbUid, categoryUid, orgUid);
        
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
        List<FaqVectorSearchResult> resultList = new ArrayList<>();
        
        for (Document doc : similarDocuments) {
            // 从文档中提取元数据
            Map<String, Object> metadata = doc.getMetadata();
            String uid = (String) metadata.getOrDefault("uid", "");
            
            // 1. 通过UID查找对应的FAQ实体，以便获取完整信息
            Optional<FaqEntity> faqEntityOpt = faqRestService.findByUid(uid);
            if (faqEntityOpt.isPresent()) {
                FaqEntity faqEntity = faqEntityOpt.get();
                
                // 2. 将FaqEntity转换为FaqVector
                FaqVector faqVector = FaqVector.fromFaqEntity(faqEntity);
                
                // 3. 创建搜索结果对象
                FaqVectorSearchResult result = FaqVectorSearchResult.builder()
                    .faqVector(faqVector)
                    .score(doc.getScore().floatValue())
                    .highlightedQuestion(faqVector.getQuestion()) // 如果需要高亮可以在这里处理
                    .distance((float) (1.0 - doc.getScore().doubleValue())) // 将相似度转换为距离，距离 = 1 - 相似度
                    .build();
                
                resultList.add(result);
            } else {
                // 如果找不到对应的FAQ实体，尝试从文档元数据构建一个简化的FaqVector
                FaqVector simpleFaqVector = createSimpleFaqVectorFromDocument(doc);
                
                FaqVectorSearchResult result = FaqVectorSearchResult.builder()
                    .faqVector(simpleFaqVector)
                    .score(doc.getScore().floatValue())
                    .highlightedQuestion((String) metadata.getOrDefault("question", ""))
                    .distance((float) (1.0 - doc.getScore().doubleValue())) // 同上
                    .build();
                
                resultList.add(result);
            }
        }
        
        return resultList;
    }
    
    /**
     * 从文档创建简化版的FaqVector对象
     * 用于在找不到对应的FAQ实体时提供基础信息
     * @param doc 文档对象
     * @return 简化的FaqVector对象
     */
    private FaqVector createSimpleFaqVectorFromDocument(Document doc) {
        Map<String, Object> metadata = doc.getMetadata();
        
        // 提取元数据
        String uid = (String) metadata.getOrDefault("uid", "");
        String question = (String) metadata.getOrDefault("question", "");
        String content = doc.getText();
        String answer = "";
        
        // 从内容中提取答案（通常问题和答案用换行符分隔）
        if (content != null && content.contains("\n")) {
            String[] parts = content.split("\n", 2);
            answer = parts.length > 1 ? parts[1] : "";
        }
        
        // 处理标签
        String tagsStr = (String) metadata.getOrDefault("tags", "");
        List<String> tagList = new ArrayList<>();
        if (tagsStr != null && !tagsStr.isEmpty()) {
            tagList = List.of(tagsStr.split(","));
        }
        
        // 创建FaqVector对象
        return FaqVector.builder()
            .uid(uid)
            .question(question)
            .answer(answer)
            .similarQuestions(new ArrayList<>()) // 空列表，因为文档中可能没有这个信息
            .tagList(tagList)
            .orgUid((String) metadata.getOrDefault("orgUid", ""))
            .kbUid((String) metadata.getOrDefault(KbaseConst.KBASE_KB_UID, ""))
            .categoryUid((String) metadata.getOrDefault("categoryUid", ""))
            .enabled(Boolean.parseBoolean((String) metadata.getOrDefault("enabled", "true")))
            .build();
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
                log.info("删除已存在的FAQ向量文档: {}", docId);
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
