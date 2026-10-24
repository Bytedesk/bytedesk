/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 17:10:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-10-22 21:50:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.llm_provider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.llm_model.LlmModelJsonLoader;
import com.bytedesk.ai.llm_model.LlmModelRestService;
import com.bytedesk.ai.llm_model.LlmModelJsonLoader.ModelJson;
import com.bytedesk.ai.llm_provider.LlmProviderJsonLoader.ProviderJson;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("llmProviderInitializer")
@AllArgsConstructor
public class LlmProviderInitializer implements SmartInitializingSingleton {

    // 组织处理进度日志间隔，避免每个组织一行 INFO 刷屏
    private static final int ORG_PROGRESS_LOG_INTERVAL = 100;

    private final LlmProviderRestService llmProviderRestService;

    private final LlmProviderJsonLoader llmProviderJsonService;

    private final LlmModelRestService llmModelRestService;

    private final LlmModelJsonLoader llmModelJsonService;

    private final OrganizationRestService organizationRestService;

    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        init();
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = LlmProviderPermissions.LLM_PROVIDER_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }

    /**
     * 初始化 LLM 提供商和模型
     */
    public void init() {
        Map<String, ProviderJson> providerJsonMap = llmProviderJsonService.loadProviders();
        initPlatformProviders(providerJsonMap);
        initPlatformModels();
        initOrganizationProviders(providerJsonMap);
    }

    /**
     * 平台级供应商初始化：缺失则按 providers.json 创建，已存在则同步 status
     */
    private void initPlatformProviders(Map<String, ProviderJson> providerJsonMap) {
        String level = LevelEnum.PLATFORM.name();
        for (Map.Entry<String, ProviderJson> entry : providerJsonMap.entrySet()) {
            String providerType = entry.getKey();
            ProviderJson providerJson = entry.getValue();
            if (!llmProviderRestService.existsByTypeAndLevel(providerType, level)) {
                String orgUid = ""; // 平台版本暂时不支持组织
                llmProviderRestService.createFromProviderJson(providerType, providerJson, level, orgUid);
            } else {
                // 如果已经存在，则更新，因为status有可能从DEVELOPMENT变为PRODUCTION
                List<LlmProviderEntity> providerList = llmProviderRestService.findByType(providerType, level);
                if (!providerList.isEmpty()) {
                    LlmProviderEntity providerEntity = providerList.get(0);
                    String status = providerJson.getStatus();
                    if (!providerEntity.getStatus().equals(status)) {
                        providerEntity.setStatus(status);
                        llmProviderRestService.save(providerEntity);
                    }
                }
            }
        }
    }

    /**
     * 平台级模型初始化：为已存在的平台级供应商补齐缺失模型
     */
    private void initPlatformModels() {
        String level = LevelEnum.PLATFORM.name();
        Map<String, List<ModelJson>> modelJsonMap = llmModelJsonService.loadModels();
        for (Map.Entry<String, List<ModelJson>> entry : modelJsonMap.entrySet()) {
            String providerType = entry.getKey();
            List<LlmProviderEntity> providerList = llmProviderRestService.findByType(providerType, level);
            if (!providerList.isEmpty()) {
                LlmProviderEntity provider = providerList.get(0);
                String providerUid = provider.getUid();
                List<ModelJson> modelJsons = entry.getValue();
                for (ModelJson modelJson : modelJsons) {
                    if (!llmModelRestService.existsByNameAndProviderUid(modelJson.getName(), providerUid)) {
                        String orgUid = ""; // 平台版本暂时不支持组织
                        llmModelRestService.createFromModelJson(providerUid, providerType, modelJson, level, orgUid);
                    }
                }
            }
        }
    }

    /**
     * 组织级供应商初始化：把 PRODUCTION 供应商幂等复制到每个组织。
     * 必须按 type + level + orgUid 精确查询当前组织的记录：
     * 1. 不能调用不含 orgUid 的列表查询后取第一条，否则会跨组织加载并可能更新到其他组织的记录；
     * 2. Optional 精确查询遇到重复记录会抛错，显式暴露数据问题，而不是随机取第一条；
     * 3. 状态未变化时不写库，避免每次重启都产生无意义的更新。
     */
    private void initOrganizationProviders(Map<String, ProviderJson> providerJsonMap) {
        Map<String, ProviderJson> productionProviderMap = new LinkedHashMap<>();
        for (Map.Entry<String, ProviderJson> entry : providerJsonMap.entrySet()) {
            if (LlmProviderStatusEnum.PRODUCTION.name().equalsIgnoreCase(entry.getValue().getStatus())) {
                productionProviderMap.put(entry.getKey(), entry.getValue());
            }
        }
        if (productionProviderMap.isEmpty()) {
            log.info("llm provider organization init skipped: no production providers");
            return;
        }
        // 组织列表只读取一次，避免按供应商类型重复加载
        List<OrganizationEntity> organizationList = organizationRestService.findAll();
        String level = LevelEnum.ORGANIZATION.name();
        log.info("llm provider organization init start: {} production providers, {} organizations",
                productionProviderMap.size(), organizationList.size());
        long startMillis = System.currentTimeMillis();
        int created = 0;
        int updated = 0;
        int skipped = 0;
        for (Map.Entry<String, ProviderJson> entry : productionProviderMap.entrySet()) {
            String providerType = entry.getKey();
            ProviderJson providerJson = entry.getValue();
            String status = providerJson.getStatus();
            long typeStartMillis = System.currentTimeMillis();
            int processed = 0;
            for (OrganizationEntity organizationEntity : organizationList) {
                String orgUid = organizationEntity.getUid();
                try {
                    Optional<LlmProviderEntity> existing = llmProviderRestService
                            .findByTypeAndLevelAndOrgUid(providerType, level, orgUid);
                    if (existing.isEmpty()) {
                        llmProviderRestService.createFromProviderJson(providerType, providerJson, level, orgUid);
                        created++;
                    } else {
                        LlmProviderEntity providerEntity = existing.get();
                        if (!Objects.equals(providerEntity.getStatus(), status)) {
                            providerEntity.setStatus(status);
                            llmProviderRestService.save(providerEntity);
                            updated++;
                        } else {
                            skipped++;
                        }
                    }
                } catch (RuntimeException e) {
                    // 不吞异常：记录失败上下文后继续抛出，让启动失败而不是假装初始化成功
                    log.error("llm provider '{}' init failed for organization {}", providerType, orgUid, e);
                    throw e;
                }
                processed++;
                if (processed % ORG_PROGRESS_LOG_INTERVAL == 0) {
                    log.info("llm provider '{}' init progress: {}/{} organizations",
                            providerType, processed, organizationList.size());
                }
            }
            log.info("llm provider '{}' init finished in {} ms", providerType,
                    System.currentTimeMillis() - typeStartMillis);
        }
        log.info("llm provider organization init finished: created={}, updated={}, skipped={}, elapsed {} ms",
                created, updated, skipped, System.currentTimeMillis() - startMillis);
    }

}
