package com.bytedesk.kbase.translation;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.kbase.article.ArticleEntity;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.llm_chunk.ChunkEntity;
import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.llm_text.TextEntity;
import com.bytedesk.kbase.llm_webpage.WebpageEntity;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KbaseTranslationSyncService {

    private final KbaseTranslationRepository translationRepository;

    @Transactional
    public void syncFaq(FaqEntity faq) {
        if (faq == null || faq.getKbase() == null || !StringUtils.hasText(faq.getUid())) {
            return;
        }

        KbaseEntity kbase = faq.getKbase();
        String sourceLanguage = resolveSourceLanguage(kbase);
        // String summary = CollectionUtils.isEmpty(faq.getSimilarQuestions())
        //         ? null
        //         : String.join(" | ", faq.getSimilarQuestions());

        upsertTargetPlaceholders(
                kbase,
                faq.getUid(),
                KbaseTranslationSourceTypeEnum.FAQ.name(),
                sourceLanguage,
                faq.getOrgUid(),
                faq.getUserUid(),
                faq.getEnabled());
    }

    @Transactional
    public void syncArticle(ArticleEntity article) {
        if (article == null || article.getKbase() == null || !StringUtils.hasText(article.getUid())) {
            return;
        }

        KbaseEntity kbase = article.getKbase();
        String sourceLanguage = resolveSourceLanguage(kbase);

        upsertTargetPlaceholders(
                kbase,
                article.getUid(),
                KbaseTranslationSourceTypeEnum.ARTICLE.name(),
                sourceLanguage,
                article.getOrgUid(),
                article.getUserUid(),
            Boolean.TRUE);
    }

    @Transactional
    public void syncText(TextEntity text) {
        if (text == null || text.getKbase() == null || !StringUtils.hasText(text.getUid())) {
            return;
        }

        KbaseEntity kbase = text.getKbase();
        String sourceLanguage = resolveSourceLanguage(kbase);

        upsertTargetPlaceholders(
                kbase,
                text.getUid(),
                KbaseTranslationSourceTypeEnum.TEXT.name(),
                sourceLanguage,
                text.getOrgUid(),
                text.getUserUid(),
                text.getEnabled());
    }

    @Transactional
    public void syncChunk(ChunkEntity chunk) {
        if (chunk == null || chunk.getKbase() == null || !StringUtils.hasText(chunk.getUid())) {
            return;
        }

        KbaseEntity kbase = chunk.getKbase();
        String sourceLanguage = resolveSourceLanguage(kbase);

        upsertTargetPlaceholders(
                kbase,
                chunk.getUid(),
                KbaseTranslationSourceTypeEnum.CHUNK.name(),
                sourceLanguage,
                chunk.getOrgUid(),
                chunk.getUserUid(),
                chunk.getEnabled());
    }

    @Transactional
    public void syncWebpage(WebpageEntity webpage) {
        if (webpage == null || webpage.getKbase() == null || !StringUtils.hasText(webpage.getUid())) {
            return;
        }

        KbaseEntity kbase = webpage.getKbase();
        String sourceLanguage = resolveSourceLanguage(kbase);

        upsertTargetPlaceholders(
                kbase,
                webpage.getUid(),
                KbaseTranslationSourceTypeEnum.WEBPAGE.name(),
                sourceLanguage,
                webpage.getOrgUid(),
                webpage.getUserUid(),
                webpage.getEnabled());
    }

    @Transactional
    public void syncSource(
            KbaseEntity kbase,
            String sourceUid,
            String sourceType,
            String orgUid,
            String userUid,
            Boolean enabled) {
        if (kbase == null || !StringUtils.hasText(sourceUid) || !StringUtils.hasText(sourceType)) {
            return;
        }

        String sourceLanguage = resolveSourceLanguage(kbase);
        upsertTargetPlaceholders(
                kbase,
                sourceUid,
                sourceType,
                sourceLanguage,
                orgUid,
                userUid,
                enabled);
    }

    private void upsertTargetPlaceholders(
            KbaseEntity kbase,
            String sourceUid,
            String sourceType,
            String sourceLanguage,
            String orgUid,
            String userUid,
            Boolean enabled) {
        if (!Boolean.TRUE.equals(kbase.getAutoTranslateEnabled()) || CollectionUtils.isEmpty(kbase.getTargetLanguages())) {
            return;
        }

        for (String targetLanguage : kbase.getTargetLanguages()) {
            if (!StringUtils.hasText(targetLanguage) || sourceLanguage.equalsIgnoreCase(targetLanguage)) {
                continue;
            }

            KbaseTranslationEntity entity = findOrCreate(kbase, sourceUid, sourceType, targetLanguage);
            entity.setOrgUid(orgUid);
            entity.setUserUid(userUid);
            entity.setSourceLanguage(sourceLanguage);
            entity.setTargetLanguage(targetLanguage);
            entity.setTranslateStatus(KbaseTranslationStatusEnum.NEW.name());
            entity.setTranslateProvider(null);
            entity.setTranslatedAt(null);
            entity.setErrorMessage(null);
            entity.setEnabled(enabled);
            entity.setTitle(null);
            entity.setSummary(null);
            entity.setContent(null);
            entity.setContentHtml(null);
            entity.setContentMarkdown(null);
            entity.setTagList(new ArrayList<>());
            translationRepository.save(entity);
        }
    }

    private KbaseTranslationEntity findOrCreate(KbaseEntity kbase, String sourceUid, String sourceType, String targetLanguage) {
        // 1. 优先查找未删除的记录
        Optional<KbaseTranslationEntity> optional = translationRepository
                .findByKbase_UidAndSourceUidAndSourceTypeAndTargetLanguageAndDeletedFalse(
                        kbase.getUid(),
                        sourceUid,
                        sourceType,
                        targetLanguage);
        if (optional.isPresent()) {
            return optional.get();
        }

        // 2. 查找软删除的记录，找到则恢复并返回托管实体
        Optional<KbaseTranslationEntity> deleted = translationRepository
                .findByKbase_UidAndSourceUidAndSourceTypeAndTargetLanguage(
                        kbase.getUid(),
                        sourceUid,
                        sourceType,
                        targetLanguage);
        if (deleted.isPresent()) {
            KbaseTranslationEntity entity = deleted.get();
            entity.setDeleted(false);
            return translationRepository.save(entity);
        }

        // 3. 新建
        KbaseTranslationEntity entity = KbaseTranslationEntity.builder()
                .uid(sourceUid + "_" + sourceType + "_" + targetLanguage)
                .kbase(kbase)
                .sourceUid(sourceUid)
                .sourceType(sourceType)
                .targetLanguage(targetLanguage)
                .enabled(true)
                .build();

        try {
            return translationRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            // 并发/重复写入场景：回退查询已持久化的记录
            return translationRepository
                    .findByKbase_UidAndSourceUidAndSourceTypeAndTargetLanguageAndDeletedFalse(
                            kbase.getUid(),
                            sourceUid,
                            sourceType,
                            targetLanguage)
                    .orElseThrow(() -> e);
        }
    }

    private String resolveSourceLanguage(KbaseEntity kbase) {
        if (kbase != null && StringUtils.hasText(kbase.getSourceLanguage())) {
            return kbase.getSourceLanguage();
        }
        if (kbase != null && StringUtils.hasText(kbase.getLanguage())) {
            return kbase.getLanguage();
        }
        return LanguageEnum.ZH_CN.name();
    }
}