package com.bytedesk.kbase.translation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.llm_chunk.ChunkRequest;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElasticService;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVectorService;
import com.bytedesk.kbase.llm_faq.FaqRequest;
import com.bytedesk.kbase.llm_faq.elastic.FaqElasticService;
import com.bytedesk.kbase.llm_faq.vector.FaqVectorService;
import com.bytedesk.kbase.llm_text.TextRequest;
import com.bytedesk.kbase.llm_text.elastic.TextElasticService;
import com.bytedesk.kbase.llm_text.vector.TextVectorService;
import com.bytedesk.kbase.llm_webpage.WebpageRequest;
import com.bytedesk.kbase.llm_webpage.elastic.WebpageElasticService;
import com.bytedesk.kbase.llm_webpage.vector.WebpageVectorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KbaseTranslationIndexBackfillService {

    private static final List<String> DEFAULT_SOURCE_TYPES = List.of(
            KbaseTranslationSourceTypeEnum.FAQ.name(),
            KbaseTranslationSourceTypeEnum.TEXT.name(),
            KbaseTranslationSourceTypeEnum.CHUNK.name(),
            KbaseTranslationSourceTypeEnum.WEBPAGE.name());

    private final KbaseRestService kbaseRestService;

    private final KbaseTranslationRepository kbaseTranslationRepository;

    private final FaqElasticService faqElasticService;

    private final FaqVectorService faqVectorService;

    private final TextElasticService textElasticService;

    private final TextVectorService textVectorService;

    private final ChunkElasticService chunkElasticService;

    private final ChunkVectorService chunkVectorService;

    private final WebpageElasticService webpageElasticService;

    private final WebpageVectorService webpageVectorService;

    public Map<String, Object> backfill(KbaseTranslationBackfillRequest request) {
        boolean includeFulltext = !Boolean.FALSE.equals(request.getIncludeFulltext());
        boolean includeVector = !Boolean.FALSE.equals(request.getIncludeVector());
        if (!includeFulltext && !includeVector) {
            throw new RuntimeException("includeFulltext/includeVector cannot both be false");
        }

        List<KbaseEntity> kbases = resolveKbases(request.getKbUid());
        List<String> sourceTypes = normalizeSourceTypes(request.getSourceTypes());

        List<Map<String, Object>> kbaseResults = new ArrayList<>();
        long successTranslationTotal = 0L;
        long executedTypeCount = 0L;

        for (KbaseEntity kbase : kbases) {
            Map<String, Object> kbaseResult = new LinkedHashMap<>();
            kbaseResult.put("kbUid", kbase.getUid());
            kbaseResult.put("kbName", kbase.getName());

            List<Map<String, Object>> typeResults = new ArrayList<>();
            for (String sourceType : sourceTypes) {
                Map<String, Object> typeResult = backfillSingleType(kbase.getUid(), sourceType, includeFulltext, includeVector);
                typeResults.add(typeResult);
                successTranslationTotal += ((Number) typeResult.getOrDefault("successTranslationCount", 0L)).longValue();
                if (Boolean.TRUE.equals(typeResult.get("executed"))) {
                    executedTypeCount++;
                }
            }

            kbaseResult.put("types", typeResults);
            kbaseResults.add(kbaseResult);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("kbUid", request.getKbUid());
        result.put("kbaseCount", kbases.size());
        result.put("sourceTypes", sourceTypes);
        result.put("includeFulltext", includeFulltext);
        result.put("includeVector", includeVector);
        result.put("executedTypeCount", executedTypeCount);
        result.put("successTranslationCount", successTranslationTotal);
        result.put("kbases", kbaseResults);
        return result;
    }

    private List<KbaseEntity> resolveKbases(String kbUid) {
        if (StringUtils.hasText(kbUid)) {
            return List.of(kbaseRestService.findByUid(kbUid)
                    .orElseThrow(() -> new RuntimeException("kbUid not found: " + kbUid)));
        }
        return kbaseRestService.findAllNotDeleted();
    }

    private List<String> normalizeSourceTypes(List<String> sourceTypes) {
        if (sourceTypes == null || sourceTypes.isEmpty()) {
            return DEFAULT_SOURCE_TYPES;
        }

        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String sourceType : sourceTypes) {
            if (!StringUtils.hasText(sourceType)) {
                continue;
            }
            String normalizedType = sourceType.trim().toUpperCase(Locale.ROOT);
            try {
                KbaseTranslationSourceTypeEnum.valueOf(normalizedType);
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException("unsupported sourceType: " + sourceType);
            }
            normalized.add(normalizedType);
        }

        if (normalized.isEmpty()) {
            return DEFAULT_SOURCE_TYPES;
        }
        return new ArrayList<>(normalized);
    }

    private Map<String, Object> backfillSingleType(String kbUid, String sourceType, boolean includeFulltext,
            boolean includeVector) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sourceType", sourceType);
        result.put("includeFulltext", includeFulltext);
        result.put("includeVector", includeVector);

        long successTranslationCount = kbaseTranslationRepository
                .countByKbase_UidAndSourceTypeAndTranslateStatusAndEnabledTrueAndDeletedFalse(
                        kbUid,
                        sourceType,
                        KbaseTranslationStatusEnum.SUCCESS.name());
        result.put("successTranslationCount", successTranslationCount);

        if (KbaseTranslationSourceTypeEnum.QUICK_REPLY.name().equals(sourceType)) {
            result.put("executed", false);
            result.put("unsupported", true);
            result.put("message", "quick reply translation backfill is not implemented yet");
            return result;
        }

        try {
            switch (KbaseTranslationSourceTypeEnum.valueOf(sourceType)) {
                case FAQ -> backfillFaq(kbUid, includeFulltext, includeVector);
                case TEXT -> backfillText(kbUid, includeFulltext, includeVector);
                case CHUNK -> backfillChunk(kbUid, includeFulltext, includeVector);
                case WEBPAGE -> backfillWebpage(kbUid, includeFulltext, includeVector);
                default -> throw new RuntimeException("unsupported sourceType: " + sourceType);
            }
            result.put("executed", true);
            result.put("success", true);
        } catch (Exception ex) {
            log.error("translation backfill failed: kbUid={}, sourceType={}, error={}", kbUid, sourceType, ex.getMessage(), ex);
            result.put("executed", true);
            result.put("success", false);
            result.put("error", ex.getMessage());
        }
        return result;
    }

    private void backfillFaq(String kbUid, boolean includeFulltext, boolean includeVector) {
        FaqRequest request = FaqRequest.builder().kbUid(kbUid).build();
        if (includeFulltext) {
            faqElasticService.updateAllIndex(request);
        }
        if (includeVector) {
            faqVectorService.updateAllVectorIndex(request);
        }
    }

    private void backfillText(String kbUid, boolean includeFulltext, boolean includeVector) {
        TextRequest request = TextRequest.builder().kbUid(kbUid).build();
        if (includeFulltext) {
            textElasticService.updateAllIndex(request);
        }
        if (includeVector) {
            textVectorService.updateAllVectorIndex(request);
        }
    }

    private void backfillChunk(String kbUid, boolean includeFulltext, boolean includeVector) {
        ChunkRequest request = ChunkRequest.builder().kbUid(kbUid).build();
        if (includeFulltext) {
            chunkElasticService.updateAllIndex(request);
        }
        if (includeVector) {
            chunkVectorService.updateAllVectorIndex(request);
        }
    }

    private void backfillWebpage(String kbUid, boolean includeFulltext, boolean includeVector) {
        WebpageRequest request = WebpageRequest.builder().kbUid(kbUid).build();
        if (includeFulltext) {
            webpageElasticService.updateAllIndex(request);
        }
        if (includeVector) {
            webpageVectorService.updateAllVectorIndex(request);
        }
    }
}