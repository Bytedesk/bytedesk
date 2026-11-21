/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-19 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-11-19 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *  Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  联系：270580156@qq.com 
 *  Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.system_config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.system_config.enums.SystemConfigKeyEnum;
import com.bytedesk.core.upload.UploadSecurityConfig;

/**
 * SuperDisk 后台配置覆盖应用器单元测试：
 * - DB 覆盖值写入运行时 Bean（properties/storage/security）
 * - 静态默认值快照在应用覆盖前捕获
 * - 空值跳过、非法数字回退、SecretKey 无默认值回显
 */
class SystemConfigUploadApplierTest {

    private BytedeskProperties properties;
    private UploadSecurityConfig uploadSecurityConfig;
    private SystemConfigRepository systemConfigRepository;
    private SystemConfigUploadApplier applier;
    private Map<String, String> overrides;

    @BeforeEach
    void setUp() {
        properties = new BytedeskProperties();
        properties.getUpload().setDir("uploads");
        properties.getUpload().setUrl("http://127.0.0.1:9003");
        uploadSecurityConfig = new UploadSecurityConfig();
        systemConfigRepository = mock(SystemConfigRepository.class);
        overrides = new HashMap<>();
        when(systemConfigRepository.findByOrgUidAndDeletedFalse(anyString()))
                .thenAnswer(invocation -> overrides.entrySet().stream()
                        .filter(entry -> StringUtils.hasText(entry.getValue()))
                        .map(entry -> SystemConfigEntity.builder()
                                .configKey(entry.getKey())
                                .configValue(entry.getValue())
                                .build())
                        .collect(Collectors.toList()));

        applier = new SystemConfigUploadApplier(properties, uploadSecurityConfig, systemConfigRepository);
        applier.init();
    }

    @Test
    void appliesUploadAndStorageOverrides() {
        overrides.put(SystemConfigConsts.KEY_UPLOAD_DIR, "/data/files");
        overrides.put(SystemConfigConsts.KEY_UPLOAD_URL, "https://files.example.com");
        overrides.put(SystemConfigConsts.KEY_STORAGE_PROVIDER, "s3");
        overrides.put(SystemConfigConsts.KEY_STORAGE_ENDPOINT, "http://10.0.0.8:19000");
        overrides.put(SystemConfigConsts.KEY_STORAGE_ACCESS_KEY, "ak");
        overrides.put(SystemConfigConsts.KEY_STORAGE_BUCKET_NAME, "bucket-1");
        overrides.put(SystemConfigConsts.KEY_STORAGE_REGION, "cn-north-1");
        overrides.put(SystemConfigConsts.KEY_STORAGE_SECURE, "true");
        overrides.put(SystemConfigConsts.KEY_STORAGE_PUBLIC_READ, "false");
        overrides.put(SystemConfigConsts.KEY_STORAGE_PRESIGNED_EXPIRY_DAYS, "3");

        applier.applyOverrides();

        assertEquals("/data/files", properties.getUpload().getDir());
        assertEquals("https://files.example.com", properties.getUploadUrl());
        assertEquals(true, properties.isS3StorageEnabled());
        assertEquals("http://10.0.0.8:19000", properties.resolveStorageEndpoint());
        assertEquals("ak", properties.resolveStorageAccessKey());
        assertEquals("bucket-1", properties.resolveStorageBucketName());
        assertEquals("cn-north-1", properties.resolveStorageRegion());
        assertEquals(Boolean.TRUE, properties.resolveStorageSecure());
        assertEquals(false, properties.resolveStoragePublicRead());
        assertEquals(3 * 24 * 3600, properties.getStoragePresignedExpirySeconds());
    }

    @Test
    void appliesSecurityOverridesAndSkipsInvalidNumbers() {
        int originalMaxFileNameLength = uploadSecurityConfig.getMaxFileNameLength();

        overrides.put(SystemConfigConsts.KEY_UPLOAD_SECURITY_MAX_FILE_SIZE, "104857600");
        overrides.put(SystemConfigConsts.KEY_UPLOAD_SECURITY_MAX_FILE_NAME_LENGTH, "not-a-number");
        overrides.put(SystemConfigConsts.KEY_UPLOAD_SECURITY_ENABLE_IMAGE_VALIDATION, "true");
        overrides.put(SystemConfigConsts.KEY_UPLOAD_SECURITY_FORCE_RENAME, "false");

        applier.applyOverrides();

        assertEquals(104857600L, uploadSecurityConfig.getMaxFileSize());
        // 非法数字不覆盖，保留原值
        assertEquals(originalMaxFileNameLength, uploadSecurityConfig.getMaxFileNameLength());
        assertEquals(true, uploadSecurityConfig.isEnableImageValidation());
        assertEquals(false, uploadSecurityConfig.isForceRename());
    }

    @Test
    void snapshotDefaultsCapturedBeforeOverridesApplied() {
        // init() 时 overrides 为空，快照 = properties 静态值
        assertEquals("uploads", applier.getDefaultValue(SystemConfigKeyEnum.UPLOAD_DIR));
        assertEquals("http://127.0.0.1:9003", applier.getDefaultValue(SystemConfigKeyEnum.UPLOAD_URL));

        // 应用覆盖后再查快照，默认值列仍是启动时静态值
        overrides.put(SystemConfigConsts.KEY_UPLOAD_DIR, "/data/files");
        applier.applyOverrides();
        assertEquals("/data/files", properties.getUploadDir());
        assertEquals("uploads", applier.getDefaultValue(SystemConfigKeyEnum.UPLOAD_DIR));

        // SecretKey 敏感项无默认值回显
        assertNull(applier.getDefaultValue(SystemConfigKeyEnum.STORAGE_SECRET_KEY));
    }

    @Test
    void blankOverridesAreSkipped() {
        overrides.put(SystemConfigConsts.KEY_UPLOAD_DIR, "  ");
        overrides.put(SystemConfigConsts.KEY_STORAGE_ENDPOINT, "");

        applier.applyOverrides();

        assertEquals("uploads", properties.getUpload().getDir());
        assertEquals("http://127.0.0.1:19000", properties.getStorage().getEndpoint());
    }
}
