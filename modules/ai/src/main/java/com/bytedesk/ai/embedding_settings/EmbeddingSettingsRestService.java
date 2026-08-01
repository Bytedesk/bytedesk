/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.embedding_settings;

import java.util.Optional;
import java.util.Objects;
import java.util.Locale;

import org.modelmapper.ModelMapper;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.springai.providers.dashscope.BytedeskDashScopeEmbeddingModel;
import com.bytedesk.ai.springai.providers.dashscope.BytedeskDashScopeEmbeddingOptions;
import com.bytedesk.ai.springai.providers.openai.OpenAiCompatibleModelFactory;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.llm.LlmConfigUtils;
import com.bytedesk.core.llm.LlmDefaults;
import com.bytedesk.core.llm.LlmProviderConfigDefault;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingSettingsRestService extends BaseRestServiceWithExport<EmbeddingSettingsEntity, EmbeddingSettingsRequest, EmbeddingSettingsResponse, EmbeddingSettingsExcel> {

    private static final String DEFAULT_KBASE_EMBEDDING_SETTINGS_NAME = "default-kbase-embedding-settings";

    private static final String DEFAULT_KBASE_EMBEDDING_SETTINGS_UID = "df_embedding_settings_kbase_uid";

    private final EmbeddingSettingsRepository embedding_settingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final Environment environment;

    private final EmbeddingSettingsReindexService embeddingSettingsReindexService;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<EmbeddingSettingsEntity> queryByOrgEntity(EmbeddingSettingsRequest request) {
        request.setLevel(LevelEnum.PLATFORM.name());
        request.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        request.setUserUid(null);
        Pageable pageable = request.getPageable();
        Specification<EmbeddingSettingsEntity> specs = EmbeddingSettingsSpecification.search(request, authService);
        return embedding_settingsRepository.findAll(specs, pageable);
    }

    @Override
    public Page<EmbeddingSettingsResponse> queryByOrg(EmbeddingSettingsRequest request) {
        Page<EmbeddingSettingsEntity> embedding_settingsPage = queryByOrgEntity(request);
        return embedding_settingsPage.map(this::convertToResponse);
    }

    @Override
    public Page<EmbeddingSettingsResponse> queryByUser(EmbeddingSettingsRequest request) {
        return queryByOrg(request);
    }

    @Cacheable(value = "embedding_settings", key = "#uid", unless="#result==null")
    @Override
    public Optional<EmbeddingSettingsEntity> findByUid(String uid) {
        return embedding_settingsRepository.findByUid(uid);
    }

    @Cacheable(value = "embedding_settings", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<EmbeddingSettingsEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return embedding_settingsRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    @Cacheable(value = "embedding_settings", key = "#name + '_' + #level + '_' + #type", unless="#result==null")
    public Optional<EmbeddingSettingsEntity> findByNameAndLevelAndType(String name, String level, String type) {
        return embedding_settingsRepository.findByNameAndLevelAndTypeAndDeletedFalse(name, level, type);
    }

    public Optional<EmbeddingSettingsEntity> findDefaultByLevelAndType(String level, String type) {
        return embedding_settingsRepository.findFirstByLevelAndTypeAndDefaultSettingsTrueAndEnabledTrueAndDeletedFalse(level, type);
    }

    public Optional<EmbeddingSettingsEntity> findDefaultByOrgUidAndType(String orgUid, String type) {
        return embedding_settingsRepository.findFirstByOrgUidAndTypeAndDefaultSettingsTrueAndEnabledTrueAndDeletedFalse(orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return embedding_settingsRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public EmbeddingSettingsResponse create(EmbeddingSettingsRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public EmbeddingSettingsResponse createSystemEmbeddingSettings(EmbeddingSettingsRequest request) {
        return createInternal(request, true);
    }

    private EmbeddingSettingsResponse createInternal(EmbeddingSettingsRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        applyDefaults(request);

        // 检查name+level+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getLevel()) && StringUtils.hasText(request.getType())) {
            Optional<EmbeddingSettingsEntity> embedding_settings = findByNameAndLevelAndType(request.getName(), request.getLevel(), request.getType());
            if (embedding_settings.isPresent()) {
                return convertToResponse(embedding_settings.get());
            }
        }
        
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }

        requireSuperUser(skipPermissionCheck);
        
        // Embedding 配置仅允许超级管理员维护平台级统一配置
        String level = LevelEnum.PLATFORM.name();
        request.setLevel(level);
        request.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        
        // 检查用户是否有权限创建该层级的数据
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(EmbeddingSettingsPermissions.MODULE_NAME, level)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }
        
        EmbeddingSettingsEntity entity = modelMapper.map(request, EmbeddingSettingsEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        clearOtherDefaultsIfNeeded(entity);
        // 
        EmbeddingSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public EmbeddingSettingsResponse update(EmbeddingSettingsRequest request) {
        requireSuperUser(false);
        Optional<EmbeddingSettingsEntity> optional = embedding_settingsRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            EmbeddingSettingsEntity entity = optional.get();
            EmbeddingSettingsSnapshot previousEffectiveSnapshot = getEffectiveDefaultSnapshot();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(EmbeddingSettingsPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
            }
            
            applyDefaults(request);
            modelMapper.map(request, entity);
            clearOtherDefaultsIfNeeded(entity);
            //
            EmbeddingSettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            triggerReindexIfNeeded(previousEffectiveSnapshot, savedEntity);
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    protected EmbeddingSettingsEntity doSave(EmbeddingSettingsEntity entity) {
        return embedding_settingsRepository.save(entity);
    }

    @Override
    public EmbeddingSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, EmbeddingSettingsEntity entity) {
        try {
            Optional<EmbeddingSettingsEntity> latest = embedding_settingsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                EmbeddingSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                latestEntity.setDescription(entity.getDescription());
                latestEntity.setType(entity.getType());
                latestEntity.setProvider(entity.getProvider());
                latestEntity.setModel(entity.getModel());
                latestEntity.setBaseUrl(entity.getBaseUrl());
                latestEntity.setApiKey(entity.getApiKey());
                latestEntity.setDimensions(entity.getDimensions());
                latestEntity.setVectorStoreType(entity.getVectorStoreType());
                latestEntity.setVectorStoreIndexName(entity.getVectorStoreIndexName());
                latestEntity.setVectorStoreDimensions(entity.getVectorStoreDimensions());
                latestEntity.setDefaultSettings(entity.getDefaultSettings());
                latestEntity.setEnabled(entity.getEnabled());
                return embedding_settingsRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        requireSuperUser(false);
        Optional<EmbeddingSettingsEntity> optional = embedding_settingsRepository.findByUid(uid);
        if (optional.isPresent()) {
            EmbeddingSettingsEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(EmbeddingSettingsPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
            }
            
            entity.setDeleted(true);
            save(entity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(EmbeddingSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    /**
     * 测试向量化是否正常
     * 使用与实际向量索引相同的配置查找逻辑：defaultSettings=true 且 enabled=true
     */
    public EmbeddingSettingsTestResponse testVectorization(EmbeddingSettingsRequest request) {
        requireSuperUser(false);

        // 与实际 FaqVectorService → EmbeddingSettingsKbaseVectorStoreResolver 使用相同的查找逻辑
        Optional<EmbeddingSettingsEntity> optional = findDefaultByLevelAndType(LevelEnum.PLATFORM.name(), EmbeddingSettingsTypeEnum.KBASE.name());
        if (optional.isEmpty() || optional.get().isDeleted() || !Boolean.TRUE.equals(optional.get().getEnabled())) {
            return EmbeddingSettingsTestResponse.fail(
                "未找到默认启用的 Embedding 配置 (defaultSettings=true, enabled=true)。请在 Embedding Settings 页面设置一条记录为默认并启用。");
        }
        EmbeddingSettingsEntity entity = optional.get();

        log.info("测试向量化: uid={}, provider={}, model={}, apiKey={}",
                entity.getUid(), entity.getProvider(), entity.getModel(),
                StringUtils.hasText(entity.getApiKey()) ? "***" + entity.getApiKey().substring(Math.max(0, entity.getApiKey().length() - 4)) : "(empty)");

        try {
            EmbeddingModel embeddingModel = buildEmbeddingModel(entity);
            embeddingModel.embed("test");
            return EmbeddingSettingsTestResponse.success("向量化测试成功 (provider=" + entity.getProvider() + ", model=" + entity.getModel() + ")");
        } catch (Exception e) {
            log.warn("EmbeddingSettings vectorization test failed: uid={}, provider={}, model={}, error={}",
                    entity.getUid(), entity.getProvider(), entity.getModel(), e.getMessage());
            String errorMsg = e.getMessage();
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                errorMsg = e.getCause().getMessage();
            }
            return EmbeddingSettingsTestResponse.fail(errorMsg);
        }
    }

    // ========== EmbeddingModel 构建方法（不依赖 Elasticsearch） ==========

    private EmbeddingModel buildEmbeddingModel(EmbeddingSettingsEntity settings) {
        String provider = resolveProvider(settings);
        if (LlmProviderConstants.DASHSCOPE.equals(provider)) {
            return buildDashscopeEmbeddingModel(settings);
        }
        if (LlmProviderConstants.ZHIPUAI.equals(provider)) {
            return buildZhipuaiEmbeddingModel(settings);
        }
        if (LlmProviderConstants.OLLAMA.equals(provider)) {
            return buildOllamaEmbeddingModel(settings);
        }
        return buildOpenAiCompatibleEmbeddingModel(settings, provider);
    }

    private EmbeddingModel buildDashscopeEmbeddingModel(EmbeddingSettingsEntity settings) {
        BytedeskDashScopeEmbeddingOptions.BytedeskDashScopeEmbeddingOptionsBuilder optionsBuilder = BytedeskDashScopeEmbeddingOptions.builder()
            .model(resolveModel(settings, "text-embedding-v4"));
        Integer dimensions = settings.getDimensions();
        if (dimensions != null && dimensions > 0) {
            optionsBuilder.dimensions(dimensions);
        }
        return new BytedeskDashScopeEmbeddingModel(
            resolveBaseUrl(settings, "https://dashscope.aliyuncs.com"),
            resolveApiKey(settings),
            optionsBuilder.build());
    }

    private EmbeddingModel buildZhipuaiEmbeddingModel(EmbeddingSettingsEntity settings) {
        ZhiPuAiApi api = new ZhiPuAiApi(resolveBaseUrl(settings, "https://open.bigmodel.cn/api/paas"), resolveApiKey(settings));
        ZhiPuAiEmbeddingOptions options = ZhiPuAiEmbeddingOptions.builder()
                .model(resolveModel(settings, "embedding-2"))
                .build();
        return new ZhiPuAiEmbeddingModel(api, MetadataMode.EMBED, options);
    }

    private EmbeddingModel buildOllamaEmbeddingModel(EmbeddingSettingsEntity settings) {
        OllamaApi api = OllamaApi.builder()
                .baseUrl(resolveBaseUrl(settings, "http://host.docker.internal:11434"))
                .build();
        OllamaEmbeddingOptions options = OllamaEmbeddingOptions.builder()
                .model(resolveModel(settings, "bge-m3:latest"))
                .build();
        return OllamaEmbeddingModel.builder()
                .ollamaApi(api)
            .options(options)
                .build();
    }

    private EmbeddingModel buildOpenAiCompatibleEmbeddingModel(EmbeddingSettingsEntity settings, String provider) {
        OpenAiEmbeddingOptions.Builder optionsBuilder = OpenAiEmbeddingOptions.builder()
                .model(resolveModel(settings, "text-embedding-3-small"));
        Integer dimensions = settings.getDimensions();
        if (dimensions != null && dimensions > 0) {
            optionsBuilder.dimensions(dimensions);
        }
        OpenAiEmbeddingOptions options = OpenAiCompatibleModelFactory.withConnection(optionsBuilder.build(),
            resolveBaseUrl(settings, resolveProviderBaseUrl(provider)), resolveApiKey(settings));
        return OpenAiCompatibleModelFactory.embeddingModel(options, MetadataMode.EMBED);
    }

    private String resolveProvider(EmbeddingSettingsEntity settings) {
        String provider = settings.getProvider();
        if (!StringUtils.hasText(provider)) {
            provider = LlmDefaults.DEFAULT_EMBEDDING_PROVIDER;
        }
        return provider.toLowerCase(Locale.ROOT);
    }

    private String resolveModel(EmbeddingSettingsEntity settings, String defaultModel) {
        if (StringUtils.hasText(settings.getModel())) {
            return settings.getModel();
        }
        String provider = resolveProvider(settings);
        return environment.getProperty("spring.ai." + provider + ".embedding.options.model", defaultModel);
    }

    private String resolveApiKey(EmbeddingSettingsEntity settings) {
        if (StringUtils.hasText(settings.getApiKey())) {
            return settings.getApiKey();
        }
        String provider = resolveProvider(settings);
        String embeddingApiKey = environment.getProperty("spring.ai." + provider + ".embedding.api-key");
        if (StringUtils.hasText(embeddingApiKey)) {
            return embeddingApiKey;
        }
        return environment.getProperty("spring.ai." + provider + ".api-key", "");
    }

    private String resolveBaseUrl(EmbeddingSettingsEntity settings, String defaultBaseUrl) {
        if (StringUtils.hasText(settings.getBaseUrl())) {
            return settings.getBaseUrl();
        }
        String provider = resolveProvider(settings);
        return environment.getProperty("spring.ai." + provider + ".base-url", defaultBaseUrl);
    }

    private String resolveProviderBaseUrl(String provider) {
        String baseUrl = environment.getProperty("spring.ai." + provider + ".base-url");
        if (StringUtils.hasText(baseUrl)) {
            return baseUrl;
        }
        if (LlmProviderConstants.SILICONFLOW.equals(provider)) {
            return "https://api.siliconflow.cn";
        }
        return "https://api.openai.com";
    }

    @Override
    public EmbeddingSettingsResponse convertToResponse(EmbeddingSettingsEntity entity) {
        return modelMapper.map(entity, EmbeddingSettingsResponse.class);
    }

    @Override
    public EmbeddingSettingsExcel convertToExcel(EmbeddingSettingsEntity entity) {
        return modelMapper.map(entity, EmbeddingSettingsExcel.class);
    }

    @Override
    protected Specification<EmbeddingSettingsEntity> createSpecification(EmbeddingSettingsRequest request) {
        return EmbeddingSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<EmbeddingSettingsEntity> executePageQuery(Specification<EmbeddingSettingsEntity> spec, Pageable pageable) {
        return embedding_settingsRepository.findAll(spec, pageable);
    }

    private void applyDefaults(EmbeddingSettingsRequest request) {
        if (!StringUtils.hasText(request.getType())) {
            request.setType(EmbeddingSettingsTypeEnum.KBASE.name());
        }
        request.setLevel(LevelEnum.PLATFORM.name());
        request.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        if (!StringUtils.hasText(request.getProvider())) {
            request.setProvider(resolveEmbeddingProvider());
        }
        if (!StringUtils.hasText(request.getModel())) {
            request.setModel(resolveEmbeddingModel(request.getProvider()));
        }
        if (!StringUtils.hasText(request.getBaseUrl())) {
            request.setBaseUrl(resolveEmbeddingBaseUrl(request.getProvider()));
        }
        if (!StringUtils.hasText(request.getApiKey())) {
            request.setApiKey(resolveEmbeddingApiKey(request.getProvider()));
        }
        if (request.getDimensions() == null || request.getDimensions() <= 0) {
            request.setDimensions(resolveEmbeddingDimensions(request.getProvider()));
        }
        if (request.getEnabled() == null) {
            request.setEnabled(true);
        }
        if (request.getDefaultSettings() == null) {
            request.setDefaultSettings(false);
        }
        if (!StringUtils.hasText(request.getVectorStoreType())) {
            request.setVectorStoreType(environment.getProperty("spring.ai.vectorstore.type", "elasticsearch"));
        }
        if (!StringUtils.hasText(request.getVectorStoreIndexName())) {
            request.setVectorStoreIndexName(environment.getProperty("spring.ai.vectorstore.elasticsearch.index-name", "bytedesk_vs_index"));
        }
        if (request.getVectorStoreDimensions() == null || request.getVectorStoreDimensions() <= 0) {
            request.setVectorStoreDimensions(request.getDimensions());
        }
    }

    private void applyDefaultsIfMissing(EmbeddingSettingsEntity entity) {
        if (!StringUtils.hasText(entity.getType())) {
            entity.setType(EmbeddingSettingsTypeEnum.KBASE.name());
        }
        entity.setLevel(LevelEnum.PLATFORM.name());
        entity.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        if (!StringUtils.hasText(entity.getProvider())) {
            entity.setProvider(resolveEmbeddingProvider());
        }
        if (!StringUtils.hasText(entity.getModel())) {
            entity.setModel(resolveEmbeddingModel(entity.getProvider()));
        }
        if (!StringUtils.hasText(entity.getBaseUrl())) {
            entity.setBaseUrl(resolveEmbeddingBaseUrl(entity.getProvider()));
        }
        if (!StringUtils.hasText(entity.getApiKey())) {
            entity.setApiKey(resolveEmbeddingApiKey(entity.getProvider()));
        }
        if (entity.getDimensions() == null || entity.getDimensions() <= 0) {
            entity.setDimensions(resolveEmbeddingDimensions(entity.getProvider()));
        }
        if (!StringUtils.hasText(entity.getVectorStoreType())) {
            entity.setVectorStoreType(environment.getProperty("spring.ai.vectorstore.type", "elasticsearch"));
        }
        if (!StringUtils.hasText(entity.getVectorStoreIndexName())) {
            entity.setVectorStoreIndexName(environment.getProperty("spring.ai.vectorstore.elasticsearch.index-name", "bytedesk_vs_index"));
        }
        if (entity.getVectorStoreDimensions() == null || entity.getVectorStoreDimensions() <= 0) {
            entity.setVectorStoreDimensions(entity.getDimensions());
        }
        if (entity.getDefaultSettings() == null) {
            entity.setDefaultSettings(false);
        }
        if (entity.getEnabled() == null) {
            entity.setEnabled(true);
        }
        if (!StringUtils.hasText(entity.getDescription())) {
            entity.setDescription(I18Consts.I18N_DESCRIPTION);
        }
    }

    private void clearOtherDefaultsIfNeeded(EmbeddingSettingsEntity entity) {
        if (!Boolean.TRUE.equals(entity.getDefaultSettings()) || !StringUtils.hasText(entity.getLevel())) {
            return;
        }
        embedding_settingsRepository.findFirstByLevelAndTypeAndDefaultSettingsTrueAndEnabledTrueAndDeletedFalse(entity.getLevel(), entity.getType())
            .filter(existing -> !existing.getUid().equals(entity.getUid()))
            .ifPresent(existing -> {
                existing.setDefaultSettings(false);
                embedding_settingsRepository.save(existing);
            });
    }

    private void requireSuperUser(boolean skipPermissionCheck) {
        if (skipPermissionCheck) {
            return;
        }
        UserEntity user = authService.getUser();
        if (user == null || !user.isSuperUser()) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
        }
    }
    
    public void initEmbeddingSettingss(String orgUid) {
        String level = LevelEnum.PLATFORM.name();
        String type = EmbeddingSettingsTypeEnum.KBASE.name();

        // 1. 创建默认 embedding 配置
        initDefaultEmbeddingSettings(level, type);

        // 2. 将配置文件中所有已启用的 embedding provider 也同步到 DB
        initProviderEmbeddingSettings(level, type);
    }

    private void initDefaultEmbeddingSettings(String level, String type) {
        if (findDefaultByLevelAndType(level, type).isPresent()) {
            return;
        }

        Optional<EmbeddingSettingsEntity> existing = findByNameAndLevelAndType(DEFAULT_KBASE_EMBEDDING_SETTINGS_NAME, level, type);
        if (existing.isPresent()) {
            EmbeddingSettingsEntity entity = existing.get();
            applyDefaultsIfMissing(entity);
            entity.setDefaultSettings(true);
            entity.setEnabled(true);
            clearOtherDefaultsIfNeeded(entity);
            save(entity);
            return;
        }

        EmbeddingSettingsRequest request = EmbeddingSettingsRequest.builder()
                .uid(DEFAULT_KBASE_EMBEDDING_SETTINGS_UID)
                .name(DEFAULT_KBASE_EMBEDDING_SETTINGS_NAME)
                .description(I18Consts.I18N_DESCRIPTION)
                .type(type)
                .defaultSettings(true)
                .enabled(true)
                .build();
        createSystemEmbeddingSettings(request);
    }

    /**
     * 扫描配置文件中所有已启用 embedding 的 provider，为每个 provider 在 DB 中创建对应记录。
     * 支持的 provider: dashscope, zhipuai, ollama, siliconflow, openai, volcengine
     */
    private void initProviderEmbeddingSettings(String level, String type) {
        String[] providers = {
            LlmProviderConstants.DASHSCOPE,
            LlmProviderConstants.ZHIPUAI,
            LlmProviderConstants.OLLAMA,
            LlmProviderConstants.SILICONFLOW,
            "openai",
            "volcengine"
        };

        for (String provider : providers) {
            try {
                initSingleProviderEmbeddingSettings(provider, level, type);
            } catch (Exception e) {
                log.warn("Failed to init embedding settings for provider={}: {}", provider, e.getMessage());
            }
        }
    }

    private void initSingleProviderEmbeddingSettings(String provider, String level, String type) {
        // 检查该 provider 的 embedding 是否启用
        Boolean enabled = environment.getProperty("spring.ai." + provider + ".embedding.enabled", Boolean.class, false);
        if (!Boolean.TRUE.equals(enabled)) {
            // 如果 embedding.enabled 未配置，检查是否有 api-key 或 base-url 作为备选判断
            String apiKey = environment.getProperty("spring.ai." + provider + ".api-key");
            String embeddingApiKey = environment.getProperty("spring.ai." + provider + ".embedding.api-key");
            if (!StringUtils.hasText(apiKey) && !StringUtils.hasText(embeddingApiKey)) {
                return; // 没有配置 key，跳过
            }
        }

        String name = provider + "-kbase-embedding-settings";
        // 检查是否已存在
        if (findByNameAndLevelAndType(name, level, type).isPresent()) {
            return;
        }

        EmbeddingSettingsRequest request = EmbeddingSettingsRequest.builder()
                .name(name)
                .description("Auto-created from config: " + provider + " embedding settings")
                .type(type)
                .provider(provider)
                .defaultSettings(false)
                .enabled(Boolean.TRUE.equals(enabled))
                .build();
        createSystemEmbeddingSettings(request);
        log.info("Initialized embedding settings for provider={}", provider);
    }

    private void triggerReindexIfNeeded(EmbeddingSettingsSnapshot previousEffectiveSnapshot, EmbeddingSettingsEntity savedEntity) {
        EmbeddingSettingsSnapshot currentEffectiveSnapshot = getEffectiveDefaultSnapshot();
        if (currentEffectiveSnapshot == null) {
            return;
        }
        if (!Objects.equals(savedEntity.getUid(), currentEffectiveSnapshot.uid())) {
            return;
        }
        Runnable reindexTask = () -> embeddingSettingsReindexService.rebuildForEffectiveSettingsChange(previousEffectiveSnapshot, currentEffectiveSnapshot);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    reindexTask.run();
                }
            });
            return;
        }
        reindexTask.run();
    }

    private EmbeddingSettingsSnapshot getEffectiveDefaultSnapshot() {
        return findDefaultByLevelAndType(LevelEnum.PLATFORM.name(), EmbeddingSettingsTypeEnum.KBASE.name())
                .map(this::toSnapshot)
                .orElse(null);
    }

    private EmbeddingSettingsSnapshot toSnapshot(EmbeddingSettingsEntity entity) {
        if (entity == null) {
            return null;
        }
        return new EmbeddingSettingsSnapshot(
                entity.getUid(),
                entity.getProvider(),
                entity.getModel(),
                entity.getDimensions(),
                entity.getVectorStoreType(),
                entity.getVectorStoreIndexName(),
                entity.getVectorStoreDimensions());
    }

    private String resolveEmbeddingProvider() {
        LlmProviderConfigDefault defaults = LlmConfigUtils.getLlmProviderConfigDefault(environment);
        String provider = defaults.getDefaultEmbeddingProvider();
        if (!StringUtils.hasText(provider)) {
            provider = LlmDefaults.DEFAULT_EMBEDDING_PROVIDER;
        }
        return provider;
    }

    private String resolveEmbeddingModel(String provider) {
        String normalizedProvider = normalizeProvider(provider);
        if (!StringUtils.hasText(normalizedProvider)) {
            return LlmDefaults.DEFAULT_EMBEDDING_MODEL;
        }
        return environment.getProperty(
                "spring.ai." + normalizedProvider + ".embedding.options.model",
                LlmConfigUtils.getLlmProviderConfigDefault(environment).getDefaultEmbeddingModel());
    }

    private String resolveEmbeddingBaseUrl(String provider) {
        String normalizedProvider = normalizeProvider(provider);
        if (!StringUtils.hasText(normalizedProvider)) {
            return null;
        }
        return environment.getProperty("spring.ai." + normalizedProvider + ".base-url", getDefaultProviderBaseUrl(normalizedProvider));
    }

    private String resolveEmbeddingApiKey(String provider) {
        String normalizedProvider = normalizeProvider(provider);
        if (!StringUtils.hasText(normalizedProvider)) {
            return BytedeskConsts.EMPTY_STRING;
        }
        String embeddingApiKey = environment.getProperty("spring.ai." + normalizedProvider + ".embedding.api-key");
        if (StringUtils.hasText(embeddingApiKey)) {
            return embeddingApiKey;
        }
        return environment.getProperty("spring.ai." + normalizedProvider + ".api-key", BytedeskConsts.EMPTY_STRING);
    }

    private Integer resolveEmbeddingDimensions(String provider) {
        String normalizedProvider = normalizeProvider(provider);
        if (StringUtils.hasText(normalizedProvider)) {
            Integer dimensions = environment.getProperty("spring.ai." + normalizedProvider + ".embedding.options.dimensions", Integer.class);
            if (dimensions != null && dimensions > 0) {
                return dimensions;
            }
        }
        return environment.getProperty("spring.ai.vectorstore.elasticsearch.dimensions", Integer.class, 1024);
    }

    private String normalizeProvider(String provider) {
        if (!StringUtils.hasText(provider)) {
            return LlmDefaults.DEFAULT_EMBEDDING_PROVIDER;
        }
        return provider.trim().toLowerCase();
    }

    private String getDefaultProviderBaseUrl(String provider) {
        if (LlmProviderConstants.DASHSCOPE.equals(provider)) {
            return "https://dashscope.aliyuncs.com";
        }
        if (LlmProviderConstants.ZHIPUAI.equals(provider)) {
            return "https://open.bigmodel.cn/api/paas";
        }
        if (LlmProviderConstants.OLLAMA.equals(provider)) {
            return "http://127.0.0.1:11434";
        }
        if (LlmProviderConstants.SILICONFLOW.equals(provider)) {
            return "https://api.siliconflow.cn";
        }
        if (LlmProviderConstants.OPENAI.equals(provider)) {
            return "https://api.openai.com";
        }
        return null;
    }

    
    
}
