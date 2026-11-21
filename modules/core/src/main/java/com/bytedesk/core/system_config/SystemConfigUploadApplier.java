/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-19 09:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-11-19 09:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *  Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates the rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  联系：270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.system_config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.system_config.enums.SystemConfigKeyEnum;
import com.bytedesk.core.system_config.utils.PlatformSecretUtils;
import com.bytedesk.core.upload.UploadSecurityConfig;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件上传/对象存储配置覆盖应用器（SuperDisk 后台配置 → 运行时生效）
 *
 * 职责：
 * 1. 启动时（PostConstruct，早于 UploadConfig.uploadDir Bean 初始化）应用 DB 覆盖值到
 *    BytedeskProperties.upload / storage 与 UploadSecurityConfig；
 * 2. 快照静态默认值（应用覆盖前的 properties 值），供 /query 展示「默认值」列；
 * 3. 保存后台配置后（SystemConfigRestService.save）重新应用，实现免重启热更新。
 *
 * 语义与 SystemConfig 全局一致：DB 覆盖值（非空）> properties/compose 静态默认值。
 * 注意：uploadDir（Path Bean）在启动时确定后不再变更——运行时修改目录仅对
 * 「保存后新解析的 Path 使用方」生效（DiskTransferService 每次调用 uploadConfig.uploadDir()），
 * UploadRestService 等持有启动时 Path 的组件仍写入原目录；前端保存后有提示文案说明。
 * 对象存储（provider/endpoint/凭据等）由 enterprise UploadMinioService 感知
 * BytedeskProperties 变化后重建客户端，实现完整热更新（见 UploadMinioService）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemConfigUploadApplier {

    private final BytedeskProperties bytedeskProperties;

    private final UploadSecurityConfig uploadSecurityConfig;

    private final SystemConfigRepository systemConfigRepository;

    /** 静态默认值快照（应用覆盖前捕获），供查询接口回显 */
    private String defaultUploadDir;
    private String defaultUploadUrl;
    private String defaultMaxFileSize;
    private String defaultEnableImageValidation;
    private String defaultEnableFileNameFilter;
    private String defaultForceRename;
    private String defaultMaxFileNameLength;
    private String defaultEnableUploadLog;
    private String defaultEnableVirusScan;
    private String defaultStorageProvider;
    private String defaultStorageEndpoint;
    private String defaultStorageAccessKey;
    private String defaultStorageBucketName;
    private String defaultStorageRegion;
    private String defaultStorageSecure;
    private String defaultStoragePublicRead;
    private String defaultStoragePresignedExpiryDays;

    /**
     * 启动时应用覆盖：先快照静态默认值，再合并 DB 覆盖值。
     * 任何异常仅记录日志并回退静态配置，不阻断启动。
     */
    @PostConstruct
    public void init() {
        snapshotDefaults();
        try {
            applyOverrides();
            log.info("Upload/storage system config applied, provider: {}", bytedeskProperties.getStorageProviderName());
        } catch (Exception e) {
            log.warn("Apply upload/storage system config overrides failed, fallback to static properties: {}",
                    e.getMessage());
        }
    }

    /**
     * 合并 DB 覆盖值到运行时配置 Bean（保存后台配置后由 SystemConfigRestService 再次调用）。
     * 直接读 repository（不走 Redis 缓存）：applyOverrides 需要「保存后最新值」，
     * 且启动 PostConstruct 阶段缓存尚未就绪；绕开 SystemConfigRestService 也避免了与其形成循环依赖。
     */
    public void applyOverrides() {
        Map<String, String> overrides = loadOverridesFromRepository();
        if (overrides == null || overrides.isEmpty()) {
            return;
        }

        // ---- 上传（bytedesk.upload.*） ----
        String uploadDir = overrides.get(SystemConfigConsts.KEY_UPLOAD_DIR);
        if (StringUtils.hasText(uploadDir)) {
            bytedeskProperties.getUpload().setDir(uploadDir);
        }
        String uploadUrl = overrides.get(SystemConfigConsts.KEY_UPLOAD_URL);
        if (StringUtils.hasText(uploadUrl)) {
            bytedeskProperties.getUpload().setUrl(uploadUrl);
        }

        // ---- 上传安全（bytedesk.upload.security.*） ----
        String maxFileSize = overrides.get(SystemConfigConsts.KEY_UPLOAD_SECURITY_MAX_FILE_SIZE);
        if (StringUtils.hasText(maxFileSize)) {
            try {
                uploadSecurityConfig.setMaxFileSize(Long.parseLong(maxFileSize.trim()));
            } catch (NumberFormatException e) {
                log.warn("Invalid upload.security.maxFileSize override: {}", maxFileSize);
            }
        }
        applyBooleanOverride(overrides.get(SystemConfigConsts.KEY_UPLOAD_SECURITY_ENABLE_IMAGE_VALIDATION),
                value -> uploadSecurityConfig.setEnableImageValidation(value));
        applyBooleanOverride(overrides.get(SystemConfigConsts.KEY_UPLOAD_SECURITY_ENABLE_FILE_NAME_FILTER),
                value -> uploadSecurityConfig.setEnableFileNameFilter(value));
        applyBooleanOverride(overrides.get(SystemConfigConsts.KEY_UPLOAD_SECURITY_FORCE_RENAME),
                value -> uploadSecurityConfig.setForceRename(value));
        String maxFileNameLength = overrides.get(SystemConfigConsts.KEY_UPLOAD_SECURITY_MAX_FILE_NAME_LENGTH);
        if (StringUtils.hasText(maxFileNameLength)) {
            try {
                uploadSecurityConfig.setMaxFileNameLength(Integer.parseInt(maxFileNameLength.trim()));
            } catch (NumberFormatException e) {
                log.warn("Invalid upload.security.maxFileNameLength override: {}", maxFileNameLength);
            }
        }
        applyBooleanOverride(overrides.get(SystemConfigConsts.KEY_UPLOAD_SECURITY_ENABLE_UPLOAD_LOG),
                value -> uploadSecurityConfig.setEnableUploadLog(value));
        applyBooleanOverride(overrides.get(SystemConfigConsts.KEY_UPLOAD_SECURITY_ENABLE_VIRUS_SCAN),
                value -> uploadSecurityConfig.setEnableVirusScan(value));

        // ---- 对象存储（bytedesk.storage.*） ----
        BytedeskProperties.Storage storage = bytedeskProperties.getStorage();
        String provider = overrides.get(SystemConfigConsts.KEY_STORAGE_PROVIDER);
        if (StringUtils.hasText(provider)) {
            storage.setProvider(provider.trim());
        }
        String endpoint = overrides.get(SystemConfigConsts.KEY_STORAGE_ENDPOINT);
        if (StringUtils.hasText(endpoint)) {
            storage.setEndpoint(endpoint.trim());
        }
        String accessKey = overrides.get(SystemConfigConsts.KEY_STORAGE_ACCESS_KEY);
        if (StringUtils.hasText(accessKey)) {
            storage.setAccessKey(accessKey.trim());
        }
        // SecretKey 为 ENC(...) 密文，需解密后注入
        String secretKey = overrides.get(SystemConfigConsts.KEY_STORAGE_SECRET_KEY);
        if (StringUtils.hasText(secretKey)) {
            storage.setSecretKey(PlatformSecretUtils.decrypt(secretKey));
        }
        String bucketName = overrides.get(SystemConfigConsts.KEY_STORAGE_BUCKET_NAME);
        if (StringUtils.hasText(bucketName)) {
            storage.setBucketName(bucketName.trim());
        }
        String region = overrides.get(SystemConfigConsts.KEY_STORAGE_REGION);
        if (StringUtils.hasText(region)) {
            storage.setRegion(region.trim());
        }
        String secure = overrides.get(SystemConfigConsts.KEY_STORAGE_SECURE);
        if (StringUtils.hasText(secure)) {
            storage.setSecure(Boolean.parseBoolean(secure.trim()));
        }
        String publicRead = overrides.get(SystemConfigConsts.KEY_STORAGE_PUBLIC_READ);
        if (StringUtils.hasText(publicRead)) {
            storage.setPublicRead(Boolean.parseBoolean(publicRead.trim()));
        }
        String presignedExpiryDays = overrides.get(SystemConfigConsts.KEY_STORAGE_PRESIGNED_EXPIRY_DAYS);
        if (StringUtils.hasText(presignedExpiryDays)) {
            try {
                storage.setPresignedExpiryDays(Integer.parseInt(presignedExpiryDays.trim()));
            } catch (NumberFormatException e) {
                log.warn("Invalid storage.presignedExpiryDays override: {}", presignedExpiryDays);
            }
        }
    }

    private void applyBooleanOverride(String rawValue, Consumer<Boolean> setter) {
        if (StringUtils.hasText(rawValue)) {
            setter.accept(Boolean.parseBoolean(rawValue.trim()));
        }
    }

    /**
     * 从 DB 读取平台级覆盖值（key -> value，仅包含非空 value），
     * 与 SystemConfigRestService.getOverrideValues 语义一致，但不走缓存。
     */
    private Map<String, String> loadOverridesFromRepository() {
        Map<String, String> overrides = new HashMap<>();
        List<SystemConfigEntity> entities = systemConfigRepository
                .findByOrgUidAndDeletedFalse(SystemConfigConsts.PLATFORM_CONFIG_ORG_UID);
        for (SystemConfigEntity entity : entities) {
            if (StringUtils.hasText(entity.getConfigValue())) {
                overrides.put(entity.getConfigKey(), entity.getConfigValue());
            }
        }
        return overrides;
    }

    /**
     * 快照静态默认值（覆盖应用前的 properties/compose 值），供查询接口「默认值」列回显。
     * SecretKey 不快照：敏感 key 无静态默认值回显。
     */
    private void snapshotDefaults() {
        defaultUploadDir = bytedeskProperties.getUpload().getDir();
        defaultUploadUrl = bytedeskProperties.getUpload().getUrl();
        defaultMaxFileSize = String.valueOf(uploadSecurityConfig.getMaxFileSize());
        defaultEnableImageValidation = String.valueOf(uploadSecurityConfig.isEnableImageValidation());
        defaultEnableFileNameFilter = String.valueOf(uploadSecurityConfig.isEnableFileNameFilter());
        defaultForceRename = String.valueOf(uploadSecurityConfig.isForceRename());
        defaultMaxFileNameLength = String.valueOf(uploadSecurityConfig.getMaxFileNameLength());
        defaultEnableUploadLog = String.valueOf(uploadSecurityConfig.isEnableUploadLog());
        defaultEnableVirusScan = String.valueOf(uploadSecurityConfig.isEnableVirusScan());
        BytedeskProperties.Storage storage = bytedeskProperties.getStorage();
        defaultStorageProvider = storage.getProvider();
        defaultStorageEndpoint = storage.getEndpoint();
        defaultStorageAccessKey = storage.getAccessKey();
        defaultStorageBucketName = storage.getBucketName();
        defaultStorageRegion = storage.getRegion();
        defaultStorageSecure = storage.getSecure() == null ? null : String.valueOf(storage.getSecure());
        defaultStoragePublicRead = storage.getPublicRead() == null ? null : String.valueOf(storage.getPublicRead());
        defaultStoragePresignedExpiryDays = storage.getPresignedExpiryDays() == null ? null
                : String.valueOf(storage.getPresignedExpiryDays());
    }

    /**
     * 读取 key 对应的静态默认值（快照），供 SystemConfigRestService 展示
     */
    public String getDefaultValue(SystemConfigKeyEnum keyEnum) {
        switch (keyEnum) {
            case UPLOAD_DIR:
                return defaultUploadDir;
            case UPLOAD_URL:
                return defaultUploadUrl;
            case UPLOAD_SECURITY_MAX_FILE_SIZE:
                return defaultMaxFileSize;
            case UPLOAD_SECURITY_ENABLE_IMAGE_VALIDATION:
                return defaultEnableImageValidation;
            case UPLOAD_SECURITY_ENABLE_FILE_NAME_FILTER:
                return defaultEnableFileNameFilter;
            case UPLOAD_SECURITY_FORCE_RENAME:
                return defaultForceRename;
            case UPLOAD_SECURITY_MAX_FILE_NAME_LENGTH:
                return defaultMaxFileNameLength;
            case UPLOAD_SECURITY_ENABLE_UPLOAD_LOG:
                return defaultEnableUploadLog;
            case UPLOAD_SECURITY_ENABLE_VIRUS_SCAN:
                return defaultEnableVirusScan;
            case STORAGE_PROVIDER:
                return defaultStorageProvider;
            case STORAGE_ENDPOINT:
                return defaultStorageEndpoint;
            case STORAGE_ACCESS_KEY:
                return defaultStorageAccessKey;
            case STORAGE_SECRET_KEY:
                return null; // 敏感 key 无静态默认值回显
            case STORAGE_BUCKET_NAME:
                return defaultStorageBucketName;
            case STORAGE_REGION:
                return defaultStorageRegion;
            case STORAGE_SECURE:
                return defaultStorageSecure;
            case STORAGE_PUBLIC_READ:
                return defaultStoragePublicRead;
            case STORAGE_PRESIGNED_EXPIRY_DAYS:
                return defaultStoragePresignedExpiryDays;
            default:
                return null;
        }
    }
}
