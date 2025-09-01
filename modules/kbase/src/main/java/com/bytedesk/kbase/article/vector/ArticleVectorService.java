/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 17:30:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-01 16:15:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article.vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.kbase.config.KbaseConst;
import com.bytedesk.kbase.article.ArticleEntity;
import com.bytedesk.kbase.article.ArticleRestService;
import com.bytedesk.kbase.article.ArticleRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * 文章向量检索服务
 * 用于处理文章的向量存储和相似度搜索
 * 
 * @author jackning
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.ai.vectorstore.elasticsearch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ArticleVectorService {

    private final ElasticsearchVectorStore vectorStore;

    private final ArticleRestService articleRestService;

    /**
     * 将文章内容添加到向量存储中
     * 添加版本检查，以更好地处理乐观锁冲突
     * 
     * @param article 文章实体
     */
    @Transactional
    public void indexVector(ArticleEntity article) {
        log.info("开始向量索引文章: {}, ID: {}", article.getTitle(), article.getUid());

        // 在处理前先获取最新的文章实体
        Optional<ArticleEntity> currentArticleOpt = articleRestService.findByUid(article.getUid());
        if (!currentArticleOpt.isPresent()) {
            log.error("文章实体不存在，无法创建向量索引: {}", article.getUid());
            throw new RuntimeException("文章实体不存在: " + article.getUid());
        }

        ArticleEntity currentArticle = currentArticleOpt.get();
        log.info("获取到最新文章实体，当前向量状态: {}, ID: {}", currentArticle.getVectorStatus(), currentArticle.getUid());

        try {
            // 1. 为标题和内容创建文档（带有元数据）
            String id = "article_" + currentArticle.getUid();
            String content = currentArticle.getTitle() + "\n" + currentArticle.getContentMarkdown();

            // 处理标签，将其转化为字符串便于索引
            String tags = String.join(",", currentArticle.getTagList());

            // 元数据
            Map<String, Object> metadata = Map.of(
                    "uid", currentArticle.getUid(),
                    "title", currentArticle.getTitle(),
                    KbaseConst.KBASE_KB_UID, currentArticle.getKbase().getUid() != null ? currentArticle.getKbase().getUid() : "",
                    "categoryUid", currentArticle.getCategoryUid() != null ? currentArticle.getCategoryUid() : "",
                    "orgUid", currentArticle.getOrgUid(),
                    "enabled", Boolean.toString(currentArticle.getPublished()),
                    "tags", tags);

            // 创建文档
            Document document = new Document(id, content, metadata);

            // 添加新文档到向量存储
            log.info("向向量存储添加文档: {}", id);
            vectorStore.add(List.of(document));
            log.info("已成功添加文档到向量存储: {}", id);

            // 3. 更新文章实体中的文档ID列表
            List<String> docIdList = currentArticle.getDocIdList();
            if (docIdList == null) {
                docIdList = new ArrayList<>();
            } else if (docIdList.contains(id)) {
                // 如果已包含该ID，先移除再添加以确保唯一性
                docIdList.remove(id);
                log.info("从文档ID列表中移除重复ID: {}", id);
            }

            // 添加文档ID并更新状态
            docIdList.add(id);
            currentArticle.setDocIdList(docIdList);

            // 设置向量索引状态为成功
            currentArticle.setVectorSuccess();

            // 更新文章实体 - 使用明确的事务保证状态更新和保存原子性
            log.info("准备保存文章实体更新，设置向量索引状态为成功: {}", currentArticle.getUid());

            articleRestService.save(currentArticle);

        } catch (Exception e) {
            log.error("文章向量索引失败: {}, 错误: {}", currentArticle.getTitle(), e.getMessage(), e);

            // 设置向量索引状态为失败
            currentArticle.setVectorError();
            try {
                log.info("保存文章实体更新，设置向量索引状态为失败: {}", currentArticle.getUid());
                articleRestService.save(currentArticle);
            } catch (Exception saveEx) {
                log.error("更新文章向量索引状态失败: {}, 错误: {}", currentArticle.getUid(), saveEx.getMessage());
            }

            throw new RuntimeException("创建向量索引失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新文章的向量索引
     * 
     * @param request 文章请求对象
     */
    public void updateVectorIndex(ArticleRequest request) {
        Optional<ArticleEntity> articleOpt = articleRestService.findByUid(request.getUid());
        if (articleOpt.isPresent()) {
            ArticleEntity article = articleOpt.get();
            // 删除旧的向量索引
            deleteArticle(article);
            // 创建新的向量索引
            indexVector(article);
        } else {
            log.warn("未找到要更新向量索引的文章: {}", request.getUid());
        }
    }

    /**
     * 更新所有文章的向量索引
     * 
     * @param request 文章请求对象，包含知识库ID
     */
    public void updateAllVectorIndex(ArticleRequest request) {
        List<ArticleEntity> articleList = articleRestService.findByKbUid(request.getKbUid());
        articleList.forEach(article -> {
            try {
                // 删除旧的向量索引
                deleteArticle(article);
                // 创建新的向量索引
                indexVector(article);
            } catch (Exception e) {
                log.error("更新文章向量索引失败: {}, 错误: {}", article.getTitle(), e.getMessage());
            }
        });
    }

    /**
     * 从向量存储中删除文章向量文档
     * 修改为不更新实体的方式，只进行向量删除操作，避免实体并发修改冲突
     * 
     * @param article 文章实体
     * @return 删除成功返回true，否则返回false
     */
    @Transactional(readOnly = true) // 只读事务，因为我们不会修改实体
    public Boolean deleteArticle(ArticleEntity article) {
        log.info("从向量索引中删除文章: {}, ID: {}", article.getTitle(), article.getUid());
        try {
            // 获取文章文档ID列表
            List<String> docIdList = article.getDocIdList();
            if (docIdList == null || docIdList.isEmpty()) {
                log.info("文章没有关联的向量文档，无需删除: {}", article.getUid());
                return true;
            }

            // 从向量存储中删除所有相关文档
            log.info("删除文章向量文档, 数量: {}, IDs: {}", docIdList.size(), docIdList);
            vectorStore.delete(docIdList);

            // 不再在此方法中更新实体状态，避免乐观锁冲突
            // 状态更新将在indexArticleVector方法中完成

            log.info("成功从向量存储中删除文章文档: {}", article.getUid());
            return true;
        } catch (Exception e) {
            log.error("删除文章向量索引失败: {}, 错误: {}", article.getTitle(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * 在向量存储中进行语义搜索
     * 
     * @param query       搜索关键词
     * @param kbUid       知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid      组织UID（可选）
     * @param limit       返回结果数量限制
     * @return 相似度搜索结果列表
     */
    public List<ArticleVectorSearchResult> searchArticleVector(String query, String kbUid, String categoryUid, String orgUid,
            int limit) {
        log.info("向量搜索文章: query={}, kbUid={}, categoryUid={}, orgUid={}", query, kbUid, categoryUid, orgUid);

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
        List<ArticleVectorSearchResult> resultList = new ArrayList<>();

        for (Document doc : similarDocuments) {
            // 从文档中提取元数据
            Map<String, Object> metadata = doc.getMetadata();
            String uid = (String) metadata.getOrDefault("uid", "");

            // 1. 通过UID查找对应的文章实体，以便获取完整信息
            Optional<ArticleEntity> articleEntityOpt = articleRestService.findByUid(uid);
            if (articleEntityOpt.isPresent()) {
                ArticleEntity articleEntity = articleEntityOpt.get();

                // 2. 将ArticleEntity转换为ArticleVector
                ArticleVector articleVector = ArticleVector.fromArticleEntity(articleEntity);

                // 3. 创建搜索结果对象
                ArticleVectorSearchResult result = ArticleVectorSearchResult.builder()
                        .articleVector(articleVector)
                        .score(doc.getScore().floatValue())
                        .highlightedTitle(articleVector.getTitle()) // 如果需要高亮可以在这里处理
                        .distance((float) (1.0 - doc.getScore().doubleValue())) // 将相似度转换为距离，距离 = 1 - 相似度
                        .build();

                resultList.add(result);
            } else {
                // 如果找不到对应的文章实体，尝试从文档元数据构建一个简化的ArticleVector
                ArticleVector simpleArticleVector = createSimpleArticleVectorFromDocument(doc);

                ArticleVectorSearchResult result = ArticleVectorSearchResult.builder()
                        .articleVector(simpleArticleVector)
                        .score(doc.getScore().floatValue())
                        .highlightedTitle((String) metadata.getOrDefault("title", ""))
                        .distance((float) (1.0 - doc.getScore().doubleValue())) // 同上
                        .build();

                resultList.add(result);
            }
        }

        return resultList;
    }

    /**
     * 从文档创建简化版的ArticleVector对象
     * 用于在找不到对应的文章实体时提供基础信息
     * 
     * @param doc 文档对象
     * @return 简化的ArticleVector对象
     */
    private ArticleVector createSimpleArticleVectorFromDocument(Document doc) {
        Map<String, Object> metadata = doc.getMetadata();

        // 提取元数据
        String uid = (String) metadata.getOrDefault("uid", "");
        String title = (String) metadata.getOrDefault("title", "");
        String content = doc.getText();
        String contentMarkdown = "";
        String contentHtml = "";

        // 从内容中提取内容（通常标题和内容用换行符分隔）
        if (content != null && content.contains("\n")) {
            String[] parts = content.split("\n", 2);
            contentMarkdown = parts.length > 1 ? parts[1] : "";
        }

        // 处理标签
        String tagsStr = (String) metadata.getOrDefault("tags", "");
        List<String> tagList = new ArrayList<>();
        if (tagsStr != null && !tagsStr.isEmpty()) {
            tagList = List.of(tagsStr.split(","));
        }

        // 创建ArticleVector对象
        return ArticleVector.builder()
                .uid(uid)
                .title(title)
                .contentMarkdown(contentMarkdown)
                .contentHtml(contentHtml)
                .tagList(tagList)
                .orgUid((String) metadata.getOrDefault("orgUid", ""))
                .kbUid((String) metadata.getOrDefault(KbaseConst.KBASE_KB_UID, ""))
                .categoryUid((String) metadata.getOrDefault("categoryUid", ""))
                .enabled(Boolean.parseBoolean((String) metadata.getOrDefault("enabled", "true")))
                .build();
    }
}
