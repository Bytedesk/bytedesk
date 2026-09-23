/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_feishu.elastic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.kbase.llm_feishu.FeishuDocEntity;
import com.bytedesk.kbase.llm_feishu.FeishuDocRequest;
import com.bytedesk.kbase.llm_feishu.FeishuDocRestService;
import com.bytedesk.kbase.llm_feishu.FeishuDocStatusEnum;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 飞书文档 Elasticsearch 全文索引服务（对齐 FaqElasticService/TextElasticService 范式）
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FeishuDocElasticService {

    private final ElasticsearchOperations elasticsearchOperations;

    private final FeishuDocRestService feishuDocRestService;

    /**
     * 创建/更新全文索引并回写状态
     */
    public void indexDoc(FeishuDocEntity entity) {
        try {
            FeishuDocElastic doc = FeishuDocElastic.fromEntity(entity);
            elasticsearchOperations.save(doc);
            // 必须经由 RestService 的 @Transactional 包装回写，直调 repository 的 @Modifying 查询会报
            // TransactionRequiredException: No active transaction for update or delete query
            feishuDocRestService.updateElasticStatusOnly(entity.getUid(), FeishuDocStatusEnum.SUCCESS.name());
        } catch (Exception e) {
            log.error("飞书文档全文索引创建失败: uid={}, error={}", entity.getUid(), e.getMessage(), e);
            feishuDocRestService.updateElasticStatusOnly(entity.getUid(), FeishuDocStatusEnum.ERROR.name());
            throw e instanceof RuntimeException re ? re : new RuntimeException(e);
        }
    }

    /**
     * 删除全文索引（幂等：索引或文档不存在时静默）
     */
    public void deleteDoc(String uid) {
        try {
            boolean indexExists = elasticsearchOperations.indexOps(FeishuDocElastic.class).exists();
            if (indexExists && elasticsearchOperations.exists(uid, FeishuDocElastic.class)) {
                elasticsearchOperations.delete(uid, FeishuDocElastic.class);
            }
        } catch (Exception e) {
            log.warn("飞书文档全文索引删除失败: uid={}, error={}", uid, e.getMessage());
        }
    }

    /**
     * 全文检索飞书文档（对齐 searchTexts 的查询构造：multiMatch + enabled/kbUid/orgUid/categoryUid 过滤）
     */
    public List<FeishuDocElasticSearchResult> searchDocs(String query, String kbUid, String categoryUid,
            String orgUid, Integer maxResults, List<String> preferredLanguages) {
        if (!StringUtils.hasText(query)) {
            return new ArrayList<>();
        }
        try {
            boolean indexExists = elasticsearchOperations.indexOps(FeishuDocElastic.class).exists();
            if (!indexExists) {
                log.warn("索引不存在: {}，请先同步飞书文档", FeishuDocElastic.class
                        .getAnnotation(org.springframework.data.elasticsearch.annotations.Document.class).indexName());
                return new ArrayList<>();
            }

            BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

            MultiMatchQuery multiMatch = QueryBuilders.multiMatch()
                    .query(query)
                    .fields("content^3", "title^2")
                    .fuzziness("AUTO")
                    .build();
            boolBuilder.must(multiMatch._toQuery());

            boolBuilder.filter(QueryBuilders.term().field("enabled").value(true).build()._toQuery());
            boolBuilder.filter(
                    QueryBuilders.term().field("sourceType").value("FEISHU").build()._toQuery());

            if (StringUtils.hasText(kbUid)) {
                boolBuilder.filter(QueryBuilders.term().field("kbUid").value(kbUid).build()._toQuery());
            }
            if (StringUtils.hasText(categoryUid)) {
                boolBuilder.filter(QueryBuilders.term().field("categoryUid").value(categoryUid).build()._toQuery());
            }
            if (StringUtils.hasText(orgUid)) {
                boolBuilder.filter(QueryBuilders.term().field("orgUid").value(orgUid).build()._toQuery());
            }
            if (preferredLanguages != null && !preferredLanguages.isEmpty()) {
                BoolQuery.Builder languageQuery = new BoolQuery.Builder();
                preferredLanguages.stream()
                        .filter(StringUtils::hasText)
                        .map(lang -> lang.trim())
                        .map(lang -> lang.toUpperCase())
                        .forEach(language -> languageQuery.should(
                                QueryBuilders.term().field("language").value(language).build()._toQuery()));
                languageQuery.minimumShouldMatch("1");
                boolBuilder.filter(languageQuery.build()._toQuery());
            }

            Query nativeQuery = NativeQuery.builder()
                    .withQuery(boolBuilder.build()._toQuery())
                    .withMaxResults(maxResults != null && maxResults > 0 ? maxResults : 10)
                    .build();

            SearchHits<FeishuDocElastic> hits = elasticsearchOperations.search(nativeQuery, FeishuDocElastic.class);

            List<FeishuDocElasticSearchResult> results = new ArrayList<>();
            for (SearchHit<FeishuDocElastic> hit : hits.getSearchHits()) {
                results.add(FeishuDocElasticSearchResult.builder()
                        .feishuDocElastic(hit.getContent())
                        .score(hit.getScore())
                        .build());
            }
            return results;
        } catch (Exception e) {
            log.error("飞书文档全文检索失败: query={}, error={}", query, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // ================ 管理端索引维护（对齐 FaqElasticService 的管理接口语义） ================

    /**
     * 管理端：更新单条全文索引（手动触发重建）
     */
    public void updateIndex(FeishuDocRequest request) {
        String uid = requireUid(request);
        FeishuDocEntity entity = feishuDocRestService.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("FeishuDoc not found with UID: " + uid));
        indexDoc(entity);
    }

    /**
     * 管理端：删除单条全文索引并回写状态
     * - 删除成功/文档不存在：elasticStatus 置为 NEW
     * - 删除失败：elasticStatus 置为 ERROR
     */
    public Boolean deleteIndexAndSyncStatus(FeishuDocRequest request) {
        String uid = requireUid(request);
        feishuDocRestService.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("FeishuDoc not found with UID: " + uid));
        try {
            boolean indexExists = elasticsearchOperations.indexOps(FeishuDocElastic.class).exists();
            if (indexExists && elasticsearchOperations.exists(uid, FeishuDocElastic.class)) {
                elasticsearchOperations.delete(uid, FeishuDocElastic.class);
            }
            feishuDocRestService.updateElasticStatusOnly(uid, FeishuDocStatusEnum.NEW.name());
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("删除飞书文档全文索引失败: uid={}, error={}", uid, e.getMessage(), e);
            feishuDocRestService.updateElasticStatusOnly(uid, FeishuDocStatusEnum.ERROR.name());
            return Boolean.FALSE;
        }
    }

    /**
     * 管理端：同步单条全文索引状态（检查 ES 中是否存在该文档并回写）
     */
    public FeishuDocEntity syncElasticStatus(FeishuDocRequest request) {
        String uid = requireUid(request);
        feishuDocRestService.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("FeishuDoc not found with UID: " + uid));
        boolean indexExists = elasticsearchOperations.indexOps(FeishuDocElastic.class).exists();
        boolean docExists = indexExists && elasticsearchOperations.exists(uid, FeishuDocElastic.class);
        String nextStatus = docExists ? FeishuDocStatusEnum.SUCCESS.name() : FeishuDocStatusEnum.NEW.name();
        feishuDocRestService.updateElasticStatusOnly(uid, nextStatus);
        return feishuDocRestService.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("FeishuDoc not found with UID: " + uid));
    }

    /**
     * 管理端：查询全文索引文档详情（前端“查看索引”弹窗）
     */
    public Map<String, Object> queryElasticByUid(FeishuDocRequest request) {
        String uid = requireUid(request);
        boolean indexExists = elasticsearchOperations.indexOps(FeishuDocElastic.class).exists();
        FeishuDocElastic doc = null;
        if (indexExists) {
            doc = elasticsearchOperations.get(uid, FeishuDocElastic.class);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("indexExists", indexExists);
        result.put("exists", doc != null);
        result.put("doc", doc);
        return result;
    }

    private String requireUid(FeishuDocRequest request) {
        if (!StringUtils.hasText(request.getUid())) {
            throw new RuntimeException("uid is required");
        }
        return request.getUid();
    }
}
