/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-08 10:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.kbase.expiry;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.kbase.article.ArticleEntity;
import com.bytedesk.kbase.article.ArticleRepository;
import com.bytedesk.kbase.article.ArticleRestService;
import com.bytedesk.kbase.article.ArticleStatusEnum;
import com.bytedesk.kbase.article.elastic.ArticleElasticService;
import com.bytedesk.kbase.article.vector.ArticleVectorService;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;
import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.llm_faq.FaqRepository;
import com.bytedesk.kbase.llm_faq.FaqRestService;
import com.bytedesk.kbase.llm_faq.FaqStatusEnum;
import com.bytedesk.kbase.llm_faq.elastic.FaqElasticService;
import com.bytedesk.kbase.llm_faq.vector.FaqVectorService;
import com.bytedesk.kbase.llm_text.TextEntity;
import com.bytedesk.kbase.llm_text.TextRepository;
import com.bytedesk.kbase.llm_text.TextRestService;
import com.bytedesk.kbase.llm_text.elastic.TextElasticService;
import com.bytedesk.kbase.llm_text.vector.TextVectorService;

import lombok.extern.slf4j.Slf4j;

/**
 * 知识库内容过期清理任务（第 1 层物理清理）：
 * 将"已过期（endDate < now）且仍残留在索引中"的 FAQ/Text/Article 从
 * Elasticsearch 全文索引和向量索引中物理移除，状态置 EXPIRED。
 *
 * - 三层防线中的第 1 层：配合检索期过滤（第 2 层）与 LLM 兜底过滤（第 3 层）；
 * - 幂等：状态置 EXPIRED 后不再命中"elasticStatus/vectorStatus = SUCCESS"查询条件；
 * - 只处理已过期，不处理未生效（未生效内容到点自动可被检索，不应删除）；
 * - 复活：管理员延长 endDate 后 UpdateDocEvent -> MQ -> 自动重建索引，状态回 SUCCESS。
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "bytedesk.kbase", name = "expiry-enabled", havingValue = "true", matchIfMissing = true)
public class KbaseExpiryTask {

    private static final int BATCH_SIZE = 200;

    private final FaqRepository faqRepository;
    private final TextRepository textRepository;
    private final ArticleRepository articleRepository;

    private final FaqRestService faqRestService;
    private final TextRestService textRestService;
    private final ArticleRestService articleRestService;

    private final FaqElasticService faqElasticService;
    private final TextElasticService textElasticService;
    private final ArticleElasticService articleElasticService;

    // 向量服务为条件装配（spring.ai.vectorstore.elasticsearch.enabled），用 ObjectProvider 兼容未配置向量环境
    private final ObjectProvider<FaqVectorService> faqVectorServiceProvider;
    private final ObjectProvider<TextVectorService> textVectorServiceProvider;
    private final ObjectProvider<ArticleVectorService> articleVectorServiceProvider;

    public KbaseExpiryTask(
            FaqRepository faqRepository,
            TextRepository textRepository,
            ArticleRepository articleRepository,
            FaqRestService faqRestService,
            TextRestService textRestService,
            ArticleRestService articleRestService,
            FaqElasticService faqElasticService,
            TextElasticService textElasticService,
            ArticleElasticService articleElasticService,
            ObjectProvider<FaqVectorService> faqVectorServiceProvider,
            ObjectProvider<TextVectorService> textVectorServiceProvider,
            ObjectProvider<ArticleVectorService> articleVectorServiceProvider) {
        this.faqRepository = faqRepository;
        this.textRepository = textRepository;
        this.articleRepository = articleRepository;
        this.faqRestService = faqRestService;
        this.textRestService = textRestService;
        this.articleRestService = articleRestService;
        this.faqElasticService = faqElasticService;
        this.textElasticService = textElasticService;
        this.articleElasticService = articleElasticService;
        this.faqVectorServiceProvider = faqVectorServiceProvider;
        this.textVectorServiceProvider = textVectorServiceProvider;
        this.articleVectorServiceProvider = articleVectorServiceProvider;
    }

    @Scheduled(cron = "${bytedesk.kbase.expiry-cron:0 0 3 * * ?}")
    public void cleanupExpiredContent() {
        ZonedDateTime now = BdDateUtils.now();
        log.info("KbaseExpiryTask 开始清理过期知识库内容: now={}", now);

        int faqCleaned = cleanupExpiredFaqs(now);
        int textCleaned = cleanupExpiredTexts(now);
        int articleCleaned = cleanupExpiredArticles(now);

        log.info("KbaseExpiryTask 清理完成: faq={}, text={}, article={}", faqCleaned, textCleaned, articleCleaned);
    }

    private int cleanupExpiredFaqs(ZonedDateTime now) {
        FaqVectorService faqVectorService = faqVectorServiceProvider.getIfAvailable();
        int cleaned = 0;
        int failed = 0;
        int page = 0;
        Page<FaqEntity> faqPage;
        do {
            Pageable pageable = PageRequest.of(page, BATCH_SIZE);
            faqPage = faqRepository.findExpiredIndexed(now, pageable);
            for (FaqEntity faq : faqPage.getContent()) {
                try {
                    // 删除全文文档（含翻译文档）
                    faqElasticService.deleteFaq(faq.getUid());
                    faqElasticService.deleteTranslatedFaqDocuments(faq.getUid());
                    // 删除向量文档（含翻译文档）
                    if (faqVectorService != null) {
                        faqVectorService.deleteFaqVector(faq);
                        faqVectorService.deleteTranslatedFaqVectors(faq);
                    }
                    // 状态置 EXPIRED（幂等：不再命中查询条件），走 RestService 事务方法并清缓存
                    faqRestService.updateElasticStatusOnly(faq.getUid(), FaqStatusEnum.EXPIRED.name());
                    faqRestService.updateVectorStatusOnly(faq.getUid(), FaqStatusEnum.EXPIRED.name());
                    faqRestService.evictFaqCacheAllEntries();
                    cleaned++;
                } catch (Exception e) {
                    failed++;
                    log.warn("清理过期FAQ失败: uid={}, error={}", faq.getUid(), e.getMessage());
                }
            }
            page++;
        } while (faqPage.hasNext());

        if (cleaned > 0 || failed > 0) {
            log.info("过期FAQ清理: cleaned={}, failed={}", cleaned, failed);
        }
        return cleaned;
    }

    private int cleanupExpiredTexts(ZonedDateTime now) {
        TextVectorService textVectorService = textVectorServiceProvider.getIfAvailable();
        int cleaned = 0;
        int failed = 0;
        int page = 0;
        Page<TextEntity> textPage;
        do {
            Pageable pageable = PageRequest.of(page, BATCH_SIZE);
            textPage = textRepository.findExpiredIndexed(now, pageable);
            for (TextEntity text : textPage.getContent()) {
                try {
                    // 删除全文文档（含翻译文档）
                    textElasticService.deleteText(text.getUid());
                    textElasticService.deleteTranslatedTextDocuments(text.getUid());
                    // 删除向量文档（含翻译文档）
                    if (textVectorService != null) {
                        textVectorService.deleteTextVector(text);
                        textVectorService.deleteTranslatedTextVectors(text);
                    }
                    // 状态置 EXPIRED（幂等：不再命中查询条件），走 RestService 事务方法并清缓存
                    textRestService.updateElasticStatusOnly(text.getUid(), ChunkStatusEnum.EXPIRED.name());
                    textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.EXPIRED.name());
                    textRestService.evictTextCacheAllEntries();
                    cleaned++;
                } catch (Exception e) {
                    failed++;
                    log.warn("清理过期Text失败: uid={}, error={}", text.getUid(), e.getMessage());
                }
            }
            page++;
        } while (textPage.hasNext());

        if (cleaned > 0 || failed > 0) {
            log.info("过期Text清理: cleaned={}, failed={}", cleaned, failed);
        }
        return cleaned;
    }

    private int cleanupExpiredArticles(ZonedDateTime now) {
        ArticleVectorService articleVectorService = articleVectorServiceProvider.getIfAvailable();
        int cleaned = 0;
        int failed = 0;
        int page = 0;
        Page<ArticleEntity> articlePage;
        do {
            Pageable pageable = PageRequest.of(page, BATCH_SIZE);
            articlePage = articleRepository.findExpiredIndexed(now, pageable);
            for (ArticleEntity article : articlePage.getContent()) {
                try {
                    // 删除全文文档
                    articleElasticService.deleteArticle(article.getUid());
                    // 删除向量文档
                    if (articleVectorService != null) {
                        articleVectorService.deleteArticle(article);
                    }
                    // 状态置 EXPIRED（幂等：不再命中查询条件），走 RestService 事务方法并清缓存
                    articleRestService.updateElasticStatusOnly(article.getUid(), ArticleStatusEnum.EXPIRED.name());
                    articleRestService.updateVectorStatusOnly(article.getUid(), ArticleStatusEnum.EXPIRED.name());
                    articleRestService.evictArticleCache(article.getUid());
                    cleaned++;
                } catch (Exception e) {
                    failed++;
                    log.warn("清理过期Article失败: uid={}, error={}", article.getUid(), e.getMessage());
                }
            }
            page++;
        } while (articlePage.hasNext());

        if (cleaned > 0 || failed > 0) {
            log.info("过期Article清理: cleaned={}, failed={}", cleaned, failed);
        }
        return cleaned;
    }
}
