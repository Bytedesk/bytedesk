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
import com.bytedesk.core.system_config.utils.PlatformSecretUtils;
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
            boolean secretKey = isSecretKey(keyEnum);
            boolean secretConfigured = secretKey && StringUtils.hasText(overrideValue);
            String effectiveValue = StringUtils.hasText(overrideValue) ? overrideValue : defaultValue;
            String source = StringUtils.hasText(overrideValue)
                    ? SystemConfigResponse.SOURCE_DB
                    : SystemConfigResponse.SOURCE_DEFAULT;

            // 敏感 key（appSecret 类）：永不回显明文/密文，已配置时统一掩码，
            // 前端凭 secretConfigured 区分「已配置但掩码」与「未配置」；
            // defaultValue 静态来源不涉及密钥，敏感 key 一律无静态默认值。
            String displayEffective = secretKey
                    ? (secretConfigured ? PlatformSecretUtils.SECRET_MASK : null)
                    : effectiveValue;
            String displayOverride = secretKey
                    ? (secretConfigured ? PlatformSecretUtils.SECRET_MASK : null)
                    : (StringUtils.hasText(overrideValue) ? overrideValue : null);

            responses.add(SystemConfigResponse.builder()
                    .key(keyEnum.getKey())
                    .group(keyEnum.getGroup().name())
                    .valueType(keyEnum.getValueType().name())
                    .displayName(keyEnum.getDisplayName())
                    .description(keyEnum.getDescription())
                    .defaultValue(secretKey ? null : defaultValue)
                    .overrideValue(displayOverride)
                    .effectiveValue(displayEffective)
                    .secretConfigured(secretKey ? secretConfigured : null)
                    .source(source)
                    .sortOrder(keyEnum.getSortOrder())
                    .orgUid(orgUid)
                    .level(LevelEnum.PLATFORM.name())
                    .build());
        }
        return responses;
    }

    /**
     * 查询敏感 key 的明文值（供前端「查看明文/复制」场景按需解密回显）。
     *
     * <p>仅超级管理员可调（requireSuperUser 兕底，Controller 层 @PreAuthorize 双保险）；
     * key 必须为已注册的敏感 key（appSecret 类），普通 key 直接拒绝；
     * 未配置返回 null；已配置返回解密后明文（兼容历史明文存储，decrypt 对非 ENC 值原样返回）。</p>
     */
    public String querySecretValue(String key) {
        requireSuperUser();

        SystemConfigKeyEnum keyEnum = SystemConfigKeyEnum.fromKey(key);
        if (keyEnum == null) {
            throw new IllegalArgumentException("unregistered system config key: " + key);
        }
        if (!isSecretKey(keyEnum)) {
            throw new IllegalArgumentException("not a secret key: " + key);
        }

        String orgUid = SystemConfigConsts.PLATFORM_CONFIG_ORG_UID;
        String stored = getOverrideValues(orgUid).get(key);
        if (!StringUtils.hasText(stored)) {
            return null;
        }
        return PlatformSecretUtils.decrypt(stored);
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
            boolean secretKey = isSecretKey(keyEnum);
            // 注意：这里查询必须包含软删除记录——软删除记录仍占用 (config_key, org_uid)
            // 唯一约束。若此前该 key 被「清空恢复默认」软删除（is_deleted=true），
            // 再用 DeletedFalse 查询会查不到而走 insert，触发 Duplicate entry 冲突。
            Optional<SystemConfigEntity> existing = systemConfigRepository
                    .findByConfigKeyAndOrgUid(key, orgUid);

            if (secretKey) {
                // 敏感 key（appSecret 类）语义与普通 key 不同：
                // - 非空且非掩码 → 新明文，encrypt() 后 upsert；
                // - 掩码 *** 或空值 → 保留原值不修改（不删除、不覆盖）；
                // 避免「空值=恢复默认」误删已配置的生产凭据。
                if (StringUtils.hasText(value) && !PlatformSecretUtils.SECRET_MASK.equals(value)) {
                    upsertOverride(keyEnum, existing, PlatformSecretUtils.encrypt(value), orgUid, user);
                } else {
                    log.debug("Skip secret key without new value, keep existing: {}", key);
                }
            } else if (StringUtils.hasText(value)) {
                // 校验值类型合法性（BOOLEAN/INTEGER 非法值直接拒绝，避免脏数据进入下发链路）
                validateValueType(keyEnum, value);
                // 平台客服 org/workgroup uid：基础格式校验（长度<=64，字符集 [a-zA-Z0-9_-]），防注入
                validatePlatformServiceUid(keyEnum, value);

                upsertOverride(keyEnum, existing, value, orgUid, user);
            } else {
                // 空值 = 删除覆盖，恢复默认。
                // 物理删除：软删除会让记录继续占用 (config_key, org_uid) 唯一约束，
                // 导致下次保存同 key 时 insert 冲突。
                existing.ifPresent(systemConfigRepository::delete);
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
     * 是否禁用全平台大模型问答（平台级开关 ai.disableQa）。
     *
     * <p>读取平台级覆盖值（走 Redis 缓存），值为 "true"（忽略大小写）时视为禁用；
     * 未配置或非 true 一律返回 false（维持现状）。</p>
     */
    public boolean isAiQaDisabled() {
        String value = getOverrideValues(SystemConfigConsts.PLATFORM_CONFIG_ORG_UID)
                .get(SystemConfigConsts.KEY_AI_DISABLE_QA);
        return "true".equalsIgnoreCase(value);
    }

    /**
     * 禁用大模型问答时的固定回复文案（平台级配置 ai.disableQaReply）。
     *
     * <p>DB 有覆盖值时返回自定义文案；否则返回内置 i18n key（前端翻译为对应语言）。</p>
     */
    public String getAiDisableQaReplyOrDefault() {
        String value = getOverrideValues(SystemConfigConsts.PLATFORM_CONFIG_ORG_UID)
                .get(SystemConfigConsts.KEY_AI_DISABLE_QA_REPLY);
        return StringUtils.hasText(value) ? value : I18Consts.I18N_AI_QA_DISABLED_REPLY;
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
            case AI_DISABLE_QA:
                return "false";
            case PUSH_ALIYUN_ENABLED:
                // 推送默认关闭：需超管显式开启后才向移动端下发凭据
                return "false";
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
            // 微信公众号模板消息：properties 兜底（bytedesk.custom.wechat-mp-*）
            case WECHAT_MP_APP_ID:
                return custom.getWechatMpAppId();
            case WECHAT_MP_LOGIN_NOTICE_TEMPLATE_ID:
                return custom.getWechatMpLoginNoticeTemplateId();
            case WECHAT_MP_MINI_PROGRAM_APP_ID:
                return custom.getWechatMpMiniProgramAppId();
            case WECHAT_MP_MINI_PROGRAM_PAGE_PATH:
                return custom.getWechatMpMiniProgramPagePath();
            case WECHAT_MP_BIND_TEMPLATE_ID:
                return custom.getWechatMpBindTemplateId();
            case WECHAT_MP_UNBIND_TEMPLATE_ID:
                return custom.getWechatMpUnbindTemplateId();
            case WECHAT_MP_RECHARGE_TEMPLATE_ID:
                return custom.getWechatMpRechargeTemplateId();
            case WECHAT_MP_RECHARGE_REMIND_TEMPLATE_ID:
                return custom.getWechatMpRechargeRemindTemplateId();
            case WECHAT_MP_PAYMENT_TEMPLATE_ID:
                return custom.getWechatMpPaymentTemplateId();
            case WECHAT_MP_STATISTIC_TEMPLATE_ID:
                return custom.getWechatMpStatisticTemplateId();
            case WECHAT_MP_RATE_TEMPLATE_ID:
                return custom.getWechatMpRateTemplateId();
            case WECHAT_MP_LEAVE_MSG_TEMPLATE_ID:
                return custom.getWechatMpLeaveMsgTemplateId();
            case WECHAT_MP_TICKET_TEMPLATE_ID:
                return custom.getWechatMpTicketTemplateId();
            case WECHAT_MP_FEEDBACK_TEMPLATE_ID:
                return custom.getWechatMpFeedbackTemplateId();
            case WECHAT_MP_LOGIN_TEMPLATE_ID:
                return custom.getWechatMpLoginTemplateId();
            case WECHAT_MP_NEW_VISITOR_TEMPLATE_ID:
                return custom.getWechatMpNewVisitorTemplateId();
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
    /**
     * 是否为敏感（加密存储/掩码回显）key：目前为推送 appSecret 类。
     * 敏感 key 无静态默认值，空值/掩码提交均表示「保留原值不修改」。
     */
    private boolean isSecretKey(SystemConfigKeyEnum keyEnum) {
        return SystemConfigKeyEnum.PUSH_ALIYUN_ANDROID_APP_SECRET.equals(keyEnum)
                || SystemConfigKeyEnum.PUSH_ALIYUN_IOS_APP_SECRET.equals(keyEnum);
    }

    /**
     * upsert 一条覆盖记录（复用/恢复软删除记录或新建）
     */
    private void upsertOverride(SystemConfigKeyEnum keyEnum, Optional<SystemConfigEntity> existing,
            String value, String orgUid, UserEntity user) {
        if (existing.isPresent()) {
            SystemConfigEntity entity = existing.get();
            entity.setConfigValue(value);
            entity.setDeleted(false); // 复用（可能曾软删除）的记录并恢复
            systemConfigRepository.save(entity);
        } else {
            SystemConfigEntity entity = SystemConfigEntity.builder()
                    .uid(uidUtils.getUid())
                    .configKey(keyEnum.getKey())
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
    }}
