package com.bytedesk.ai.kbase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot.RobotSearchTypeEnum;
import com.bytedesk.ai.service.SearchResultWithSources;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.kbase.article.elastic.ArticleElastic;
import com.bytedesk.kbase.article.elastic.ArticleElasticSearchResult;
import com.bytedesk.kbase.article.elastic.ArticleElasticService;
import com.bytedesk.kbase.article.vector.ArticleVector;
import com.bytedesk.kbase.article.vector.ArticleVectorSearchResult;
import com.bytedesk.kbase.article.vector.ArticleVectorService;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElastic;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElasticSearchResult;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElasticService;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVector;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVectorSearchResult;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVectorService;
import com.bytedesk.kbase.llm_faq.FaqProtobuf;
import com.bytedesk.kbase.llm_faq.elastic.FaqElastic;
import com.bytedesk.kbase.llm_faq.elastic.FaqElasticSearchResult;
import com.bytedesk.kbase.llm_faq.elastic.FaqElasticService;
import com.bytedesk.kbase.llm_faq.vector.FaqVector;
import com.bytedesk.kbase.llm_faq.vector.FaqVectorSearchResult;
import com.bytedesk.kbase.llm_faq.vector.FaqVectorService;
import com.bytedesk.kbase.llm_text.elastic.TextElastic;
import com.bytedesk.kbase.llm_text.elastic.TextElasticSearchResult;
import com.bytedesk.kbase.llm_text.elastic.TextElasticService;
import com.bytedesk.kbase.llm_text.vector.TextVector;
import com.bytedesk.kbase.llm_text.vector.TextVectorSearchResult;
import com.bytedesk.kbase.llm_text.vector.TextVectorService;
import com.bytedesk.kbase.llm_webpage.elastic.WebpageElastic;
import com.bytedesk.kbase.llm_webpage.elastic.WebpageElasticSearchResult;
import com.bytedesk.kbase.llm_webpage.elastic.WebpageElasticService;
import com.bytedesk.kbase.llm_webpage.vector.WebpageVector;
import com.bytedesk.kbase.llm_webpage.vector.WebpageVectorSearchResult;
import com.bytedesk.kbase.llm_webpage.vector.WebpageVectorService;

import lombok.extern.slf4j.Slf4j;

/**
 * 知识库检索与结果重排辅助类：
 * - 封装全文/向量检索及来源构造
 * - 提供聚合/去重/重排/TopK 能力
 */
@Slf4j
@Component
public class KbaseSearchHelper {

    public KbaseSearchHelper(
            ObjectProvider<FaqVectorService> faqVectorServiceProvider,
            ObjectProvider<TextVectorService> textVectorServiceProvider,
            ObjectProvider<ChunkVectorService> chunkVectorServiceProvider,
            ObjectProvider<WebpageVectorService> webpageVectorServiceProvider,
            ObjectProvider<ArticleVectorService> articleVectorServiceProvider,
            FaqElasticService faqElasticService,
            TextElasticService textElasticService,
            ChunkElasticService chunkElasticService,
            WebpageElasticService webpageElasticService,
            ArticleElasticService articleElasticService) {
        this.faqElasticService = faqElasticService;
        this.textElasticService = textElasticService;
        this.chunkElasticService = chunkElasticService;
        this.webpageElasticService = webpageElasticService;
        this.articleElasticService = articleElasticService;
        this.faqVectorService = faqVectorServiceProvider.getIfAvailable();
        this.textVectorService = textVectorServiceProvider.getIfAvailable();
        this.chunkVectorService = chunkVectorServiceProvider.getIfAvailable();
        this.webpageVectorService = webpageVectorServiceProvider.getIfAvailable();
        this.articleVectorService = articleVectorServiceProvider.getIfAvailable();
    }

    private static final int DEFAULT_VECTOR_RECALL_LIMIT = 5;
    private static final int MAX_VECTOR_RECALL_LIMIT = 50;

    private static final int DEFAULT_FULLTEXT_RECALL_LIMIT = 10;
    private static final int MAX_FULLTEXT_RECALL_LIMIT = 200;

    private final FaqElasticService faqElasticService;
    private final TextElasticService textElasticService;
    private final ChunkElasticService chunkElasticService;
    private final WebpageElasticService webpageElasticService;
    private final ArticleElasticService articleElasticService;

    private final FaqVectorService faqVectorService;
    private final TextVectorService textVectorService;
    private final ChunkVectorService chunkVectorService;
    private final WebpageVectorService webpageVectorService;
    private final ArticleVectorService articleVectorService;

    // 2. 知识库搜索相关方法
    public List<FaqProtobuf> searchKnowledgeBase(String query, RobotProtobuf robot) {
        // 统一走“带来源”的检索，再做聚合/TopK，返回Faq列表
        SearchResultWithSources raw = searchKnowledgeBaseWithSources(query, robot);
        SearchResultWithSources aggregated = rerankMergeTopK(raw, robot);
        return aggregated.getSearchResults();
    }

    /**
     * 搜索知识库并收集源引用信息
     * 
     * @param query 查询内容
     * @param robot 机器人配置
     * @return 包含源引用信息的搜索结果
     */
    public SearchResultWithSources searchKnowledgeBaseWithSources(String query, RobotProtobuf robot) {
        return searchKnowledgeBaseWithSources(query, robot, null, null);
    }

    /**
     * 搜索知识库并收集源引用信息（支持按数据源类型过滤）
     * 
     * @param query 查询内容
     * @param robot 机器人配置
     * @param sourceTypeFilter 数据源类型过滤（ALL/FAQ/TEXT/CHUNK/WEBPAGE）
     * @return 包含源引用信息的搜索结果
     */
    public SearchResultWithSources searchKnowledgeBaseWithSources(String query, RobotProtobuf robot,
            String sourceTypeFilter) {
        return searchKnowledgeBaseWithSources(query, robot, sourceTypeFilter, null);
        }

        public SearchResultWithSources searchKnowledgeBaseWithSources(String query, RobotProtobuf robot,
            String sourceTypeFilter, List<String> preferredLanguages) {
        // 如果知识库未启用，直接返回空结果
        if (!StringUtils.hasText(robot.getKbUid()) || !robot.getKbEnabled()) {
            log.info("知识库未启用或未指定知识库UID");
            return new SearchResultWithSources(new ArrayList<>(), new ArrayList<>());
        }

        // 创建搜索结果列表和源引用列表
        List<FaqProtobuf> searchResultList = new ArrayList<>();
        List<RobotContent.SourceReference> sourceReferences = new ArrayList<>();

        // 根据搜索类型执行相应的搜索
        String searchType = robot.getLlm().getSearchType();
        if (searchType == null) {
            searchType = RobotSearchTypeEnum.FULLTEXT.name(); // 默认使用全文搜索
        }

        // 执行搜索并收集源引用
        switch (RobotSearchTypeEnum.valueOf(searchType)) {
            case VECTOR:
                log.info("使用向量搜索");
                    executeVectorSearchWithSources(query, robot, robot.getKbUid(), searchResultList,
                            sourceReferences, sourceTypeFilter, preferredLanguages);
                break;
            case MIXED:
                log.info("使用混合搜索");
                executeFulltextSearchWithSources(query, robot, robot.getKbUid(), searchResultList,
                    sourceReferences, preferredLanguages, sourceTypeFilter);
                    executeVectorSearchWithSources(query, robot, robot.getKbUid(), searchResultList,
                            sourceReferences, sourceTypeFilter, preferredLanguages);
                break;
            case FULLTEXT:
            default:
                log.info("使用全文搜索");
                executeFulltextSearchWithSources(query, robot, robot.getKbUid(), searchResultList,
                    sourceReferences, preferredLanguages, sourceTypeFilter);
                break;
        }

        // 过滤数据源类型（ALL/空值不过滤）
        if (StringUtils.hasText(sourceTypeFilter) && !"ALL".equalsIgnoreCase(sourceTypeFilter)) {
            String normalized = sourceTypeFilter.trim().toUpperCase();
            List<RobotContent.SourceReference> filtered = new ArrayList<>();
            for (RobotContent.SourceReference s : sourceReferences) {
                if (s == null || s.getSourceType() == null) {
                    continue;
                }
                String typeName = s.getSourceType().name();
                if (normalized.equals(typeName)) {
                    filtered.add(s);
                }
            }
            sourceReferences = filtered;
        }

        // 读取过滤参数：scoreThreshold、topP、topK（允许为空，使用安全默认）
        Double scoreThreshold = null;
        Double topP = null;
        Integer topK = null;
        try {
            if (robot != null && robot.getLlm() != null) {
                scoreThreshold = robot.getLlm().getScoreThreshold();
                topP = robot.getLlm().getTopP();
                topK = robot.getLlm().getTopK();
            }
        } catch (Exception ex) {
            log.debug("Read robot llm config failed, use defaults", ex);
        }

        // 1) 先对来源进行 uid 聚合：同一内容保留分数最高的一条
        Map<String, RobotContent.SourceReference> bestSrcByUid = new LinkedHashMap<>();
        for (RobotContent.SourceReference src : sourceReferences) {
            if (src == null || !StringUtils.hasText(src.getSourceUid()))
                continue;
            RobotContent.SourceReference existing = bestSrcByUid.get(src.getSourceUid());
            double s = src.getScore() != null ? src.getScore() : 0.0;
            if (existing == null) {
                bestSrcByUid.put(src.getSourceUid(), src);
            } else {
                double old = existing.getScore() != null ? existing.getScore() : 0.0;
                if (s > old)
                    bestSrcByUid.put(src.getSourceUid(), src);
            }
        }

        // 2) 应用 scoreThreshold 与 topP 过滤
        double maxScore = 0.0;
        for (RobotContent.SourceReference s : bestSrcByUid.values()) {
            double sc = s.getScore() != null ? s.getScore() : 0.0;
            if (sc > maxScore)
                maxScore = sc;
        }

        double pCut = 0.0;
        if (topP != null && topP > 0 && topP <= 1 && maxScore > 0) {
            pCut = topP * maxScore; // 保留分数 >= topP*maxScore 的来源
        }
        double thr = scoreThreshold != null ? scoreThreshold : Double.NEGATIVE_INFINITY; // 若未设置阈值则不限制
        double finalCut = Math.max(thr, pCut);

        List<RobotContent.SourceReference> filteredSources = new ArrayList<>();
        for (RobotContent.SourceReference s : bestSrcByUid.values()) {
            double sc = s.getScore() != null ? s.getScore() : 0.0;
            if (sc >= finalCut)
                filteredSources.add(s);
        }

        // 3) 按分数降序排序
        filteredSources.sort((a, b) -> {
            double sa = a.getScore() != null ? a.getScore() : 0.0;
            double sb = b.getScore() != null ? b.getScore() : 0.0;
            return Double.compare(sb, sa);
        });

        // 4) 应用 topK 截断
        int useTopK = (topK != null && topK > 0) ? topK : Integer.MAX_VALUE;
        if (filteredSources.size() > useTopK) {
            filteredSources = filteredSources.subList(0, useTopK);
        }

        // 5) 构建 uid->Faq 的映射（保留首次出现）
        Map<String, FaqProtobuf> faqByUidFirst = new LinkedHashMap<>();
        for (FaqProtobuf faq : searchResultList) {
            if (faq == null || !StringUtils.hasText(faq.getUid()))
                continue;
            String resultKey = StringUtils.hasText(faq.getSourceUid()) ? faq.getSourceUid() : faq.getUid();
            faqByUidFirst.putIfAbsent(resultKey, faq);
        }

        // 6) 根据过滤后的来源顺序构建对应的 Faq 列表，确保两者对齐
        List<FaqProtobuf> filteredFaqs = new ArrayList<>();
        for (RobotContent.SourceReference s : filteredSources) {
            FaqProtobuf f = faqByUidFirst.get(s.getSourceUid());
            if (f != null) {
                // 以来源类型为准，保证 searchResults.type 与 sourceReferences.sourceType 一致
                if (s != null && s.getSourceType() != null) {
                    f.setType(s.getSourceType().name());
                }
                filteredFaqs.add(f);
            }
        }

        return new SearchResultWithSources(filteredFaqs, filteredSources);
    }

    /**
     * 执行全文搜索并填充结果与来源引用
     */
    public void executeFulltextSearchWithSources(String query, RobotProtobuf robot, String kbUid,
            List<FaqProtobuf> searchResultList,
            List<RobotContent.SourceReference> sourceReferences,
            List<String> preferredLanguages,
            String sourceTypeFilter) {

        boolean allowAll = !StringUtils.hasText(sourceTypeFilter) || "ALL".equalsIgnoreCase(sourceTypeFilter);
        boolean allowFaq = allowAll || "FAQ".equalsIgnoreCase(sourceTypeFilter);
        boolean allowText = allowAll || "TEXT".equalsIgnoreCase(sourceTypeFilter);
        boolean allowChunk = allowAll || "CHUNK".equalsIgnoreCase(sourceTypeFilter);
        boolean allowWebpage = allowAll || "WEBPAGE".equalsIgnoreCase(sourceTypeFilter);
        boolean allowArticle = allowAll || "ARTICLE".equalsIgnoreCase(sourceTypeFilter);

        int recallLimit = DEFAULT_FULLTEXT_RECALL_LIMIT;
        try {
            Integer topK = (robot != null && robot.getLlm() != null) ? robot.getLlm().getTopK() : null;
            if (topK != null && topK > 0) {
                recallLimit = Math.max(DEFAULT_FULLTEXT_RECALL_LIMIT, topK);
            }
        } catch (Exception ex) {
            log.debug("Compute fulltext recall limit failed, use default", ex);
        }
        if (recallLimit > MAX_FULLTEXT_RECALL_LIMIT) {
            recallLimit = MAX_FULLTEXT_RECALL_LIMIT;
        }

        List<String> languageFallbackOrder = buildLanguageFallbackOrder(preferredLanguages);
        for (String language : languageFallbackOrder) {
            int resultSizeBefore = searchResultList.size();

            if (allowFaq) {
                List<FaqElasticSearchResult> searchResults = faqElasticService.searchFaq(query, kbUid, null, null,
                        recallLimit, language == null ? null : List.of(language));
                for (FaqElasticSearchResult withScore : searchResults) {
                    FaqElastic faq = withScore.getFaqElastic();
                    FaqProtobuf faqProtobuf = FaqProtobuf.fromElastic(faq);
                    searchResultList.add(faqProtobuf);

                    RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                            .sourceType(RobotContent.SourceTypeEnum.FAQ)
                            .sourceUid(StringUtils.hasText(faq.getSourceUid()) ? faq.getSourceUid() : faq.getUid())
                            .sourceName(faq.getQuestion())
                            .contentSummary(getContentSummary(faq.getAnswer(), 200))
                            .language(faq.getLanguage())
                            .searchChannel(RobotSearchTypeEnum.FULLTEXT.name())
                            .score((double) withScore.getScore())
                            .highlighted(false)
                            .build();
                    sourceReferences.add(sourceRef);
                }
            }

            if (allowText) {
                List<TextElasticSearchResult> textResults = textElasticService.searchTexts(query, kbUid, null, null,
                        recallLimit, language == null ? null : List.of(language));
                for (TextElasticSearchResult withScore : textResults) {
                    TextElastic text = withScore.getTextElastic();
                    FaqProtobuf faqProtobuf = FaqProtobuf.fromText(text);
                    searchResultList.add(faqProtobuf);

                    RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                            .sourceType(RobotContent.SourceTypeEnum.TEXT)
                            .sourceUid(StringUtils.hasText(text.getSourceUid()) ? text.getSourceUid() : text.getUid())
                            .sourceName(text.getTitle())
                            .contentSummary(getContentSummary(text.getContent(), 200))
                            .language(text.getLanguage())
                            .searchChannel(RobotSearchTypeEnum.FULLTEXT.name())
                            .score((double) withScore.getScore())
                            .highlighted(false)
                            .build();
                    sourceReferences.add(sourceRef);
                }
            }

            if (allowChunk) {
                List<ChunkElasticSearchResult> chunkResults = chunkElasticService.searchChunks(query, kbUid, null, null,
                        recallLimit, language == null ? null : List.of(language));
                for (ChunkElasticSearchResult withScore : chunkResults) {
                    ChunkElastic chunk = withScore.getChunkElastic();
                    FaqProtobuf faqProtobuf = FaqProtobuf.fromChunk(chunk);
                    searchResultList.add(faqProtobuf);

                    RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                            .sourceType(RobotContent.SourceTypeEnum.CHUNK)
                            .sourceUid(StringUtils.hasText(chunk.getSourceUid()) ? chunk.getSourceUid() : chunk.getUid())
                            .sourceName(chunk.getName())
                            .fileName(chunk.getFileName())
                            .fileUrl(chunk.getFileUrl())
                            .fileUid(chunk.getFileUid())
                            .contentSummary(getContentSummary(chunk.getContent(), 200))
                            .language(chunk.getLanguage())
                            .searchChannel(RobotSearchTypeEnum.FULLTEXT.name())
                            .score((double) withScore.getScore())
                            .highlighted(false)
                            .build();
                    sourceReferences.add(sourceRef);
                }
            }

            if (allowWebpage) {
                List<WebpageElasticSearchResult> webpageResults = webpageElasticService.searchWebpage(query, kbUid, null,
                        null, recallLimit, language == null ? null : List.of(language));
                for (WebpageElasticSearchResult withScore : webpageResults) {
                    WebpageElastic webpage = withScore.getWebpageElastic();
                    FaqProtobuf faqProtobuf = FaqProtobuf.fromWebpage(webpage);
                    searchResultList.add(faqProtobuf);

                    RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                            .sourceType(RobotContent.SourceTypeEnum.WEBPAGE)
                            .sourceUid(StringUtils.hasText(webpage.getSourceUid()) ? webpage.getSourceUid() : webpage.getUid())
                            .sourceName(webpage.getTitle())
                            .contentSummary(getContentSummary(webpage.getContent(), 200))
                            .language(webpage.getLanguage())
                            .searchChannel(RobotSearchTypeEnum.FULLTEXT.name())
                            .score((double) withScore.getScore())
                            .highlighted(false)
                            .build();
                    sourceReferences.add(sourceRef);
                }
            }

            if (allowArticle) {
                // Article 没有 language 维度，不受 preferredLanguages 影响
                List<ArticleElasticSearchResult> articleResults = articleElasticService.searchArticle(query, kbUid,
                        null, null, recallLimit);
                for (ArticleElasticSearchResult withScore : articleResults) {
                    ArticleElastic article = withScore.getArticleElastic();
                    FaqProtobuf faqProtobuf = FaqProtobuf.fromArticle(article);
                    searchResultList.add(faqProtobuf);

                    RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                            .sourceType(RobotContent.SourceTypeEnum.ARTICLE)
                            .sourceUid(StringUtils.hasText(article.getUid()) ? article.getUid() : article.getUid())
                            .sourceName(article.getTitle())
                            .contentSummary(getContentSummary(
                                    article.getContentMarkdown() != null ? article.getContentMarkdown()
                                            : article.getSummary(),
                                    200))
                            .searchChannel(RobotSearchTypeEnum.FULLTEXT.name())
                            .score((double) withScore.getScore())
                            .highlighted(false)
                            .build();
                    sourceReferences.add(sourceRef);
                }
            }

            if (searchResultList.size() > resultSizeBefore) {
                log.debug("Fulltext language fallback hit: language={}, results={}", language,
                        searchResultList.size() - resultSizeBefore);
                break;
            }
        }
    }

    private List<String> buildLanguageFallbackOrder(List<String> preferredLanguages) {
        List<String> languageOrder = new ArrayList<>();
        if (preferredLanguages != null) {
            for (String language : preferredLanguages) {
                if (StringUtils.hasText(language)) {
                    languageOrder.add(language.trim().toUpperCase());
                }
            }
        }
        if (languageOrder.isEmpty()) {
            languageOrder.add(null);
        }
        return languageOrder;
    }

    /**
     * 执行向量搜索并填充结果与来源引用
     */
    public void executeVectorSearchWithSources(String query, RobotProtobuf robot, String kbUid,
            List<FaqProtobuf> searchResultList,
            List<RobotContent.SourceReference> sourceReferences,
            String sourceTypeFilter,
            List<String> preferredLanguages) {

        // 若指定了数据源类型，则只执行对应的向量检索（减少无谓召回）
        boolean allowAll = !StringUtils.hasText(sourceTypeFilter) || "ALL".equalsIgnoreCase(sourceTypeFilter);
        boolean allowFaq = allowAll || "FAQ".equalsIgnoreCase(sourceTypeFilter);
        boolean allowText = allowAll || "TEXT".equalsIgnoreCase(sourceTypeFilter);
        boolean allowChunk = allowAll || "CHUNK".equalsIgnoreCase(sourceTypeFilter);
        boolean allowWebpage = allowAll || "WEBPAGE".equalsIgnoreCase(sourceTypeFilter);
        boolean allowArticle = allowAll || "ARTICLE".equalsIgnoreCase(sourceTypeFilter);

        // Vector 召回数量：默认 5；若配置了 topK，则至少取 topK；并设置上限防止过大查询
        int recallLimit = DEFAULT_VECTOR_RECALL_LIMIT;
        try {
            Integer configuredTopK = (robot != null && robot.getLlm() != null) ? robot.getLlm().getTopK() : null;
            if (configuredTopK != null && configuredTopK > 0) {
                recallLimit = Math.max(DEFAULT_VECTOR_RECALL_LIMIT, configuredTopK);
            }
        } catch (Exception ex) {
            log.debug("Read robot topK failed, use default vector recall limit", ex);
        }
        recallLimit = Math.min(recallLimit, MAX_VECTOR_RECALL_LIMIT);

        List<String> languageFallbackOrder = buildLanguageFallbackOrder(preferredLanguages);
        for (String language : languageFallbackOrder) {
            int resultSizeBefore = searchResultList.size();

            if (allowFaq && faqVectorService != null) {
                try {
                    List<FaqVectorSearchResult> searchResults = faqVectorService.searchFaqVector(query, kbUid, null, null,
                            recallLimit, language);
                    for (FaqVectorSearchResult withScore : searchResults) {
                        FaqVector faqVector = withScore.getFaqVector();
                        FaqProtobuf faqProtobuf = FaqProtobuf.fromFaqVector(faqVector);
                        searchResultList.add(faqProtobuf);

                        RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                                .sourceType(RobotContent.SourceTypeEnum.FAQ)
                            .sourceUid(StringUtils.hasText(faqVector.getSourceUid()) ? faqVector.getSourceUid() : faqVector.getUid())
                                .sourceName(faqVector.getQuestion())
                                .contentSummary(getContentSummary(faqVector.getAnswer(), 200))
                                .language(faqVector.getLanguage())
                                .searchChannel(RobotSearchTypeEnum.VECTOR.name())
                                .score((double) withScore.getScore())
                                .highlighted(false)
                                .build();
                        sourceReferences.add(sourceRef);
                    }
                } catch (Exception e) {
                    log.warn("FaqVectorService search failed: {}", e.getMessage());
                }
            }

            if (allowText && textVectorService != null) {
                try {
                    List<TextVectorSearchResult> textResults = textVectorService.searchTextVector(query, kbUid, null, null,
                            recallLimit, language);
                    for (TextVectorSearchResult withScore : textResults) {
                        TextVector textVector = withScore.getTextVector();
                        FaqProtobuf faqProtobuf = FaqProtobuf.fromTextVector(textVector);
                        searchResultList.add(faqProtobuf);

                        RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                                .sourceType(RobotContent.SourceTypeEnum.TEXT)
                            .sourceUid(StringUtils.hasText(textVector.getSourceUid()) ? textVector.getSourceUid() : textVector.getUid())
                                .sourceName(textVector.getTitle())
                                .contentSummary(getContentSummary(textVector.getContent(), 200))
                                .language(textVector.getLanguage())
                                .searchChannel(RobotSearchTypeEnum.VECTOR.name())
                                .score((double) withScore.getScore())
                                .highlighted(false)
                                .build();
                        sourceReferences.add(sourceRef);
                    }
                } catch (Exception e) {
                    log.warn("TextVectorService search failed: {}", e.getMessage());
                }
            }

            if (allowChunk && chunkVectorService != null) {
                try {
                    List<ChunkVectorSearchResult> chunkResults = chunkVectorService.searchChunkVector(query, kbUid, null,
                            null, recallLimit, 0.0, language);
                    for (ChunkVectorSearchResult withScore : chunkResults) {
                        ChunkVector chunkVector = withScore.getChunkVector();
                        FaqProtobuf faqProtobuf = FaqProtobuf.fromChunkVector(chunkVector);
                        searchResultList.add(faqProtobuf);

                        RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                                .sourceType(RobotContent.SourceTypeEnum.CHUNK)
                            .sourceUid(StringUtils.hasText(chunkVector.getSourceUid()) ? chunkVector.getSourceUid() : chunkVector.getUid())
                                .sourceName(chunkVector.getName())
                                .fileName(chunkVector.getFileName())
                                .fileUrl(chunkVector.getFileUrl())
                                .fileUid(chunkVector.getFileUid())
                                .contentSummary(getContentSummary(chunkVector.getContent(), 200))
                                .language(chunkVector.getLanguage())
                                .searchChannel(RobotSearchTypeEnum.VECTOR.name())
                                .score((double) withScore.getScore())
                                .highlighted(false)
                                .build();
                        sourceReferences.add(sourceRef);
                    }
                } catch (Exception e) {
                    log.warn("ChunkVectorService search failed: {}", e.getMessage());
                }
            }

            if (allowWebpage && webpageVectorService != null) {
                try {
                    List<WebpageVectorSearchResult> webpageResults = webpageVectorService.searchWebpageVector(query, kbUid,
                            null, null, recallLimit, language);
                    for (WebpageVectorSearchResult withScore : webpageResults) {
                        WebpageVector webpageVector = withScore.getWebpageVector();
                        FaqProtobuf faqProtobuf = FaqProtobuf.fromWebpageVector(webpageVector);
                        searchResultList.add(faqProtobuf);

                        RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                                .sourceType(RobotContent.SourceTypeEnum.WEBPAGE)
                            .sourceUid(StringUtils.hasText(webpageVector.getSourceUid()) ? webpageVector.getSourceUid() : webpageVector.getUid())
                                .sourceName(webpageVector.getTitle())
                                .contentSummary(getContentSummary(webpageVector.getContent(), 200))
                                .language(webpageVector.getLanguage())
                                .searchChannel(RobotSearchTypeEnum.VECTOR.name())
                                .score((double) withScore.getScore())
                                .highlighted(false)
                                .build();
                        sourceReferences.add(sourceRef);
                    }
                } catch (Exception e) {
                    log.warn("WebpageVectorService search failed: {}", e.getMessage());
                }
            }

            if (allowArticle && articleVectorService != null) {
                try {
                    // Article 没有 language 维度，不受 preferredLanguages 影响
                    List<ArticleVectorSearchResult> articleResults = articleVectorService.searchArticleVector(query,
                            kbUid, null, null, recallLimit);
                    for (ArticleVectorSearchResult withScore : articleResults) {
                        ArticleVector articleVector = withScore.getArticleVector();
                        FaqProtobuf faqProtobuf = FaqProtobuf.fromArticleVector(articleVector);
                        searchResultList.add(faqProtobuf);

                        RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                                .sourceType(RobotContent.SourceTypeEnum.ARTICLE)
                                .sourceUid(StringUtils.hasText(articleVector.getUid()) ? articleVector.getUid() : articleVector.getUid())
                                .sourceName(articleVector.getTitle())
                                .contentSummary(getContentSummary(
                                        articleVector.getContentMarkdown() != null ? articleVector.getContentMarkdown()
                                                : articleVector.getSummary(),
                                        200))
                                .searchChannel(RobotSearchTypeEnum.VECTOR.name())
                                .score((double) withScore.getScore())
                                .highlighted(false)
                                .build();
                        sourceReferences.add(sourceRef);
                    }
                } catch (Exception e) {
                    log.warn("ArticleVectorService search failed: {}", e.getMessage());
                }
            }

            if (searchResultList.size() > resultSizeBefore) {
                log.debug("Vector language fallback hit: language={}, results={}", language,
                        searchResultList.size() - resultSizeBefore);
                break;
            }
        }
    }

    /**
     * 聚合/去重/重排并TopK输出
     */
    public SearchResultWithSources rerankMergeTopK(SearchResultWithSources raw, RobotProtobuf robot) {
        if (raw == null) {
            return new SearchResultWithSources(new ArrayList<>(), new ArrayList<>());
        }

        // 以 sourceUid 为主键进行聚合：同一内容来自不同通道时取最高分
        class Agg {
            FaqProtobuf faq;
            RobotContent.SourceReference bestSrc;
            double bestScore;
        }

        Map<String, Agg> aggMap = new LinkedHashMap<>();
        for (RobotContent.SourceReference src : raw.getSourceReferences()) {
            if (src == null || !StringUtils.hasText(src.getSourceUid()))
                continue;
            Agg a = aggMap.computeIfAbsent(src.getSourceUid(), k -> {
                Agg x = new Agg();
                x.bestScore = 0.0;
                return x;
            });
            double sc = src.getScore() != null ? src.getScore() : 0.0;
            if (a.bestSrc == null || sc > a.bestScore) {
                a.bestSrc = src;
                a.bestScore = sc;
            }
        }

        // 回填Faq对象，保证输出与uid一致
        for (FaqProtobuf faq : raw.getSearchResults()) {
            if (faq != null && StringUtils.hasText(faq.getUid())) {
                Agg a = aggMap.computeIfAbsent(faq.getUid(), k -> new Agg());
                a.faq = faq;
                if (a.bestSrc == null) {
                    // 占位来源，分数为0，便于前端结构统一
                    RobotContent.SourceReference placeholder = RobotContent.SourceReference.builder()
                            .sourceType(RobotContent.SourceTypeEnum.FAQ)
                            .sourceUid(StringUtils.hasText(faq.getSourceUid()) ? faq.getSourceUid() : faq.getUid())
                            .sourceName(faq.getQuestion())
                            .contentSummary(getContentSummary(faq.getAnswer(), 200))
                            .score(0.0)
                            .highlighted(false)
                            .build();
                    a.bestSrc = placeholder;
                }
            }
        }

        // 排序并截断TopK
        List<Agg> list = new ArrayList<>(aggMap.values());
        list.sort((a, b) -> Double.compare(b.bestScore, a.bestScore));

        int topK = 3;
        try {
            if (robot != null && robot.getLlm() != null && robot.getLlm().getTopK() != null
                    && robot.getLlm().getTopK() > 0) {
                topK = robot.getLlm().getTopK();
            }
        } catch (Exception ex) {
            log.debug("Read robot topK failed, use default", ex);
        }
        if (list.size() > topK) {
            list = list.subList(0, topK);
        }

        // 输出结果
        List<FaqProtobuf> outFaqs = new ArrayList<>();
        List<RobotContent.SourceReference> outSources = new ArrayList<>();
        for (Agg a : list) {
            if (a.faq != null && a.bestSrc != null) {
                outFaqs.add(a.faq);
                outSources.add(a.bestSrc);
            }
        }
        return new SearchResultWithSources(outFaqs, outSources);
    }

    /**
     * 简单内容摘要
     */
    private String getContentSummary(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
