/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-10-21 09:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-10-21 09:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.provider.aliyun.openai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.bytedesk.ai.llm_provider.LlmProviderEntity;
import com.bytedesk.ai.llm_provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotRepository;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.llm.LlmProviderConstants;

/**
 * 存量迁移映射规则测试（规划 4.7，验收矩阵“迁移”）。
 */
@ExtendWith(MockitoExtension.class)
class AliyunOpenaiRobotMigrationTest {

    @Mock
    private LlmProviderRestService llmProviderRestService;

    @Mock
    private RobotRepository robotRepository;

    @Mock
    private RobotRestService robotRestService;

    private AliyunOpenaiRobotMigration migration;

    @BeforeEach
    void setUp() {
        migration = new AliyunOpenaiRobotMigration(llmProviderRestService, robotRepository, robotRestService);
    }

    private LlmProviderEntity provider(String uid, String type, String level, String orgUid) {
        LlmProviderEntity entity = new LlmProviderEntity();
        entity.setUid(uid);
        entity.setType(type);
        entity.setLevel(level);
        entity.setOrgUid(orgUid);
        entity.setEnabled(true);
        return entity;
    }

    private RobotEntity robot(String uid, String orgUid, String providerUid, String model) {
        RobotEntity entity = new RobotEntity();
        entity.setUid(uid);
        entity.setOrgUid(orgUid);
        entity.setLlm(RobotLlm.builder()
                .textProvider(LlmProviderConstants.DASHSCOPE)
                .textProviderUid(providerUid)
                .textModel(model)
                .build());
        return entity;
    }

    @Test
    void orgRobotMapsToSameOrgTargetNotPlatform() {
        RobotEntity robotEntity = robot("robot-1", "org-1", "dashscope-org-uid", "qwen3.8-max");
        when(robotRepository.findByLlmTextProviderAndDeletedFalse(eq(LlmProviderConstants.DASHSCOPE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(robotEntity)));
        when(llmProviderRestService.findByUid("dashscope-org-uid"))
                .thenReturn(Optional.of(provider("dashscope-org-uid", LlmProviderConstants.DASHSCOPE,
                        LevelEnum.ORGANIZATION.name(), "org-1")));
        when(llmProviderRestService.findByTypeAndOrgUid(LlmProviderConstants.ALIYUN_OPENAI, "org-1"))
                .thenReturn(Optional.of(provider("aliyun-org-uid", LlmProviderConstants.ALIYUN_OPENAI,
                        LevelEnum.ORGANIZATION.name(), "org-1")));

        AliyunOpenaiRobotMigration.MigrationReport report = migration.preview();

        assertThat(report.getBlocked()).isZero();
        assertThat(report.getMigratable()).isEqualTo(1);
        assertThat(report.getMigratedItems()).isEmpty(); // 预览不写库
        var item = report.getMigratableSnapshot().get("robot-1");
        assertThat(item.targetUid()).isEqualTo("aliyun-org-uid");
    }

    @Test
    void missingOrgTargetBlocksInsteadOfPlatformFallback() {
        RobotEntity robotEntity = robot("robot-1", "org-1", "dashscope-org-uid", "qwen3.8-flash");
        when(robotRepository.findByLlmTextProviderAndDeletedFalse(eq(LlmProviderConstants.DASHSCOPE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(robotEntity)));
        when(llmProviderRestService.findByUid("dashscope-org-uid"))
                .thenReturn(Optional.of(provider("dashscope-org-uid", LlmProviderConstants.DASHSCOPE,
                        LevelEnum.ORGANIZATION.name(), "org-1")));
        when(llmProviderRestService.findByTypeAndOrgUid(LlmProviderConstants.ALIYUN_OPENAI, "org-1"))
                .thenReturn(Optional.empty());

        AliyunOpenaiRobotMigration.MigrationReport report = migration.preview();

        assertThat(report.getBlocked()).isEqualTo(1);
        assertThat(report.getBlockedReasons().get(0)).contains("org aliyun_openai provider missing");
    }

    @Test
    void nonTargetModelsAreIgnored() {
        RobotEntity robotEntity = robot("robot-1", "org-1", "dashscope-org-uid", "qwen-plus");
        when(robotRepository.findByLlmTextProviderAndDeletedFalse(eq(LlmProviderConstants.DASHSCOPE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(robotEntity)));

        AliyunOpenaiRobotMigration.MigrationReport report = migration.preview();

        assertThat(report.getCandidates()).isZero();
        assertThat(report.getBlocked()).isZero();
    }

    @Test
    void nonUniquePlatformTargetBlocks() {
        RobotEntity robotEntity = robot("robot-1", "org-1", "dashscope-platform-uid", "qwen3.8-max");
        when(robotRepository.findByLlmTextProviderAndDeletedFalse(eq(LlmProviderConstants.DASHSCOPE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(robotEntity)));
        when(llmProviderRestService.findByUid("dashscope-platform-uid"))
                .thenReturn(Optional.of(provider("dashscope-platform-uid", LlmProviderConstants.DASHSCOPE,
                        LevelEnum.PLATFORM.name(), "")));
        when(llmProviderRestService.findByType(LlmProviderConstants.ALIYUN_OPENAI, LevelEnum.PLATFORM.name()))
                .thenReturn(List.of(
                        provider("aliyun-platform-1", LlmProviderConstants.ALIYUN_OPENAI,
                                LevelEnum.PLATFORM.name(), ""),
                        provider("aliyun-platform-2", LlmProviderConstants.ALIYUN_OPENAI,
                                LevelEnum.PLATFORM.name(), "")));

        AliyunOpenaiRobotMigration.MigrationReport report = migration.preview();

        assertThat(report.getBlocked()).isEqualTo(1);
        assertThat(report.getBlockedReasons().get(0)).contains("not unique");
    }

    @Test
    void executeSkipsRobotChangedAfterPreview() {
        AliyunOpenaiRobotMigration.MigrationReport preview = new AliyunOpenaiRobotMigration.MigrationReport();
        preview.candidates = 1;
        preview.migratable.put("robot-1",
                new AliyunOpenaiRobotMigration.MigrationItem("robot-1", "dashscope-org-uid", "aliyun-org-uid",
                        "qwen3.8-max"));

        // 预览后被人工改为其他模型：不得覆盖
        when(robotRepository.findByUid("robot-1"))
                .thenReturn(Optional.of(robot("robot-1", "org-1", "dashscope-org-uid", "qwen-plus")));

        AliyunOpenaiRobotMigration.MigrationReport result = migration.execute(preview);

        assertThat(result.getMigrated()).isZero();
        assertThat(result.getBlocked()).isEqualTo(1);
        assertThat(result.getBlockedReasons().get(0)).contains("changed after preview");
    }

    @Test
    void executeUpdatesProviderAndUidKeepingModel() {
        AliyunOpenaiRobotMigration.MigrationReport preview = new AliyunOpenaiRobotMigration.MigrationReport();
        preview.candidates = 1;
        preview.migratable.put("robot-1",
                new AliyunOpenaiRobotMigration.MigrationItem("robot-1", "dashscope-org-uid", "aliyun-org-uid",
                        "qwen3.8-max"));

        RobotEntity robotEntity = robot("robot-1", "org-1", "dashscope-org-uid", "qwen3.8-max");
        when(robotRepository.findByUid("robot-1")).thenReturn(Optional.of(robotEntity));
        when(robotRestService.save(robotEntity)).thenReturn(robotEntity);

        AliyunOpenaiRobotMigration.MigrationReport result = migration.execute(preview);

        assertThat(result.getMigrated()).isEqualTo(1);
        assertThat(result.getBlocked()).isZero();
        assertThat(robotEntity.getLlm().getTextProvider()).isEqualTo(LlmProviderConstants.ALIYUN_OPENAI);
        assertThat(robotEntity.getLlm().getTextProviderUid()).isEqualTo("aliyun-org-uid");
        assertThat(robotEntity.getLlm().getTextModel()).isEqualTo("qwen3.8-max");
    }

}
