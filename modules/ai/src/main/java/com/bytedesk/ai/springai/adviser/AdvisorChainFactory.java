/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-10
 * @Description: Spring AI Advisor 链组装工厂，集中管理横切关注点（内容安全、输入增强、日志、多轮记忆）。
 *   从 BaseSpringAIService 抽取，避免基类膨胀；所有依赖均可选（ObjectProvider 优雅降级）。
 */
package com.bytedesk.ai.springai.adviser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.kbase.taboo.TabooService;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring AI Advisor 链组装工厂。
 *
 * <p>集中管理客服问答的横切关注点，从 {@code BaseSpringAIService} 抽取以精简基类：</p>
 * <ul>
 *   <li><b>SafeGuard</b>：内容安全/敏感词拦截（LLM 层拦截，命中即短路，不转发）</li>
 *   <li><b>ReReading</b>：Re2 输入增强（response 透传）</li>
 *   <li><b>Logging</b>：请求/响应日志（仅 debug 模式）</li>
 *   <li><b>Memory</b>：多轮对话记忆（order &gt; ToolCallingAdvisor 默认 +1000，避开 tool 循环）</li>
 * </ul>
 *
 * <p>所有依赖均为可选（{@link ObjectProvider}），社区版缺 TabooService / ChatMemoryRepository 时安全降级。</p>
 *
 * <p><b>Advisor 执行顺序（order 升序，请求侧小的先执行）</b>：</p>
 * <pre>
 *   HIGHEST_PRECEDENCE            SafeGuardAdvisor          敏感词拦截，命中即短路
 *   HIGHEST_PRECEDENCE + 100      ReReadingAdvisor          Re2 输入增强（可选）
 *   HIGHEST_PRECEDENCE + 200      SimpleLoggerAdvisor       请求/响应日志（仅 debug）
 *   HIGHEST_PRECEDENCE + 1000     ToolCallingAdvisor        框架自动注册（有 tools 时），不可手动 new
 *   HIGHEST_PRECEDENCE + 1100     MessageChatMemoryAdvisor  多轮记忆（须在 tool 循环外侧）
 * </pre>
 */
@Slf4j
@Component
public class AdvisorChainFactory {

    private final TabooService tabooService;
    private final BytedeskProperties bytedeskProperties;
    private final ChatMemoryRepository chatMemoryRepository;

    // 敏感词缓存（按 orgUid 隔离，5min TTL），避免每请求查库。
    // 复用 TabooFilterHelper 的 CacheEntry 缓存模式（enterprise/kbase）。
    private final Map<String, CachedTabooWords> tabooWordsCache = new ConcurrentHashMap<>();
    private static final long TABOO_CACHE_TTL_MS = 5L * 60 * 1000; // 5 分钟

    public AdvisorChainFactory(
            ObjectProvider<TabooService> tabooServiceProvider,
            ObjectProvider<BytedeskProperties> bytedeskPropertiesProvider,
            ObjectProvider<ChatMemoryRepository> chatMemoryRepositoryProvider) {
        this.tabooService = tabooServiceProvider.getIfAvailable();
        this.bytedeskProperties = bytedeskPropertiesProvider.getIfAvailable();
        this.chatMemoryRepository = chatMemoryRepositoryProvider.getIfAvailable();
    }

    /**
     * 根据机器人配置动态组装 Advisor 链。
     *
     * <p>当 {@code robot == null} 或 {@code robot.getLlm() == null} 时返回空 list，
     * 保证旧调用路径（健康检查、无 thread 请求等）行为零变化。</p>
     *
     * @param robot 机器人配置（含 llm 配置）；{@code null} 表示不注入任何 Advisor
     * @return 组装后的 Advisor 链；无 Advisor 时返回空 list
     */
    public List<Advisor> buildAdvisorChain(RobotProtobuf robot) {
        if (robot == null || robot.getLlm() == null) {
            return Collections.emptyList();
        }
        RobotLlm llm = robot.getLlm();
        List<Advisor> advisors = new ArrayList<>();

        // ---- SafeGuard：内容安全/敏感词拦截（LLM 层拦截，不转发）----
        // 语义与消息层 TabooFilterHelper「替换后继续发送」不同，需 safeGuardEnabled 显式开启（默认关闭）。
        // 敏感词来源复用 TabooService 数据库源（按 orgUid 隔离），避免双份词库维护。
        if (Boolean.TRUE.equals(llm.getSafeGuardEnabled()) && tabooService != null
                && StringUtils.hasText(robot.getOrgUid())) {
            Advisor safeGuardAdvisor = buildSafeGuardAdvisor(robot);
            if (safeGuardAdvisor != null) {
                advisors.add(safeGuardAdvisor);
            }
        }

        // ---- ReReading：Re2 输入增强（response 透传）----
        if (Boolean.TRUE.equals(llm.getReReadingEnabled())) {
            advisors.add(new ReReadingAdvisor());
        }

        // ---- Logging：仅 debug=true 时注入 SimpleLoggerAdvisor（请求/响应摘要）----
        if (isDebugEnabled()) {
            SimpleLoggerAdvisor loggerAdvisor = new SimpleLoggerAdvisor();
            // Spring AI 2.0 SimpleLoggerAdvisor 实现 Ordered，默认 order 较低；显式设置以固定位置。
            // 由于该 advisor 未暴露 order setter，这里依赖其默认 order（不强制调整）。
            advisors.add(loggerAdvisor);
        }

        // ---- Memory：多轮对话记忆 ----
        // order 设为 HIGHEST_PRECEDENCE + 1100（> ToolCallingAdvisor 默认 +1000），确保位于 tool 循环外侧，
        // 避免每次 tool 迭代重复持久化中间消息污染记忆（见规划 §2.2）。
        // memoryEnabled 默认 true（向后兼容）；chatMemoryRepository 不可用时安全降级（社区版无 JDBC starter）。
        if (Boolean.TRUE.equals(llm.getMemoryEnabled()) && chatMemoryRepository != null) {
            int maxMessages = llm.getContextMsgCount() != null && llm.getContextMsgCount() > 0
                    ? llm.getContextMsgCount() : 10;
            ChatMemory chatMemory = MessageWindowChatMemory.builder()
                    .chatMemoryRepository(chatMemoryRepository)
                    .maxMessages(maxMessages)
                    .build();
            advisors.add(MessageChatMemoryAdvisor.builder(chatMemory)
                    .order(Ordered.HIGHEST_PRECEDENCE + 1100)
                    .build());
        }

        return advisors;
    }

    /**
     * 构建 SafeGuardAdvisor。
     *
     * <p>敏感词来源：{@code tabooService.listEnabledWordsWithSynonyms(orgUid)}（带 5min 缓存）。
     * 词库为空时返回 {@code null}（不注入，安全降级）。</p>
     *
     * <p>拦截提示语优先级：{@code tabooService.resolveReplyForContent(orgUid, content)}（组织级数据库文案）
     * → Spring AI 默认文案。（v5.6 简化：移除机器人级 safeGuardFailureResponse 覆盖，统一由组织级 Taboo 配置管理）</p>
     */
    private Advisor buildSafeGuardAdvisor(RobotProtobuf robot) {
        List<String> sensitiveWords = getCachedTabooWords(robot.getOrgUid());
        if (sensitiveWords == null || sensitiveWords.isEmpty()) {
            // 企业版未启用（社区版 TabooServiceImpl 返回空 list）时安全降级，不拦截
            return null;
        }
        // 拦截语统一来自组织级 Taboo 配置（v5.6 移除机器人级覆盖，避免双份维护）
        String failureResponse = resolveTabooFailureResponse(robot.getOrgUid());
        SafeGuardAdvisor.Builder builder = SafeGuardAdvisor.builder()
                .sensitiveWords(sensitiveWords)
                .order(Ordered.HIGHEST_PRECEDENCE);
        if (StringUtils.hasText(failureResponse)) {
            builder.failureResponse(failureResponse);
        }
        return builder.build();
    }

    /**
     * 读取按 orgUid 缓存的敏感词（5min TTL）。
     * 复用 TabooFilterHelper 的缓存模式，避免 buildAdvisorChain 每请求查库。
     */
    private List<String> getCachedTabooWords(String orgUid) {
        CachedTabooWords cached = tabooWordsCache.get(orgUid);
        if (cached != null && !cached.isExpired()) {
            return cached.words;
        }
        List<String> words;
        try {
            words = tabooService.listEnabledWordsWithSynonyms(orgUid);
        } catch (Exception e) {
            log.warn("Load taboo words failed for orgUid={}: {}", orgUid, e.getMessage());
            words = Collections.emptyList();
        }
        if (words == null) {
            words = Collections.emptyList();
        }
        tabooWordsCache.put(orgUid, new CachedTabooWords(words, System.currentTimeMillis()));
        return words;
    }

    /**
     * 解析组织级 taboo 拦截文案（数据库可配置）。失败时返回 null（由 SafeGuardAdvisor 用默认文案）。
     */
    private String resolveTabooFailureResponse(String orgUid) {
        if (tabooService == null) {
            return null;
        }
        try {
            return tabooService.resolveReplyForContent(orgUid, "").orElse(null);
        } catch (Exception e) {
            log.warn("Resolve taboo failure response failed for orgUid={}: {}", orgUid, e.getMessage());
            return null;
        }
    }

    private boolean isDebugEnabled() {
        return bytedeskProperties != null && Boolean.TRUE.equals(bytedeskProperties.getDebug());
    }

    /** 敏感词缓存条目（复用 TabooFilterHelper 的 CacheEntry 模式）。 */
    private static class CachedTabooWords {
        final List<String> words;
        final long createdAtMs;

        CachedTabooWords(List<String> words, long createdAtMs) {
            this.words = words;
            this.createdAtMs = createdAtMs;
        }

        boolean isExpired() {
            return (System.currentTimeMillis() - createdAtMs) > TABOO_CACHE_TTL_MS;
        }
    }
}
