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
    @Transactional
    public void indexFaqVector(FaqEntity faq) {
        log.info("开始向量索引FAQ: {}, ID: {}", faq.getQuestion(), faq.getUid());
        
        // 在处理前先获取最新的FAQ实体
        Optional<FaqEntity> currentFaqOpt = faqRestService.findByUid(faq.getUid());
        if (!currentFaqOpt.isPresent()) {
            log.error("FAQ实体不存在，无法创建向量索引: {}", faq.getUid());
            throw new RuntimeException("FAQ实体不存在: " + faq.getUid());
        }
        
        FaqEntity currentFaq = currentFaqOpt.get();
        log.info("获取到最新FAQ实体，当前向量状态: {}, ID: {}", currentFaq.getVectorStatus(), currentFaq.getUid());
        
        try {
            // 1. 为问题和答案创建文档（带有元数据）
            String id = "faq_" + currentFaq.getUid();
            String content = currentFaq.getQuestion() + "\n" + currentFaq.getAnswer();
            
            // 处理标签，将其转化为字符串便于索引
            String tags = String.join(",", currentFaq.getTagList());
            
            // 元数据
            Map<String, Object> metadata = Map.of(
                "uid", currentFaq.getUid(),
                "question", currentFaq.getQuestion(),
                KbaseConst.KBASE_KB_UID, currentFaq.getKbase() != null ? currentFaq.getKbase().getUid() : "",
                "categoryUid", currentFaq.getCategoryUid() != null ? currentFaq.getCategoryUid() : "",
                "orgUid", currentFaq.getOrgUid(),
                "enabled", Boolean.toString(currentFaq.getEnabled()),
                "tags", tags
            );
            
            // 创建文档
            Document document = new Document(id, content, metadata);
            
            // 2. 添加到向量存储
            // 检查是否已存在该文档ID并删除
            try {
                checkAndDeleteExistingDoc(id);
                log.info("已检查并删除存在的文档(如果有): {}", id);
            } catch (Exception e) {
                log.warn("检查和删除现有文档时出错，继续处理: {}, 错误: {}", id, e.getMessage());
            }
            
            // 添加新文档到向量存储
            log.info("向向量存储添加文档: {}", id);
            vectorStore.add(List.of(document));
            log.info("已成功添加文档到向量存储: {}", id);
            
            // 3. 更新FAQ实体中的文档ID列表
            List<String> docIdList = currentFaq.getDocIdList();
            if (docIdList == null) {
                docIdList = new ArrayList<>();
            } else if (docIdList.contains(id)) {
                // 如果已包含该ID，先移除再添加以确保唯一性
                docIdList.remove(id);
                log.info("从文档ID列表中移除重复ID: {}", id);
            }
            
            // 添加文档ID并更新状态
            docIdList.add(id);
            currentFaq.setDocIdList(docIdList);
            
            // 设置向量索引状态为成功
            currentFaq.setVectorStatus(ChunkStatusEnum.SUCCESS.name());
            
            // 更新FAQ实体 - 使用明确的事务保证状态更新和保存原子性
            log.info("准备保存FAQ实体更新，设置向量索引状态为成功: {}", currentFaq.getUid());
            
            // 确保isDeleted标志为false，防止触发删除事件
            if (currentFaq.isDeleted()) {
                log.warn("FAQ实体标记为已删除，重置isDeleted标志: {}", currentFaq.getUid());
                currentFaq.setDeleted(false);
            }
            
            // 强制刷新实体状态并确保事务提交
            FaqEntity savedFaq = updateFaqEntityStatus(currentFaq, ChunkStatusEnum.SUCCESS.name(), docIdList);
            
            // 确认保存成功
            if (ChunkStatusEnum.SUCCESS.name().equals(savedFaq.getVectorStatus())) {
                log.info("FAQ向量索引成功并确认状态已更新: {}, 文档ID: {}", currentFaq.getQuestion(), savedFaq.getDocIdList());
            } else {
                log.warn("FAQ实体已保存但向量状态未正确更新: {}, 当前状态: {}", savedFaq.getUid(), savedFaq.getVectorStatus());
            }
            
        } catch (Exception e) {
            log.error("FAQ向量索引失败: {}, 错误: {}", currentFaq.getQuestion(), e.getMessage(), e);
            
            // 设置向量索引状态为失败
            currentFaq.setVectorStatus(ChunkStatusEnum.ERROR.name());
            try {
                log.info("保存FAQ实体更新，设置向量索引状态为失败: {}", currentFaq.getUid());
                faqRestService.save(currentFaq);
            } catch (Exception saveEx) {
                log.error("更新FAQ向量索引状态失败: {}, 错误: {}", currentFaq.getUid(), saveEx.getMessage());
            }
            
            throw new RuntimeException("创建向量索引失败: " + e.getMessage(), e);
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
    /**
     * 单独事务更新FAQ实体状态
     * 确保状态更新能够正确提交到数据库
     * 
     * @param faq FAQ实体
     * @param status 要设置的向量状态
     * @param docIdList 文档ID列表
     * @return 更新后的FAQ实体
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public FaqEntity updateFaqEntityStatus(FaqEntity faq, String status, List<String> docIdList) {
        log.info("在单独事务中更新FAQ实体状态: {}, 新状态: {}", faq.getUid(), status);
        
        // 使用新的查询获取实体，确保在新事务中最新的状态
        Optional<FaqEntity> latestFaqOpt = faqRestService.findByUid(faq.getUid());
        if (!latestFaqOpt.isPresent()) {
            log.warn("找不到要更新的FAQ实体: {}", faq.getUid());
            // 如果找不到，还是尝试保存传入的实体
            faq.setVectorStatus(status);
            faq.setDocIdList(docIdList);
            return faqRestService.save(faq);
        }
        
        // 获取最新实体并更新状态
        FaqEntity latestFaq = latestFaqOpt.get();
        latestFaq.setVectorStatus(status);
        latestFaq.setDocIdList(docIdList);
        
        // 确保isDeleted标志为false，避免触发删除事件
        latestFaq.setDeleted(false);
        
        // 保存并返回更新后的实体
        FaqEntity savedFaq = faqRestService.save(latestFaq);
        log.info("FAQ实体状态已更新: {}, 状态: {}, 文档ID数量: {}", 
                savedFaq.getUid(), 
                savedFaq.getVectorStatus(), 
                savedFaq.getDocIdList() != null ? savedFaq.getDocIdList().size() : 0);
                
        return savedFaq;
    }
    
    @Transactional
    public Boolean deleteFaqVector(FaqEntity faq) {
        log.info("从向量索引中删除FAQ: {}, ID: {}", faq.getQuestion(), faq.getUid());
        
        // 获取最新状态的FAQ实体
        Optional<FaqEntity> currentFaqOpt = faqRestService.findByUid(faq.getUid());
        if (!currentFaqOpt.isPresent()) {
            log.error("FAQ实体不存在，无法删除向量索引: {}", faq.getUid());
            return false;
        }
        
        FaqEntity currentFaq = currentFaqOpt.get();
        
        // 添加去重逻辑：如果状态已经是NEW且没有文档ID，说明已处理过，直接返回
        if (ChunkStatusEnum.NEW.name().equals(currentFaq.getVectorStatus()) &&
            (currentFaq.getDocIdList() == null || currentFaq.getDocIdList().isEmpty())) {
            log.info("FAQ已处理过删除操作，避免重复处理: {}", currentFaq.getUid());
            return true;
        }
        
        log.info("获取到最新FAQ实体，当前向量状态: {}, 文档ID列表: {}", currentFaq.getVectorStatus(), currentFaq.getDocIdList());
        
        try {
            // 获取文档ID列表
            List<String> docIdList = currentFaq.getDocIdList();
            if (docIdList != null && !docIdList.isEmpty()) {
                log.info("准备删除FAQ向量文档，文档ID数量: {}", docIdList.size());
                
                // 尝试删除所有关联的向量文档
                try {
                    vectorStore.delete(docIdList);
                    log.info("已从向量存储中删除文档，数量: {}", docIdList.size());
                } catch (Exception e) {
                    log.warn("删除向量文档时出现错误，将继续更新状态: {}, 错误: {}", currentFaq.getUid(), e.getMessage());
                }
                
                // 清空文档ID列表并更新状态，确保delete标志不会触发新的删除操作
                currentFaq.setDocIdList(new ArrayList<>());
                currentFaq.setVectorStatus(ChunkStatusEnum.NEW.name());
                
                // 记录原始删除标志，确保不会重复触发删除循环
                boolean wasDeleted = currentFaq.isDeleted();
                if (wasDeleted) {
                    // 临时设置为false以避免触发删除事件
                    currentFaq.setDeleted(false);
                }
                
                // 保存更新后的FAQ实体
                FaqEntity savedFaq = faqRestService.save(currentFaq);
                log.info("已更新FAQ实体，清除文档ID列表并设置向量状态为NEW: {}, 原删除标志: {}", 
                          savedFaq.getUid(), wasDeleted);
                
                // 检查状态是否成功更新
                if (ChunkStatusEnum.NEW.name().equals(savedFaq.getVectorStatus()) && 
                   (savedFaq.getDocIdList() == null || savedFaq.getDocIdList().isEmpty())) {
                    log.info("确认FAQ向量索引已成功删除: {}", savedFaq.getUid());
                    return true;
                } else {
                    log.warn("FAQ实体已保存但状态或文档ID列表未正确更新: {}, 当前状态: {}, 文档ID列表: {}", 
                             savedFaq.getUid(), savedFaq.getVectorStatus(), savedFaq.getDocIdList());
                    
                    // 尝试再次更新
                    savedFaq.setDocIdList(new ArrayList<>());
                    savedFaq.setVectorStatus(ChunkStatusEnum.NEW.name());
                    faqRestService.save(savedFaq);
                    log.info("已尝试再次更新FAQ实体状态: {}", savedFaq.getUid());
                    
                    return true;
                }
            } else {
                // 如果没有文档ID列表，检查状态是否需要更新
                if (!ChunkStatusEnum.NEW.name().equals(currentFaq.getVectorStatus())) {
                    currentFaq.setVectorStatus(ChunkStatusEnum.NEW.name());
                    faqRestService.save(currentFaq);
                    log.info("FAQ实体没有文档ID，已更新向量状态为NEW: {}", currentFaq.getUid());
                } else {
                    log.info("FAQ实体没有文档ID且状态已是NEW，无需删除操作: {}", currentFaq.getUid());
                }
                return true;
            }
        } catch (Exception e) {
            log.error("从向量存储中删除FAQ失败: {}, 错误: {}", currentFaq.getQuestion(), e.getMessage());
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
     * 增强版本：多次尝试并提供详细日志
     * @param docId 文档ID
     */
    private void checkAndDeleteExistingDoc(String docId) {
        // 验证输入
        if (docId == null || docId.isEmpty()) {
            log.warn("尝试检查空文档ID，跳过操作");
            return;
        }

        log.info("开始检查并删除文档是否存在: {}", docId);
        
        // 最大重试次数
        int maxRetries = 3;
        boolean deleteSuccess = false;
        Exception lastError = null;
        
        for (int attempt = 1; attempt <= maxRetries && !deleteSuccess; attempt++) {
            try {
                // 首先尝试使用过滤表达式查询已存在的文档
                FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
                FilterExpressionBuilder.Op idOp = expressionBuilder.eq("id", docId);
                Expression expression = idOp.build();
                
                // 构建搜索请求，只查询是否存在，不关心内容
                SearchRequest searchRequest = SearchRequest.builder()
                        .query("")
                        .filterExpression(expression)
                        .build();
                
                log.info("尝试第{}次查找文档: {}", attempt, docId);
                List<Document> existingDocs = vectorStore.similaritySearch(searchRequest);
                
                // 如果文档存在，则删除
                if (existingDocs != null && !existingDocs.isEmpty()) {
                    log.info("找到已存在的FAQ向量文档: {}, 数量: {}, 尝试删除", docId, existingDocs.size());
                    vectorStore.delete(List.of(docId));
                    log.info("成功删除FAQ向量文档: {}", docId);
                    deleteSuccess = true;
                } else {
                    log.info("未找到要删除的FAQ向量文档，可能是新文档: {}", docId);
                    deleteSuccess = true; // 如果文档不存在也算成功，因为不需要删除
                }
            } catch (Exception e) {
                lastError = e;
                log.warn("第{}次检查和删除文档时出错: {}, 错误: {}", attempt, docId, e.getMessage());
                
                if (attempt == maxRetries) {
                    // 最后一次尝试，直接强制删除，忽略错误
                    try {
                        log.info("最后一次尝试，直接强制删除文档: {}", docId);
                        vectorStore.delete(List.of(docId));
                        log.info("强制删除文档可能成功: {}", docId);
                        deleteSuccess = true;
                    } catch (Exception ex) {
                        log.warn("强制删除文档最终失败: {}, 错误: {}", docId, ex.getMessage());
                    }
                }
                
                // 短暂等待后重试
                try {
                    Thread.sleep(100 * attempt); // 100ms, 200ms, 300ms
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        if (deleteSuccess) {
            log.info("文档检查和删除操作最终成功: {}", docId);
        } else if (lastError != null) {
            log.error("经过{}次尝试后，文档检查和删除操作失败: {}, 最后错误: {}", 
                    maxRetries, docId, lastError.getMessage());
        }
    }
}
