package com.bytedesk.ai.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot.RobotSearchTypeEnum;
import com.bytedesk.core.message.content.RobotContent;
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
public class KnowledgeBaseSearchHelper {

    @Autowired
    private FaqElasticService faqElasticService;
    @Autowired
    private TextElasticService textElasticService;
    @Autowired
    private ChunkElasticService chunkElasticService;
    @Autowired
    private WebpageElasticService webpageElasticService;

    @Autowired(required = false)
    private FaqVectorService faqVectorService;
    @Autowired(required = false)
    private TextVectorService textVectorService;
    @Autowired(required = false)
    private ChunkVectorService chunkVectorService;
    @Autowired(required = false)
    private WebpageVectorService webpageVectorService;

    // 2. 知识库搜索相关方法
    protected List<FaqProtobuf> searchKnowledgeBase(String query, RobotProtobuf robot) {
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
    protected SearchResultWithSources searchKnowledgeBaseWithSources(String query, RobotProtobuf robot) {
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
                executeVectorSearchWithSources(query, robot.getKbUid(), searchResultList,
                        sourceReferences);
                break;
            case MIXED:
                log.info("使用混合搜索");
                executeFulltextSearchWithSources(query, robot.getKbUid(), searchResultList,
                        sourceReferences);
                executeVectorSearchWithSources(query, robot.getKbUid(), searchResultList,
                        sourceReferences);
                break;
            case FULLTEXT:
            default:
                log.info("使用全文搜索");
                executeFulltextSearchWithSources(query, robot.getKbUid(), searchResultList,
                        sourceReferences);
                break;
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
        } catch (Exception ignored) {}

        // 1) 先对来源进行 uid 聚合：同一内容保留分数最高的一条
        Map<String, RobotContent.SourceReference> bestSrcByUid = new LinkedHashMap<>();
        for (RobotContent.SourceReference src : sourceReferences) {
            if (src == null || !StringUtils.hasText(src.getSourceUid())) continue;
            RobotContent.SourceReference existing = bestSrcByUid.get(src.getSourceUid());
            double s = src.getScore() != null ? src.getScore() : 0.0;
            if (existing == null) {
                bestSrcByUid.put(src.getSourceUid(), src);
            } else {
                double old = existing.getScore() != null ? existing.getScore() : 0.0;
                if (s > old) bestSrcByUid.put(src.getSourceUid(), src);
            }
        }

        // 2) 应用 scoreThreshold 与 topP 过滤
        double maxScore = 0.0;
        for (RobotContent.SourceReference s : bestSrcByUid.values()) {
            double sc = s.getScore() != null ? s.getScore() : 0.0;
            if (sc > maxScore) maxScore = sc;
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
            if (sc >= finalCut) filteredSources.add(s);
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
            if (faq == null || !StringUtils.hasText(faq.getUid())) continue;
            faqByUidFirst.putIfAbsent(faq.getUid(), faq);
        }

        // 6) 根据过滤后的来源顺序构建对应的 Faq 列表，确保两者对齐
        List<FaqProtobuf> filteredFaqs = new ArrayList<>();
        for (RobotContent.SourceReference s : filteredSources) {
            FaqProtobuf f = faqByUidFirst.get(s.getSourceUid());
            if (f != null) filteredFaqs.add(f);
        }

        return new SearchResultWithSources(filteredFaqs, filteredSources);
    }


    /**
     * 执行全文搜索并填充结果与来源引用
     */
    public void executeFulltextSearchWithSources(String query, String kbUid,
            List<FaqProtobuf> searchResultList,
            List<RobotContent.SourceReference> sourceReferences) {
        List<FaqElasticSearchResult> searchResults = faqElasticService.searchFaq(query, kbUid, null, null);
        for (FaqElasticSearchResult withScore : searchResults) {
            FaqElastic faq = withScore.getFaqElastic();
            FaqProtobuf faqProtobuf = FaqProtobuf.fromElastic(faq);
            searchResultList.add(faqProtobuf);

            RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                    .sourceType(RobotContent.SourceTypeEnum.FAQ)
                    .sourceUid(faq.getUid())
                    .sourceName(faq.getQuestion())
                    .contentSummary(getContentSummary(faq.getAnswer(), 200))
                    .score((double) withScore.getScore())
                    .highlighted(false)
                    .build();
            sourceReferences.add(sourceRef);
        }

        List<TextElasticSearchResult> textResults = textElasticService.searchTexts(query, kbUid, null, null);
        for (TextElasticSearchResult withScore : textResults) {
            TextElastic text = withScore.getTextElastic();
            FaqProtobuf faqProtobuf = FaqProtobuf.fromText(text);
            searchResultList.add(faqProtobuf);

            RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                    .sourceType(RobotContent.SourceTypeEnum.TEXT)
                    .sourceUid(text.getUid())
                    .sourceName(text.getTitle())
                    .contentSummary(getContentSummary(text.getContent(), 200))
                    .score((double) withScore.getScore())
                    .highlighted(false)
                    .build();
            sourceReferences.add(sourceRef);
        }

        List<ChunkElasticSearchResult> chunkResults = chunkElasticService.searchChunks(query, kbUid, null, null);
        for (ChunkElasticSearchResult withScore : chunkResults) {
            ChunkElastic chunk = withScore.getChunkElastic();
            FaqProtobuf faqProtobuf = FaqProtobuf.fromChunk(chunk);
            searchResultList.add(faqProtobuf);

            RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                    .sourceType(RobotContent.SourceTypeEnum.CHUNK)
                    .sourceUid(chunk.getUid())
                    .sourceName(chunk.getName())
                    .fileName(chunk.getFileName())
                    .fileUrl(chunk.getFileUrl())
                    .fileUid(chunk.getFileUid())
                    .contentSummary(getContentSummary(chunk.getContent(), 200))
                    .score((double) withScore.getScore())
                    .highlighted(false)
                    .build();
            sourceReferences.add(sourceRef);
        }

        List<WebpageElasticSearchResult> webpageResults = webpageElasticService.searchWebpage(query, kbUid, null, null);
        for (WebpageElasticSearchResult withScore : webpageResults) {
            WebpageElastic webpage = withScore.getWebpageElastic();
            FaqProtobuf faqProtobuf = FaqProtobuf.fromWebpage(webpage);
            searchResultList.add(faqProtobuf);

            RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                    .sourceType(RobotContent.SourceTypeEnum.WEBPAGE)
                    .sourceUid(webpage.getUid())
                    .sourceName(webpage.getTitle())
                    .contentSummary(getContentSummary(webpage.getContent(), 200))
                    .score((double) withScore.getScore())
                    .highlighted(false)
                    .build();
            sourceReferences.add(sourceRef);
        }
    }

    /**
     * 执行向量搜索并填充结果与来源引用
     */
    public void executeVectorSearchWithSources(String query, String kbUid,
            List<FaqProtobuf> searchResultList,
            List<RobotContent.SourceReference> sourceReferences) {
        if (faqVectorService != null) {
            try {
                List<FaqVectorSearchResult> searchResults = faqVectorService.searchFaqVector(query, kbUid, null, null, 5);
                for (FaqVectorSearchResult withScore : searchResults) {
                    FaqVector faqVector = withScore.getFaqVector();
                    FaqProtobuf faqProtobuf = FaqProtobuf.fromFaqVector(faqVector);
                    searchResultList.add(faqProtobuf);

                    RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                            .sourceType(RobotContent.SourceTypeEnum.FAQ)
                            .sourceUid(faqVector.getUid())
                            .sourceName(faqVector.getQuestion())
                            .contentSummary(getContentSummary(faqVector.getAnswer(), 200))
                            .score((double) withScore.getScore())
                            .highlighted(false)
                            .build();
                    sourceReferences.add(sourceRef);
                }
            } catch (Exception e) {
                log.warn("FaqVectorService search failed: {}", e.getMessage());
            }
        }

        if (textVectorService != null) {
            try {
                List<TextVectorSearchResult> textResults = textVectorService.searchTextVector(query, kbUid, null, null, 5);
                for (TextVectorSearchResult withScore : textResults) {
                    TextVector textVector = withScore.getTextVector();
                    FaqProtobuf faqProtobuf = FaqProtobuf.fromTextVector(textVector);
                    searchResultList.add(faqProtobuf);

                    RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                            .sourceType(RobotContent.SourceTypeEnum.TEXT)
                            .sourceUid(textVector.getUid())
                            .sourceName(textVector.getTitle())
                            .contentSummary(getContentSummary(textVector.getContent(), 200))
                            .score((double) withScore.getScore())
                            .highlighted(false)
                            .build();
                    sourceReferences.add(sourceRef);
                }
            } catch (Exception e) {
                log.warn("TextVectorService search failed: {}", e.getMessage());
            }
        }

        if (chunkVectorService != null) {
            try {
                List<ChunkVectorSearchResult> chunkResults = chunkVectorService.searchChunkVector(query, kbUid, null, null, 5);
                for (ChunkVectorSearchResult withScore : chunkResults) {
                    ChunkVector chunkVector = withScore.getChunkVector();
                    FaqProtobuf faqProtobuf = FaqProtobuf.fromChunkVector(chunkVector);
                    searchResultList.add(faqProtobuf);

                    RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                            .sourceType(RobotContent.SourceTypeEnum.CHUNK)
                            .sourceUid(chunkVector.getUid())
                            .sourceName(chunkVector.getName())
                            .fileName(chunkVector.getFileName())
                            .fileUrl(chunkVector.getFileUrl())
                            .fileUid(chunkVector.getFileUid())
                            .contentSummary(getContentSummary(chunkVector.getContent(), 200))
                            .score((double) withScore.getScore())
                            .highlighted(false)
                            .build();
                    sourceReferences.add(sourceRef);
                }
            } catch (Exception e) {
                log.warn("ChunkVectorService search failed: {}", e.getMessage());
            }
        }

        if (webpageVectorService != null) {
            try {
                List<WebpageVectorSearchResult> webpageResults = webpageVectorService.searchWebpageVector(query, kbUid, null, null, 5);
                for (WebpageVectorSearchResult withScore : webpageResults) {
                    WebpageVector webpageVector = withScore.getWebpageVector();
                    FaqProtobuf faqProtobuf = FaqProtobuf.fromWebpageVector(webpageVector);
                    searchResultList.add(faqProtobuf);

                    RobotContent.SourceReference sourceRef = RobotContent.SourceReference.builder()
                            .sourceType(RobotContent.SourceTypeEnum.WEBPAGE)
                            .sourceUid(webpageVector.getUid())
                            .sourceName(webpageVector.getTitle())
                            .contentSummary(getContentSummary(webpageVector.getContent(), 200))
                            .score((double) withScore.getScore())
                            .highlighted(false)
                            .build();
                    sourceReferences.add(sourceRef);
                }
            } catch (Exception e) {
                log.warn("WebpageVectorService search failed: {}", e.getMessage());
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
            if (src == null || !StringUtils.hasText(src.getSourceUid())) continue;
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
                            .sourceUid(faq.getUid())
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
        } catch (Exception ignored) {}
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
