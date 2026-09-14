/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-14 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *  Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.organization_config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 组织级配置服务（KV）
 *
 * 语义（参照 SystemConfigRestService 裁剪）：
 * - DB 无记录 = 使用系统级静态默认值（懒加载，无需种子数据）；
 * - 保存时 value 为空 = 删除覆盖记录（恢复系统级默认）；
 * - 读取走 Redis 缓存（organization_config:{orgUid}），写后整组失效；
 * - 仅组织管理员（ADMIN/SUPER）可写，读写按当前登录用户所属组织隔离。
 *
 * apiKey 治理：apiKey 类 key 明文落库（与 LlmProviderEntity.apiKey 现状一致），
 * 但 /query 响应一律脱敏（只报是否已配置 + 掩码），真实值仅服务端内部解析使用。
 */
@Slf4j
@Service
@AllArgsConstructor
public class OrganizationConfigRestService {

    private final OrganizationConfigRepository organizationConfigRepository;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final CacheManager cacheManager;

    private final Environment environment;

    /**
     * 查询指定组织的全量配置清单：由 OrganizationConfigKeyEnum 合成 + DB 覆盖值叠加。
     * apiKey 类 key 脱敏返回。
     */
    public List<OrganizationConfigResponse> queryAll(String orgUid) {
        requireOrgAccess(orgUid);
        Map<String, String> overrides = getOverrideValues(orgUid);

        List<OrganizationConfigResponse> responses = new ArrayList<>();
        for (OrganizationConfigKeyEnum keyEnum : OrganizationConfigKeyEnum.values()) {
            String defaultValue = getSystemDefaultValue(keyEnum);
            String overrideValue = overrides.get(keyEnum.getKey());
            boolean overridden = StringUtils.hasText(overrideValue);
            String effectiveValue = overridden ? overrideValue : defaultValue;
            String source = overridden
                    ? OrganizationConfigResponse.SOURCE_DB
                    : OrganizationConfigResponse.SOURCE_SYSTEM;

            boolean sensitive = isSensitiveKey(keyEnum.getKey());
            if (sensitive) {
                defaultValue = maskApiKey(defaultValue);
                overrideValue = maskApiKey(overrideValue);
                effectiveValue = maskApiKey(effectiveValue);
            }

            responses.add(OrganizationConfigResponse.builder()
                    .key(keyEnum.getKey())
                    .group(keyEnum.getGroup())
                    .valueType(keyEnum.getValueType())
                    .displayName(keyEnum.getDisplayName())
                    .description(keyEnum.getDescription())
                    .defaultValue(defaultValue)
                    .overrideValue(overridden ? overrideValue : null)
                    .effectiveValue(effectiveValue)
                    .source(source)
                    .sortOrder(keyEnum.getSortOrder())
                    .orgUid(orgUid)
                    .level(LevelEnum.ORGANIZATION.name())
                    .build());
        }
        return responses;
    }

    /**
     * 批量保存组织覆盖值。
     * 语义：value 非空 = upsert 覆盖记录；value 为空 = 删除覆盖记录（恢复系统级默认）。
     * key 必须为 OrganizationConfigKeyEnum 注册过的受控 key，未注册 key 直接拒绝。
     */
    @Transactional
    public List<OrganizationConfigResponse> save(OrganizationConfigRequest request) {
        // 组织隔离：默认当前登录用户组织；仅 SUPER 可指定他人组织
        String orgUid = resolveWritableOrgUid(request.getOrgUid());

        List<OrganizationConfigRequest.OrganizationConfigItem> items = request.getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items cannot be empty");
        }

        UserEntity user = authService.getUser();

        for (OrganizationConfigRequest.OrganizationConfigItem item : items) {
            String key = item.getKey();
            OrganizationConfigKeyEnum keyEnum = OrganizationConfigKeyEnum.fromKey(key);
            if (keyEnum == null) {
                throw new IllegalArgumentException("unregistered organization config key: " + key);
            }
            String value = item.getValue();
            Optional<OrganizationConfigEntity> existing = organizationConfigRepository
                    .findByConfigKeyAndOrgUidAndDeletedFalse(key, orgUid);

            if (StringUtils.hasText(value)) {
                validateValueType(keyEnum, value);

                if (existing.isPresent()) {
                    OrganizationConfigEntity entity = existing.get();
                    entity.setConfigValue(value);
                    organizationConfigRepository.save(entity);
                } else {
                    OrganizationConfigEntity entity = OrganizationConfigEntity.builder()
                            .uid(uidUtils.getUid())
                            .configKey(key)
                            .configValue(value)
                            .valueType(keyEnum.getValueType())
                            .configGroup(keyEnum.getGroup())
                            .displayName(keyEnum.getDisplayName())
                            .description(keyEnum.getDescription())
                            .visible(true)
                            .sortOrder(keyEnum.getSortOrder())
                            .orgUid(orgUid)
                            .userUid(user != null ? user.getUid() : null)
                            .level(LevelEnum.ORGANIZATION.name())
                            .build();
                    organizationConfigRepository.save(entity);
                }
            } else {
                // 空值 = 删除覆盖，恢复系统级默认
                existing.ifPresent(entity -> {
                    entity.setDeleted(true);
                    organizationConfigRepository.save(entity);
                });
            }
            // 逐 key 保存后失效缓存（缓存粒度为整组 Map，任一 key 变更即整组失效）
            // 不走 @CacheEvict 注解——save 内部自调用会绕过 Spring 代理，用 CacheManager 手动失效
            evictOverrideCache(orgUid);
        }

        log.info("Organization config saved, orgUid={}, keys: {}", orgUid,
                items.stream().map(item -> item.getKey()).collect(Collectors.joining(",")));

        return queryAll(orgUid);
    }

    /**
     * 读取组织全部覆盖值（key -> value，仅包含非空 value，真实值）。
     * 供 OrgAiConfigResolver 等服务端内部解析使用，走 Redis 缓存。
     */
    @Cacheable(value = OrganizationConfigConsts.CACHE_NAME_ORGANIZATION_CONFIG, key = "#orgUid", unless = "#result == null")
    public Map<String, String> getOverrideValues(String orgUid) {
        Map<String, String> overrides = new HashMap<>();
        if (!StringUtils.hasText(orgUid)) {
            return overrides;
        }
        List<OrganizationConfigEntity> entities = organizationConfigRepository.findByOrgUidAndDeletedFalse(orgUid);
        for (OrganizationConfigEntity entity : entities) {
            if (StringUtils.hasText(entity.getConfigValue())) {
                overrides.put(entity.getConfigKey(), entity.getConfigValue());
            }
        }
        return overrides;
    }

    /**
     * 读取单个 key 的组织生效值（组织覆盖 > 系统静态默认），未配置返回 null。
     */
    public String getEffectiveValue(String orgUid, String key) {
        if (!StringUtils.hasText(orgUid) || !StringUtils.hasText(key)) {
            return null;
        }
        Map<String, String> overrides = getOverrideValues(orgUid);
        if (StringUtils.hasText(overrides.get(key))) {
            return overrides.get(key);
        }
        return getSystemDefaultValue(OrganizationConfigKeyEnum.fromKey(key));
    }

    /**
     * 失效指定 orgUid 的整组覆盖值缓存（save 后调用）。
     */
    private void evictOverrideCache(String orgUid) {
        try {
            Cache cache = cacheManager.getCache(OrganizationConfigConsts.CACHE_NAME_ORGANIZATION_CONFIG);
            if (cache != null) {
                cache.evict(orgUid);
                log.debug("Evicted organization_config cache for orgUid: {}", orgUid);
            }
        } catch (Exception e) {
            log.warn("Evict organization_config cache failed: {}", e.getMessage());
        }
    }

    /**
     * 读取单个 key 的系统级静态默认值（properties / 环境变量）。
     * 与执行服务 @Value 注入的键保持一致，保证「回退系统级」语义相同。
     */
    private String getSystemDefaultValue(OrganizationConfigKeyEnum keyEnum) {
        if (keyEnum == null) {
            return null;
        }
        switch (keyEnum) {
            case AI_ASR_PROVIDER:
                return "dashscope";
            case AI_ASR_MODEL:
                return environment.getProperty("spring.ai.dashscope.audio.transcription.options.model", "paraformer-v2");
            case AI_ASR_API_KEY:
                return firstNonBlank(
                        environment.getProperty("spring.ai.dashscope.audio.transcription.api-key"),
                        environment.getProperty("spring.ai.dashscope.api-key"),
                        environment.getProperty("DASHSCOPE_API_KEY"));
            case AI_ASR_LANGUAGE:
                return "zh";
            case AI_TTS_PROVIDER:
                return "dashscope";
            case AI_TTS_MODEL:
                return environment.getProperty("spring.ai.dashscope.audio.synthesis.options.model", "cosyvoice-v2");
            case AI_TTS_VOICE:
                return environment.getProperty("spring.ai.dashscope.audio.synthesis.options.voice", "longanhuan");
            case AI_TTS_API_KEY:
                return firstNonBlank(
                        environment.getProperty("spring.ai.dashscope.audio.synthesis.api-key"),
                        environment.getProperty("spring.ai.dashscope.api-key"),
                        environment.getProperty("DASHSCOPE_API_KEY"));
            case AI_TTS_LANGUAGE:
                return "zh-CN";
            case AI_OCR_PROVIDER:
                return "dashscope";
            case AI_OCR_MODEL:
                return "qwen-vl-ocr-latest";
            case AI_OCR_API_KEY:
                return firstNonBlank(
                        environment.getProperty("spring.ai.dashscope.api-key"),
                        environment.getProperty("DASHSCOPE_API_KEY"));
            default:
                return null;
        }
    }

    /**
     * 值类型校验：BOOLEAN/INTEGER 非法值拒绝
     */
    private void validateValueType(OrganizationConfigKeyEnum keyEnum, String value) {
        switch (keyEnum.getValueType()) {
            case "BOOLEAN":
                if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                    throw new IllegalArgumentException(
                            "invalid boolean value for key " + keyEnum.getKey() + ": " + value);
                }
                break;
            case "INTEGER":
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "invalid integer value for key " + keyEnum.getKey() + ": " + value);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 组织访问校验（查询）：组织管理员仅可读自己组织；SUPER 可读任意组织。
     */
    private void requireOrgAccess(String orgUid) {
        if (!StringUtils.hasText(orgUid)) {
            throw new IllegalArgumentException("orgUid is required");
        }
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException(I18Consts.I18N_SUPER_ADMIN_REQUIRED);
        }
        if (user.isSuperUser()) {
            return;
        }
        if (!orgUid.equals(user.getOrgUid())) {
            throw new RuntimeException("no permission to access organization config of " + orgUid);
        }
    }

    /**
     * 写入组织解析：默认当前登录用户组织；仅 SUPER 可指定他人组织。
     */
    private String resolveWritableOrgUid(String requestedOrgUid) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException(I18Consts.I18N_SUPER_ADMIN_REQUIRED);
        }
        if (user.isSuperUser() && StringUtils.hasText(requestedOrgUid)) {
            return requestedOrgUid;
        }
        String currentOrgUid = user.getOrgUid();
        if (!StringUtils.hasText(currentOrgUid)) {
            throw new RuntimeException("current user has no organization");
        }
        if (StringUtils.hasText(requestedOrgUid) && !requestedOrgUid.equals(currentOrgUid)) {
            throw new RuntimeException("no permission to write organization config of " + requestedOrgUid);
        }
        return currentOrgUid;
    }

    /**
     * apiKey 类 key 判定（响应需脱敏）
     */
    static boolean isSensitiveKey(String key) {
        return key != null && key.endsWith(".apiKey");
    }

    /**
     * apiKey 掩码：非空返回 "••••" + 末 4 位（长度不足时全掩码），空返回 null
     */
    static String maskApiKey(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        if (value.length() <= 4) {
            return "••••";
        }
        return "••••" + value.substring(value.length() - 4);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }
}
