/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:20:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 16:21:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

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
        log.info("向量索引FAQ: {}", faq.getQuestion());
        
        try {
            // 1. 为问题和答案创建文档（带有元数据）
            String id = "faq_" + faq.getUid();
            String content = faq.getQuestion() + "\n" + faq.getAnswer();
            
            // 处理标签，将其转化为字符串便于索引
            String tags = String.join(",", faq.getTagList());
            
            // 元数据
            Map<String, Object> metadata = Map.of(
                "uid", faq.getUid(),
                "question", faq.getQuestion(),
                KbaseConst.KBASE_KB_UID, faq.getKbase() != null ? faq.getKbase().getUid() : "",
                "categoryUid", faq.getCategoryUid() != null ? faq.getCategoryUid() : "",
                "orgUid", faq.getOrgUid(),
                "enabled", Boolean.toString(faq.isEnabled()),
                "tags", tags
            );
            
            // 创建文档
            Document document = new Document(id, content, metadata);
            
            // 2. 添加到向量存储
            // 检查是否已存在该文档ID
            checkAndDeleteExistingDoc(id);
            
            // 添加新文档
            vectorStore.add(List.of(document));
            
            // 3. 更新FAQ实体中的文档ID列表
            List<String> docIdList = faq.getDocIdList();
            if (docIdList == null) {
                docIdList = new ArrayList<>();
            }
            if (!docIdList.contains(id)) {
                docIdList.add(id);
                faq.setDocIdList(docIdList);
                
                // 设置向量索引状态为成功
                faq.setVectorStatus(ChunkStatusEnum.SUCCESS.name());
                
                // 更新FAQ实体
                faqRestService.save(faq);
            }
            
            log.info("FAQ向量索引成功: {}", faq.getQuestion());
        } catch (Exception e) {
            log.error("FAQ向量索引失败: {}, 错误: {}", faq.getQuestion(), e.getMessage());
            
            // 设置向量索引状态为失败
            faq.setVectorStatus(ChunkStatusEnum.ERROR.name());
            faqRestService.save(faq);
            
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
    public boolean deleteFaqVector(FaqEntity faq) {
        log.info("从向量索引中删除FAQ: {}", faq.getQuestion());
        
        try {
            // 获取文档ID列表
            List<String> docIdList = faq.getDocIdList();
            if (docIdList != null && !docIdList.isEmpty()) {
                // 删除所有关联的向量文档
                vectorStore.delete(docIdList);
                
                // 清空文档ID列表并更新状态
                faq.setDocIdList(new ArrayList<>());
                faq.setVectorStatus(ChunkStatusEnum.NEW.name());
                faqRestService.save(faq);
                
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("从向量存储中删除FAQ失败: {}, 错误: {}", faq.getQuestion(), e.getMessage());
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
            .questionList(new ArrayList<>()) // 空列表，因为文档中可能没有这个信息
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
