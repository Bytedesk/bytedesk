/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-10-20 21:20:00
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.llm_provider.LlmProviderEntity;
import com.bytedesk.ai.llm_provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotRepository;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.llm.LlmProviderConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云百炼 OpenAI 兼容模式接入的存量机器人数据迁移（规划 4.7）。
 *
 * <p>qwen3.8-max / qwen3.8-flash 仅支持 OpenAI 兼容协议，已从 dashscope 目录迁移到
 * aliyun_openai。存量机器人若仍配置 dashscope + 这两个模型，需将其文本供应商切换为
 * aliyun_openai 并同步修正 textProviderUid。</p>
 *
 * <p>与初版“启动即无条件改库”不同（规划 B2/B6）：</p>
 * <ul>
 * <li>默认只做只读预览并输出报告，不写库；</li>
 * <li>须同时开启 chat 与独立迁移开关 bytedesk.ai.aliyun-openai.migration.enabled 才执行；</li>
 * <li>按“来源 provider UID → 同租户同 level 目标 provider UID”一对一映射，
 * 不把所有组织合并到首个平台记录；</li>
 * <li>目标缺失/禁用/映射不唯一/机器人并发变更时阻断该项并计入报告，
 * 不静默跳过，不覆盖人工修改；</li>
 * <li>经 {@link RobotRestService#save} 保存以复用乐观锁与缓存失效语义。</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AliyunOpenaiRobotMigration implements ApplicationRunner {

    /** 仅这两个精确模型名参与迁移，不按 qwen3.8-* 前缀扩展（规划 4.7.1-1）。 */
    static final Set<String> OPENAI_ONLY_MODELS = Set.of("qwen3.8-max", "qwen3.8-flash");

    private static final int BATCH_SIZE = 100;

    private final LlmProviderRestService llmProviderRestService;

    private final RobotRepository robotRepository;

    private final RobotRestService robotRestService;

    @Value("${spring.ai.aliyun_openai.chat.enabled:false}")
    private boolean chatEnabled;

    /** 独立迁移开关：仅开启 chat 不会触发写库（规划 4.5）。 */
    @Value("${bytedesk.ai.aliyun-openai.migration.enabled:false}")
    private boolean migrationEnabled;

    @Override
    public void run(ApplicationArguments args) {
        try {
            if (!chatEnabled) {
                log.info("[AliyunOpenaiMigration] chat disabled, skip");
                return;
            }
            MigrationReport report = preview();
            if (!migrationEnabled) {
                log.info("[AliyunOpenaiMigration] migration switch off, preview only: {}", report.summary());
                return;
            }
            if (report.getBlocked() > 0) {
                log.warn("[AliyunOpenaiMigration] {} blocked robots, execute after resolving: {}",
                        report.getBlocked(), report.summary());
            }
            MigrationReport executed = execute(report);
            log.info("[AliyunOpenaiMigration] executed: {}", executed.summary());
        } catch (Exception e) {
            // 迁移失败不阻断应用启动，但必须显式留痕，不输出“全部完成”
            log.error("[AliyunOpenaiMigration] failed", e);
        }
    }

    /**
     * 只读预览：扫描候选机器人并建立来源→目标映射，不写库。
     */
    public MigrationReport preview() {
        MigrationReport report = new MigrationReport();
        Map<String, String> targetCache = new HashMap<>();

        int pageNumber = 0;
        Page<RobotEntity> page;
        do {
            Pageable pageable = PageRequest.of(pageNumber, BATCH_SIZE, Sort.Direction.ASC, "id");
            page = robotRepository.findByLlmTextProviderAndDeletedFalse(
                    LlmProviderConstants.DASHSCOPE, pageable);
            for (RobotEntity robot : page.getContent()) {
                RobotLlm llm = robot.getLlm();
                if (llm == null || !OPENAI_ONLY_MODELS.contains(llm.getTextModel())) {
                    continue;
                }
                report.candidates++;
                String sourceUid = llm.getTextProviderUid();
                String targetUid = resolveTargetUid(robot, sourceUid, targetCache, report);
                if (targetUid == null) {
                    continue; // 已计入 blocked
                }
                report.migratable.put(robot.getUid(),
                        new MigrationItem(robot.getUid(), sourceUid, targetUid, llm.getTextModel()));
            }
            pageNumber++;
        } while (page.hasNext());

        return report;
    }

    /**
     * 显式执行：逐项复核当前值仍等于预览三元组后更新，失败计入报告。
     * <p>刻意不加类级事务：单项失败只阻断该项，不回滚整批（规划 4.7.2）；
     * 每次保存经 {@link RobotRestService#save} 独立提交并失效缓存。</p>
     */
    public MigrationReport execute(MigrationReport previewReport) {
        MigrationReport result = new MigrationReport();
        result.candidates = previewReport.candidates;

        for (MigrationItem item : previewReport.migratable.values()) {
            try {
                RobotEntity robot = robotRepository.findByUid(item.robotUid).orElse(null);
                if (robot == null || robot.isDeleted() || robot.getLlm() == null) {
                    result.blocked.add(item.robotUid + ": robot missing or deleted");
                    continue;
                }
                RobotLlm llm = robot.getLlm();
                // 复核预览三元组：并发人工修改则阻断，不覆盖（规划 4.7.2）
                if (!LlmProviderConstants.DASHSCOPE.equals(llm.getTextProvider())
                        || !OPENAI_ONLY_MODELS.contains(llm.getTextModel())
                        || (StringUtils.hasText(item.sourceUid)
                                && !item.sourceUid.equals(llm.getTextProviderUid()))) {
                    result.blocked.add(item.robotUid + ": robot changed after preview");
                    continue;
                }
                llm.setTextProvider(LlmProviderConstants.ALIYUN_OPENAI);
                llm.setTextProviderUid(item.targetUid);
                robotRestService.save(robot);
                result.migrated.put(item.robotUid, item);
            } catch (Exception e) {
                log.error("[AliyunOpenaiMigration] migrate robot {} failed", item.robotUid, e);
                result.blocked.add(item.robotUid + ": " + e.getMessage());
            }
        }
        return result;
    }

    /**
     * 来源 provider UID → 同租户目标 provider UID。空来源 UID 仅在原作用域唯一时补全。
     */
    private String resolveTargetUid(RobotEntity robot, String sourceUid,
            Map<String, String> targetCache, MigrationReport report) {
        String orgUid = robot.getOrgUid();
        String cacheKey = (StringUtils.hasText(sourceUid) ? sourceUid : "") + "|" + orgUid;
        if (targetCache.containsKey(cacheKey)) {
            return targetCache.get(cacheKey);
        }

        LlmProviderEntity source = null;
        if (StringUtils.hasText(sourceUid)) {
            source = llmProviderRestService.findByUid(sourceUid).orElse(null);
            if (source == null || source.isDeleted()) {
                report.blocked.add(robot.getUid() + ": source provider missing " + sourceUid);
                targetCache.put(cacheKey, null);
                return null;
            }
        }

        String level = source != null ? source.getLevel() : LevelEnum.ORGANIZATION.name();
        String targetUid = null;
        if (LevelEnum.PLATFORM.name().equals(level)) {
            // 平台来源保持平台范围：取唯一平台目标，多个即阻断（规划 4.7.1-3）
            List<LlmProviderEntity> targets = llmProviderRestService.findByType(
                    LlmProviderConstants.ALIYUN_OPENAI, LevelEnum.PLATFORM.name());
            if (targets.size() == 1) {
                targetUid = targets.get(0).getUid();
            } else if (targets.isEmpty()) {
                report.blocked.add(robot.getUid() + ": platform aliyun_openai provider missing");
            } else {
                report.blocked.add(robot.getUid() + ": platform aliyun_openai provider not unique");
            }
        } else {
            // 组织来源保持组织范围：不回退平台记录，不跨租户（规划 B2）
            if (!StringUtils.hasText(orgUid)) {
                report.blocked.add(robot.getUid() + ": org robot without orgUid");
            } else {
                var target = llmProviderRestService.findByTypeAndOrgUid(
                        LlmProviderConstants.ALIYUN_OPENAI, orgUid);
                if (target.isPresent()) {
                    targetUid = target.get().getUid();
                } else {
                    report.blocked.add(robot.getUid() + ": org aliyun_openai provider missing for " + orgUid);
                }
            }
        }

        targetCache.put(cacheKey, targetUid);
        return targetUid;
    }

    /**
     * 迁移报告：仅含 UID、模型与原因，不含 Key 或对话内容（规划 4.7.1-6）。
     */
    public static class MigrationReport {
        int candidates;
        final Map<String, MigrationItem> migratable = new LinkedHashMap<>();
        final Map<String, MigrationItem> migrated = new LinkedHashMap<>();
        final List<String> blocked = new ArrayList<>();

        public int getCandidates() {
            return candidates;
        }

        public int getMigratable() {
            return migratable.size();
        }

        public int getMigrated() {
            return migrated.size();
        }

        public int getBlocked() {
            return blocked.size();
        }

        public List<String> getBlockedReasons() {
            return List.copyOf(blocked);
        }

        public Map<String, MigrationItem> getMigratedItems() {
            return Map.copyOf(migrated);
        }

        public Map<String, MigrationItem> getMigratableSnapshot() {
            return Map.copyOf(migratable);
        }

        String summary() {
            return "candidates=" + candidates + ", migratable=" + migratable.size()
                    + ", migrated=" + migrated.size() + ", blocked=" + blocked.size();
        }
    }

    /** 单个机器人的原始三元组与目标三元组（回滚依据，规划 4.7.1-6）。 */
    public record MigrationItem(String robotUid, String sourceUid, String targetUid, String model) {
    }

}
