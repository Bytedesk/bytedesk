/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-04 09:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-04 09:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *  Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.system_config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统全局配置服务（KV 专用，裁剪自 settings 样板）
 * 
 * 语义：
 * - DB 无记录 = 使用静态默认值（懒加载，无需种子数据）；
 * - 保存时 value 为空 = 删除覆盖记录（恢复默认）；
 * - 读取走 Redis 缓存（system_config），写后逐 key 失效；
 * - 仅超级管理员可写（requireSuperUser 兜底，Controller 层再加 @PreAuthorize 双保险）。
 */
@Slf4j
@Service
@AllArgsConstructor
public class SystemConfigRestService {

    private final SystemConfigRepository systemConfigRepository;

    private final BytedeskProperties bytedeskProperties;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final CacheManager cacheManager;

    /**
     * 查询全量配置清单：由 SystemConfigKeyEnum 合成 + DB 覆盖值叠加（含默认值与来源）
     */
    public List<SystemConfigResponse> queryAll() {
        String orgUid = SystemConfigConsts.PLATFORM_CONFIG_ORG_UID;
        Map<String, String> overrides = getOverrideValues(orgUid);

        List<SystemConfigResponse> responses = new ArrayList<>();
        for (SystemConfigKeyEnum keyEnum : SystemConfigKeyEnum.values()) {
            String defaultValue = getDefaultValue(keyEnum);
            String overrideValue = overrides.get(keyEnum.getKey());
            String effectiveValue = StringUtils.hasText(overrideValue) ? overrideValue : defaultValue;
            String source = StringUtils.hasText(overrideValue)
                    ? SystemConfigResponse.SOURCE_DB
                    : SystemConfigResponse.SOURCE_DEFAULT;

            responses.add(SystemConfigResponse.builder()
                    .key(keyEnum.getKey())
                    .group(keyEnum.getGroup().name())
                    .valueType(keyEnum.getValueType().name())
                    .displayName(keyEnum.getDisplayName())
                    .description(keyEnum.getDescription())
                    .defaultValue(defaultValue)
                    .overrideValue(StringUtils.hasText(overrideValue) ? overrideValue : null)
                    .effectiveValue(effectiveValue)
                    .source(source)
                    .sortOrder(keyEnum.getSortOrder())
                    .orgUid(orgUid)
                    .level(LevelEnum.PLATFORM.name())
                    .build());
        }
        return responses;
    }

    /**
     * 批量保存覆盖值。
     * 语义：value 非空 = upsert 覆盖记录；value 为空 = 删除覆盖记录（恢复默认）。
     * key 必须为 SystemConfigKeyEnum 注册过的受控 key，未注册 key 直接拒绝。
     */
    @Transactional
    public List<SystemConfigResponse> save(SystemConfigRequest request) {
        requireSuperUser();

        List<SystemConfigRequest.SystemConfigItem> items = request.getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items cannot be empty");
        }

        String orgUid = SystemConfigConsts.PLATFORM_CONFIG_ORG_UID;
        UserEntity user = authService.getUser();

        for (SystemConfigRequest.SystemConfigItem item : items) {
            String key = item.getKey();
            SystemConfigKeyEnum keyEnum = SystemConfigKeyEnum.fromKey(key);
            if (keyEnum == null) {
                throw new IllegalArgumentException("unregistered system config key: " + key);
            }
            String value = item.getValue();
            Optional<SystemConfigEntity> existing = systemConfigRepository
                    .findByConfigKeyAndOrgUidAndDeletedFalse(key, orgUid);

            if (StringUtils.hasText(value)) {
                // 校验值类型合法性（BOOLEAN/INTEGER 非法值直接拒绝，避免脏数据进入下发链路）
                validateValueType(keyEnum, value);
                // 平台客服 org/workgroup uid：基础格式校验（长度<=64，字符集 [a-zA-Z0-9_-]），防注入
                validatePlatformServiceUid(keyEnum, value);

                if (existing.isPresent()) {
                    SystemConfigEntity entity = existing.get();
                    entity.setConfigValue(value);
                    systemConfigRepository.save(entity);
                } else {
                    SystemConfigEntity entity = SystemConfigEntity.builder()
                            .uid(uidUtils.getUid())
                            .configKey(key)
                            .configValue(value)
                            .valueType(keyEnum.getValueType().name())
                            .configGroup(keyEnum.getGroup().name())
                            .displayName(keyEnum.getDisplayName())
                            .description(keyEnum.getDescription())
                            .visible(true)
                            .sortOrder(keyEnum.getSortOrder())
                            .orgUid(orgUid)
                            .userUid(user != null ? user.getUid() : null)
                            .level(LevelEnum.PLATFORM.name())
                            .build();
                    systemConfigRepository.save(entity);
                }
            } else {
                // 空值 = 删除覆盖，恢复默认
                existing.ifPresent(entity -> {
                    entity.setDeleted(true);
                    systemConfigRepository.save(entity);
                });
            }
            // 逐 key 保存后失效缓存（缓存粒度为整组 Map，任一 key 变更即整组失效）
            // 注意：不走 @CacheEvict 注解——save 内部自调用会绕过 Spring 代理导致注解失效，
            // 此处通过 CacheManager 手动失效
            evictOverrideCache(orgUid);
        }

        log.info("System config saved, keys: {}",
                items.stream().map(item -> item.getKey())
                        .collect(Collectors.joining(",")));

        return queryAll();
    }

    /**
     * 读取平台级全部覆盖值（key -> value，仅包含非空 value）。
     * 供 /config/bytedesk/properties 下发链路合并使用，走 Redis 缓存。
     * 注意：缓存粒度为「整组覆盖值 Map」，任一 key 保存后都会重建。
     */
    @Cacheable(value = SystemConfigConsts.CACHE_NAME_SYSTEM_CONFIG, key = "#orgUid", unless = "#result == null")
    public Map<String, String> getOverrideValues(String orgUid) {
        Map<String, String> overrides = new HashMap<>();
        List<SystemConfigEntity> entities = systemConfigRepository.findByOrgUidAndDeletedFalse(orgUid);
        for (SystemConfigEntity entity : entities) {
            if (StringUtils.hasText(entity.getConfigValue())) {
                overrides.put(entity.getConfigKey(), entity.getConfigValue());
            }
        }
        return overrides;
    }

    /**
     * 失效指定 orgUid 的整组覆盖值缓存（save 后调用；因缓存粒度是整组 Map）。
     * 通过 CacheManager 手动失效，规避同类内部调用绕过 AOP 代理的问题。
     */
    private void evictOverrideCache(String orgUid) {
        try {
            Cache cache = cacheManager.getCache(SystemConfigConsts.CACHE_NAME_SYSTEM_CONFIG);
            if (cache != null) {
                cache.evict(orgUid);
                log.debug("Evicted system_config cache for orgUid: {}", orgUid);
            }
        } catch (Exception e) {
            // 缓存失效失败仅记录日志，不阻断保存流程（缓存有 TTL 兜底）
            log.warn("Evict system_config cache failed: {}", e.getMessage());
        }
    }

    /**
     * 读取单个 key 的静态默认值（来自 BytedeskProperties.Custom 或代码常量）
     */
    private String getDefaultValue(SystemConfigKeyEnum keyEnum) {
        // 平台客服：静态默认值为代码常量，不新增 properties 键
        switch (keyEnum) {
            case PLATFORM_SERVICE_ENABLED:
                return "false";
            case PLATFORM_SERVICE_ORG_UID:
                return com.bytedesk.core.constant.BytedeskConsts.DEFAULT_ORGANIZATION_UID;
            case PLATFORM_SERVICE_WORKGROUP_UID:
                return com.bytedesk.core.constant.BytedeskConsts.DEFAULT_WORKGROUP_UID;
            default:
                break;
        }
        if (bytedeskProperties == null || bytedeskProperties.getCustom() == null) {
            return null;
        }
        BytedeskProperties.Custom custom = bytedeskProperties.getCustom();
        switch (keyEnum) {
            case CUSTOM_ENABLED:
                return custom.getEnabled() == null ? null : custom.getEnabled().toString();
            case CUSTOM_NAME:
                return custom.getName();
            case CUSTOM_LOGO:
                return custom.getLogo();
            case CUSTOM_FAVICON:
                return custom.getFavicon();
            case CUSTOM_DESCRIPTION:
                return custom.getDescription();
            case CUSTOM_PRIVACY_POLICY_URL:
                return custom.getPrivacyPolicyUrl();
            case CUSTOM_TERMS_OF_SERVICE_URL:
                return custom.getTermsOfServiceUrl();
            default:
                return null;
        }
    }

    /**
     * 值类型校验：BOOLEAN/INTEGER 非法值拒绝
     */
    private void validateValueType(SystemConfigKeyEnum keyEnum, String value) {
        switch (keyEnum.getValueType()) {
            case BOOLEAN:
                if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                    throw new IllegalArgumentException(
                            "invalid boolean value for key " + keyEnum.getKey() + ": " + value);
                }
                break;
            case INTEGER:
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
     * 平台客服 org/workgroup uid 格式校验：长度<=64，仅允许 [a-zA-Z0-9_-]，防止注入
     */
    private void validatePlatformServiceUid(SystemConfigKeyEnum keyEnum, String value) {
        if (!SystemConfigKeyEnum.PLATFORM_SERVICE_ORG_UID.equals(keyEnum)
                && !SystemConfigKeyEnum.PLATFORM_SERVICE_WORKGROUP_UID.equals(keyEnum)) {
            return;
        }
        if (value == null || value.length() > 64 || !value.matches("[a-zA-Z0-9_-]+")) {
            throw new IllegalArgumentException(
                    "invalid uid value for key " + keyEnum.getKey() + ": only [a-zA-Z0-9_-] up to 64 chars allowed");
        }
    }

    /**
     * 超级管理员校验（兜底，Controller 层 @PreAuthorize 为主）
     */
    private void requireSuperUser() {
        UserEntity user = authService.getUser();
        if (user == null || !user.isSuperUser()) {
            throw new RuntimeException(I18Consts.I18N_SUPER_ADMIN_REQUIRED);
        }
    }
}
