package com.bytedesk.kbase.translation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface KbaseTranslationRepository
        extends JpaRepository<KbaseTranslationEntity, Long>, JpaSpecificationExecutor<KbaseTranslationEntity> {

    Optional<KbaseTranslationEntity> findByUid(String uid);

    Optional<KbaseTranslationEntity> findByKbase_UidAndSourceUidAndSourceTypeAndTargetLanguageAndDeletedFalse(
            String kbUid,
            String sourceUid,
            String sourceType,
            String targetLanguage);

    Optional<KbaseTranslationEntity> findByKbase_UidAndSourceUidAndSourceTypeAndTargetLanguageAndEnabledTrueAndDeletedFalse(
            String kbUid,
            String sourceUid,
            String sourceType,
            String targetLanguage);

    /**
     * 包含软删除记录：用于恢复已删除的翻译记录并重新翻译
     */
    Optional<KbaseTranslationEntity> findByKbase_UidAndSourceUidAndSourceTypeAndTargetLanguage(
            String kbUid,
            String sourceUid,
            String sourceType,
            String targetLanguage);

    List<KbaseTranslationEntity> findByKbase_UidAndSourceUidAndSourceTypeAndDeletedFalse(
            String kbUid,
            String sourceUid,
            String sourceType);

    /**
     * 包含软删除记录：用于重新翻译时纳入已删除的目标记录
     */
    List<KbaseTranslationEntity> findByKbase_UidAndSourceUidAndSourceType(
            String kbUid,
            String sourceUid,
            String sourceType);

    long countByKbase_UidAndSourceTypeAndTranslateStatusAndEnabledTrueAndDeletedFalse(
            String kbUid,
            String sourceType,
            String translateStatus);

    /**
     * 查询指定状态的翻译记录（用于批量 LLM 翻译）
     */
    List<KbaseTranslationEntity> findByTranslateStatusAndDeletedFalse(String translateStatus);
}