/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-10-22 22:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-10-22 22:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.llm_provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.ai.llm_model.LlmModelJsonLoader;
import com.bytedesk.ai.llm_model.LlmModelRestService;
import com.bytedesk.ai.llm_provider.LlmProviderJsonLoader.ProviderJson;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRestService;

/**
 * 组织级 LLM 供应商初始化测试（启动性能修复验证）。
 *
 * 覆盖 BYTEDESK_STARTUP_FIX_GUIDE 6.1 场景：
 * - 跨组织隔离：只按 type+level+orgUid 精确读取当前组织记录，不调用列表查询取第一条
 * - 幂等：状态相同不保存；状态不同只更新当前组织记录
 * - 缺失补建：只为当前组织创建
 * - 异常路径：初始化失败不吞异常
 * - 空组织列表 / 非 PRODUCTION 供应商筛选
 */
@ExtendWith(MockitoExtension.class)
class LlmProviderInitializerTest {

    private static final String ORG_LEVEL = LevelEnum.ORGANIZATION.name();
    private static final String PLATFORM_LEVEL = LevelEnum.PLATFORM.name();

    @Mock
    private LlmProviderRestService llmProviderRestService;

    @Mock
    private LlmProviderJsonLoader llmProviderJsonService;

    @Mock
    private LlmModelRestService llmModelRestService;

    @Mock
    private LlmModelJsonLoader llmModelJsonService;

    @Mock
    private OrganizationRestService organizationRestService;

    @Mock
    private AuthorityRestService authorityRestService;

    private LlmProviderInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new LlmProviderInitializer(
                llmProviderRestService,
                llmProviderJsonService,
                llmModelRestService,
                llmModelJsonService,
                organizationRestService,
                authorityRestService);
        // init() 每次都会执行平台级模型初始化；真实 loadModels 不会返回 null，mock 需给出空 Map
        when(llmModelJsonService.loadModels()).thenReturn(new HashMap<>());
    }

    private ProviderJson providerJson(String status) {
        ProviderJson json = new ProviderJson();
        json.setNickname("test");
        json.setLogo("logo.png");
        json.setBaseUrl("https://api.example.com");
        json.setWebUrl("https://www.example.com");
        json.setStatus(status);
        return json;
    }

    private OrganizationEntity organization(String uid) {
        return OrganizationEntity.builder().uid(uid).name("org-" + uid).build();
    }

    private LlmProviderEntity provider(String type, String orgUid, String status) {
        return LlmProviderEntity.builder()
                .uid("provider-" + type + "-" + orgUid)
                .type(type)
                .level(ORG_LEVEL)
                .orgUid(orgUid)
                .status(status)
                .build();
    }

    @Test
    void orgInitUsesExactQueryPerOrganization_neverListQuery() {
        // 两个组织都有同类型供应商：每个组织一次精确读取，不混用实体
        when(llmProviderJsonService.loadProviders()).thenReturn(Map.of("zhipuai", providerJson("PRODUCTION")));
        when(organizationRestService.findAll()).thenReturn(
                List.of(organization("org-a"), organization("org-b")));
        when(llmProviderRestService.existsByTypeAndLevel("zhipuai", PLATFORM_LEVEL)).thenReturn(false);
        when(llmProviderRestService.findByTypeAndLevelAndOrgUid(eq("zhipuai"), eq(ORG_LEVEL), anyString()))
                .thenReturn(Optional.of(provider("zhipuai", "org-a", "PRODUCTION")))
                .thenReturn(Optional.of(provider("zhipuai", "org-b", "PRODUCTION")));

        initializer.init();

        // 组织分支不得调用不含 orgUid 的列表查询
        verify(llmProviderRestService, never()).findByType(anyString(), eq(ORG_LEVEL));
        // 每个 (type, level, orgUid) 恰好一次精确读取
        verify(llmProviderRestService).findByTypeAndLevelAndOrgUid("zhipuai", ORG_LEVEL, "org-a");
        verify(llmProviderRestService).findByTypeAndLevelAndOrgUid("zhipuai", ORG_LEVEL, "org-b");
        // 状态相同：不调用保存
        verify(llmProviderRestService, never()).save(any(LlmProviderEntity.class));
    }

    @Test
    void missingProvider_createdOnlyForCurrentOrganization() {
        // 当前组织记录缺失：只为当前组织创建一条
        when(llmProviderJsonService.loadProviders()).thenReturn(Map.of("zhipuai", providerJson("PRODUCTION")));
        when(organizationRestService.findAll()).thenReturn(List.of(organization("org-a")));
        when(llmProviderRestService.existsByTypeAndLevel("zhipuai", PLATFORM_LEVEL)).thenReturn(false);
        when(llmProviderRestService.findByTypeAndLevelAndOrgUid("zhipuai", ORG_LEVEL, "org-a"))
                .thenReturn(Optional.empty());

        initializer.init();

        verify(llmProviderRestService).createFromProviderJson(
                eq("zhipuai"), any(ProviderJson.class), eq(ORG_LEVEL), eq("org-a"));
        verify(llmProviderRestService, never()).save(any(LlmProviderEntity.class));
    }

    @Test
    void statusMismatch_updatesOnlyCurrentOrganizationRecord() {
        // 当前组织状态需要更新：只更新当前组织记录
        when(llmProviderJsonService.loadProviders()).thenReturn(Map.of("zhipuai", providerJson("PRODUCTION")));
        when(organizationRestService.findAll()).thenReturn(
                List.of(organization("org-a"), organization("org-b")));
        when(llmProviderRestService.existsByTypeAndLevel("zhipuai", PLATFORM_LEVEL)).thenReturn(false);
        LlmProviderEntity orgAProvider = provider("zhipuai", "org-a", "DEVELOPMENT");
        when(llmProviderRestService.findByTypeAndLevelAndOrgUid("zhipuai", ORG_LEVEL, "org-a"))
                .thenReturn(Optional.of(orgAProvider));
        when(llmProviderRestService.findByTypeAndLevelAndOrgUid("zhipuai", ORG_LEVEL, "org-b"))
                .thenReturn(Optional.of(provider("zhipuai", "org-b", "PRODUCTION")));

        initializer.init();

        verify(llmProviderRestService).save(orgAProvider);
        assertThat(orgAProvider.getStatus()).isEqualTo("PRODUCTION");
        // org-b 状态未变，不保存
        verify(llmProviderRestService, times(1)).save(any(LlmProviderEntity.class));
    }

    @Test
    void duplicateRecords_failExplicitly_notFirstRecord() {
        // 当前组织有效记录重复：精确查询抛错，显式暴露数据问题，不随机取第一条
        when(llmProviderJsonService.loadProviders()).thenReturn(Map.of("zhipuai", providerJson("PRODUCTION")));
        when(organizationRestService.findAll()).thenReturn(List.of(organization("org-a")));
        when(llmProviderRestService.existsByTypeAndLevel("zhipuai", PLATFORM_LEVEL)).thenReturn(false);
        when(llmProviderRestService.findByTypeAndLevelAndOrgUid("zhipuai", ORG_LEVEL, "org-a"))
                .thenThrow(new RuntimeException("Duplicate row: expected single result"));

        assertThatThrownBy(() -> initializer.init())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Duplicate row");
    }

    @Test
    void emptyOrganizationList_platformAndModelInitStillRun() {
        // 空组织列表：组织阶段正常完成
        when(llmProviderJsonService.loadProviders()).thenReturn(Map.of("zhipuai", providerJson("PRODUCTION")));
        when(organizationRestService.findAll()).thenReturn(Collections.emptyList());
        when(llmProviderRestService.existsByTypeAndLevel("zhipuai", PLATFORM_LEVEL)).thenReturn(false);

        initializer.init();

        verify(llmProviderRestService, never()).findByTypeAndLevelAndOrgUid(anyString(), anyString(), anyString());
        verify(llmProviderRestService, never()).createFromProviderJson(anyString(), any(), anyString(), anyString());
    }

    @Test
    void nonProductionProviders_skippedInOrganizationPhase() {
        // 非 PRODUCTION 供应商：保留原有筛选行为，不进入组织阶段
        when(llmProviderJsonService.loadProviders()).thenReturn(Map.of("devonly", providerJson("DEVELOPMENT")));
        when(llmProviderRestService.existsByTypeAndLevel("devonly", PLATFORM_LEVEL)).thenReturn(true);
        when(llmProviderRestService.findByType("devonly", PLATFORM_LEVEL))
                .thenReturn(List.of(provider("devonly", "", "DEVELOPMENT")));

        initializer.init();

        verify(organizationRestService, never()).findAll();
        verify(llmProviderRestService, never()).findByTypeAndLevelAndOrgUid(anyString(), anyString(), anyString());
    }

    @Test
    void organizationListLoadedOnce_notPerProviderType() {
        // 组织列表只加载一次，不按供应商类型重复读取
        Map<String, ProviderJson> providers = new HashMap<>();
        providers.put("zhipuai", providerJson("PRODUCTION"));
        providers.put("deepseek", providerJson("PRODUCTION"));
        providers.put("ollama", providerJson("PRODUCTION"));
        when(llmProviderJsonService.loadProviders()).thenReturn(providers);
        when(organizationRestService.findAll()).thenReturn(List.of(organization("org-a")));
        when(llmProviderRestService.existsByTypeAndLevel(anyString(), eq(PLATFORM_LEVEL))).thenReturn(false);
        when(llmProviderRestService.findByTypeAndLevelAndOrgUid(anyString(), eq(ORG_LEVEL), anyString()))
                .thenReturn(Optional.of(provider("zhipuai", "org-a", "PRODUCTION")));

        initializer.init();

        verify(organizationRestService, times(1)).findAll();
    }

    @Test
    void initFailure_propagatesNotSwallowed() {
        // 查询/保存异常：不吞异常，初始化失败
        when(llmProviderJsonService.loadProviders()).thenReturn(Map.of("zhipuai", providerJson("PRODUCTION")));
        when(organizationRestService.findAll()).thenReturn(List.of(organization("org-a")));
        when(llmProviderRestService.existsByTypeAndLevel("zhipuai", PLATFORM_LEVEL)).thenReturn(false);
        when(llmProviderRestService.findByTypeAndLevelAndOrgUid("zhipuai", ORG_LEVEL, "org-a"))
                .thenThrow(new RuntimeException("db down"));

        assertThatThrownBy(() -> initializer.init())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("db down");
    }

    @Test
    void platformProviderStatusSync_preserved() {
        // 平台级供应商状态同步行为保持：DEVELOPMENT -> PRODUCTION 更新
        when(llmProviderJsonService.loadProviders()).thenReturn(Map.of("zhipuai", providerJson("PRODUCTION")));
        when(llmProviderRestService.existsByTypeAndLevel("zhipuai", PLATFORM_LEVEL)).thenReturn(true);
        LlmProviderEntity platformProvider = LlmProviderEntity.builder()
                .uid("platform-provider").type("zhipuai").level(PLATFORM_LEVEL).orgUid("")
                .status("DEVELOPMENT").build();
        when(llmProviderRestService.findByType("zhipuai", PLATFORM_LEVEL))
                .thenReturn(List.of(platformProvider));
        when(organizationRestService.findAll()).thenReturn(Collections.emptyList());

        initializer.init();

        verify(llmProviderRestService).save(platformProvider);
        assertThat(platformProvider.getStatus()).isEqualTo("PRODUCTION");
    }
}
