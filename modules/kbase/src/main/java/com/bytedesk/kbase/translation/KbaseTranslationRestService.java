package com.bytedesk.kbase.translation;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class KbaseTranslationRestService
        extends BaseRestService<KbaseTranslationEntity, KbaseTranslationRequest, KbaseTranslationResponse> {

    private final KbaseTranslationRepository translationRepository;

    private final KbaseRepository kbaseRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final KbaseTranslationIndexService translationIndexService;

    @Override
    protected Specification<KbaseTranslationEntity> createSpecification(KbaseTranslationRequest request) {
        return KbaseTranslationSpecification.search(request, authService);
    }

    @Override
    protected Page<KbaseTranslationEntity> executePageQuery(Specification<KbaseTranslationEntity> spec, Pageable pageable) {
        return translationRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "kbaseTranslation", key = "#uid", unless = "#result == null")
    @Override
    public Optional<KbaseTranslationEntity> findByUid(String uid) {
        return translationRepository.findByUid(uid);
    }

    @Override
    public KbaseTranslationResponse create(KbaseTranslationRequest request) {
        if (!StringUtils.hasText(request.getKbUid())) {
            throw new RuntimeException("kbUid is required");
        }
        if (!StringUtils.hasText(request.getSourceUid())) {
            throw new RuntimeException("sourceUid is required");
        }
        if (!StringUtils.hasText(request.getTargetLanguage())) {
            throw new RuntimeException("targetLanguage is required");
        }

        Optional<KbaseTranslationEntity> existing = translationRepository
                .findByKbase_UidAndSourceUidAndSourceTypeAndTargetLanguageAndDeletedFalse(
                        request.getKbUid(),
                        request.getSourceUid(),
                        request.getSourceType(),
                        request.getTargetLanguage());
        if (existing.isPresent()) {
            return updateExisting(existing.get(), request);
        }

        UserEntity user = authService.getUser();
        KbaseEntity kbase = resolveKbase(request.getKbUid());

        KbaseTranslationEntity entity = modelMapper.map(request, KbaseTranslationEntity.class);
        entity.setUid(uidUtils.getUid());
        entity.setKbase(kbase);
        if (user != null) {
            entity.setOrgUid(user.getOrgUid());
            entity.setUserUid(user.getUid());
        }

        KbaseTranslationEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create kbase translation failed");
        }
        triggerReindexIfSuccess(savedEntity);
        return convertToResponse(savedEntity);
    }

    @Override
    public KbaseTranslationResponse update(KbaseTranslationRequest request) {
        Optional<KbaseTranslationEntity> optional = translationRepository.findByUid(request.getUid());
        if (optional.isEmpty()) {
            throw new RuntimeException("KbaseTranslation not found");
        }

        return updateExisting(optional.get(), request);
    }

    private KbaseTranslationResponse updateExisting(KbaseTranslationEntity entity, KbaseTranslationRequest request) {
        if (StringUtils.hasText(request.getKbUid())) {
            entity.setKbase(resolveKbase(request.getKbUid()));
        }
        entity.setSourceUid(request.getSourceUid());
        entity.setSourceType(request.getSourceType());
        entity.setSourceLanguage(request.getSourceLanguage());
        entity.setTargetLanguage(request.getTargetLanguage());
        entity.setTitle(request.getTitle());
        entity.setSummary(request.getSummary());
        entity.setContent(request.getContent());
        entity.setContentHtml(request.getContentHtml());
        entity.setContentMarkdown(request.getContentMarkdown());
        entity.setTagList(request.getTagList());
        entity.setTranslateStatus(request.getTranslateStatus());
        entity.setTranslateProvider(request.getTranslateProvider());
        entity.setTranslatedAt(request.getTranslatedAt());
        entity.setErrorMessage(request.getErrorMessage());
        entity.setEnabled(request.getEnabled());

        KbaseTranslationEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Update kbase translation failed");
        }
        triggerReindexIfSuccess(savedEntity);
        return convertToResponse(savedEntity);
    }

    @Override
    public KbaseTranslationEntity save(KbaseTranslationEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @CachePut(value = "kbaseTranslation", key = "#entity.uid")
    @Override
    protected KbaseTranslationEntity doSave(KbaseTranslationEntity entity) {
        return translationRepository.save(entity);
    }

    @CacheEvict(value = "kbaseTranslation", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<KbaseTranslationEntity> optional = translationRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            return;
        }
        throw new RuntimeException("KbaseTranslation not found");
    }

    @Override
    public void delete(KbaseTranslationRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public KbaseTranslationEntity handleOptimisticLockingFailureException(
            ObjectOptimisticLockingFailureException e,
            KbaseTranslationEntity entity) {
        try {
            Optional<KbaseTranslationEntity> latest = translationRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                return translationRepository.save(latest.get());
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public KbaseTranslationResponse convertToResponse(KbaseTranslationEntity entity) {
        KbaseTranslationResponse response = modelMapper.map(entity, KbaseTranslationResponse.class);
        if (entity.getKbase() != null) {
            response.setKbUid(entity.getKbase().getUid());
        }
        return response;
    }

    private KbaseEntity resolveKbase(String kbUid) {
        return kbaseRepository.findByUid(kbUid)
                .orElseThrow(() -> new RuntimeException("kbUid not found"));
    }

    /**
     * 翻译状态为 SUCCESS 时，自动触发源实体的全文索引和向量索引更新
     */
    private void triggerReindexIfSuccess(KbaseTranslationEntity entity) {
        if (!KbaseTranslationStatusEnum.SUCCESS.name().equals(entity.getTranslateStatus())) {
            return;
        }
        if (entity.getSourceUid() == null || entity.getSourceType() == null) {
            return;
        }
        try {
            translationIndexService.reindexTranslationSource(entity.getSourceUid(), entity.getSourceType());
        } catch (Exception e) {
            log.error("translation auto-reindex failed: translationUid={}, sourceUid={}, sourceType={}, error={}",
                    entity.getUid(), entity.getSourceUid(), entity.getSourceType(), e.getMessage(), e);
        }
    }
}