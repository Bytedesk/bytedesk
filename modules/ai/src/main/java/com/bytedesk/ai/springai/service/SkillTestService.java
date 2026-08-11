package com.bytedesk.ai.springai.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springaicommunity.agent.tools.AskUserQuestionTool;
import org.springaicommunity.agent.tools.FileSystemTools;
import org.springaicommunity.agent.tools.ShellTools;
import org.springaicommunity.agent.tools.SkillsTool;
import org.springaicommunity.agent.tools.SmartWebFetchTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.springai.adviser.MyLoggingAdvisor;

import lombok.extern.slf4j.Slf4j;

/**
 * Skills 测试服务 — 封装 ChatClient 构建、技能扫描与解析的核心逻辑
 */
@Slf4j
@Service
public class SkillTestService {

    /** Primary ChatModel，用于兜底 */
    private final ChatModel primaryChatModel;

    /** 容器中所有 ChatModel，用于挑选支持工具调用的模型 */
    private final List<ChatModel> allChatModels;

    /** 优先使用的 ChatModel 简单类名，通过配置可覆盖；默认 DeepSeekChatModel */
    private final String preferredModelName;

    private final Resource[] agentSkillsDirs;

    private final ResourcePatternResolver resourcePatternResolver;

    /** YAML frontmatter 解析：提取 name 和 description */
    private static final Pattern YAML_NAME = Pattern.compile("^name:\\s*(.+)$", Pattern.MULTILINE);
    private static final Pattern YAML_DESC = Pattern.compile("^description:\\s*(.+)$", Pattern.MULTILINE);

    /** 默认 system prompt */
    private static final String DEFAULT_SYSTEM_PROMPT =
            "Always use the available skills to assist the user in their requests.";

    public SkillTestService(
            ObjectProvider<ChatModel> chatModelProvider,
            @Value("${agent.skills.dirs:classpath:/skills}") Resource[] agentSkillsDirs,
            @Value("${bytedesk.ai.skills.preferred-model:DeepSeekChatModel}") String preferredModelName,
            ResourcePatternResolver resourcePatternResolver) {
        this.primaryChatModel = chatModelProvider.getIfAvailable();
        this.allChatModels = chatModelProvider.orderedStream().toList();
        this.preferredModelName = preferredModelName;
        this.agentSkillsDirs = agentSkillsDirs;
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 选择用于 Skills 测试的 ChatModel。
     * <p>
     * 选择优先级：
     * <ol>
     *   <li>配置 {@code bytedesk.ai.skills.preferred-model} 指定的模型（默认 {@code DeepSeekChatModel}），
     *       且其 options 实现 {@code ToolCallingChatOptions}（否则工具会被 Spring AI 丢弃）。</li>
     *   <li>任意 options 实现 {@code ToolCallingChatOptions} 的模型。</li>
     *   <li>Primary ChatModel 兜底（此时工具不可用，仅纯文本对话）。</li>
     * </ol>
     * 项目自定义的 DashScope / ZhipuAI 适配模型仅实现 {@code ChatOptions}，不实现
     * {@code ToolCallingChatOptions}，会导致 {@code defaultTools(...)} 注册的工具被静默丢弃
     * （表现为 MyLoggingAdvisor 显示 "No Tools"，模型也收不到工具定义）。
     */
    private ChatModel resolveSkillsChatModel() {
        // 1) 优先匹配配置指定的模型类名
        if (preferredModelName != null && !preferredModelName.isBlank()) {
            for (ChatModel candidate : allChatModels) {
                if (candidate != null
                        && preferredModelName.equals(candidate.getClass().getSimpleName())
                        && candidate.getOptions() instanceof ToolCallingChatOptions) {
                    log.info("Skills 使用配置指定的 ChatModel: {}", candidate.getClass().getSimpleName());
                    return candidate;
                }
            }
            log.warn("未找到配置指定的 ChatModel: {}（可能未启用或其 options 不支持工具调用）", preferredModelName);
        }
        // 2) 任意支持工具调用的模型
        for (ChatModel candidate : allChatModels) {
            if (candidate != null && candidate.getOptions() instanceof ToolCallingChatOptions) {
                log.info("Skills 使用支持工具调用的 ChatModel: {}", candidate.getClass().getSimpleName());
                return candidate;
            }
        }
        // 3) Primary 兜底
        log.warn("未找到支持 ToolCallingChatOptions 的 ChatModel，回退到 Primary: {}",
                primaryChatModel != null ? primaryChatModel.getClass().getSimpleName() : "none");
        return primaryChatModel;
    }

    /**
     * 构建 Skills ChatClient。systemPrompt 为空时使用默认提示词。
     */
    private ChatClient buildSkillsChatClient(String systemPrompt) {
        ChatModel model = resolveSkillsChatModel();
        String prompt = (systemPrompt != null && !systemPrompt.isBlank())
                ? systemPrompt
                : DEFAULT_SYSTEM_PROMPT;
        return ChatClient.builder(model)
                .defaultSystem(prompt)
                .defaultTools(
                        SkillsTool.builder().addSkillsResources(List.of(agentSkillsDirs)).build(),
                        ShellTools.builder().build(),
                        FileSystemTools.builder().build(),
                        SmartWebFetchTool.builder(ChatClient.builder(model).build()).build())
                .defaultAdvisors(
                        MyLoggingAdvisor.builder()
                                .showAvailableTools(true)
                                .showSystemMessage(true)
                                .build())
                .build();
    }

    /**
     * 调用 Skills ChatClient 发送消息（使用默认 system prompt）
     */
    public String chat(String message) {
        return chatWithSystem(message, null);
    }

    /**
     * 带 system prompt 的聊天
     */
    public String chatWithSystem(String message, String systemPrompt) {
        ChatClient client = buildSkillsChatClient(systemPrompt);
        return client.prompt()
                .user(message)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
    }

    /**
     * 注册 {@link AskUserQuestionTool} 的演示聊天。
     * <p>
     * 在 HTTP 请求线程中没有交互式控制台，因此使用 {@link WebAutoQuestionHandler}
     * 自动采纳每个问题的推荐选项（列表第一项），保证演示端到端可跑通，
     * 同时在日志中输出 AI 提出的澄清问题，便于观察 Agent 的提问行为。
     *
     * @param message     用户消息（建议故意模糊，以触发 AI 反问）
     * @param systemPrompt 系统提示，为空时使用专门鼓励提问的默认提示
     */
    public String chatWithAskUser(String message, String systemPrompt) {
        ChatModel model = resolveSkillsChatModel();
        String prompt = (systemPrompt != null && !systemPrompt.isBlank())
                ? systemPrompt
                : "You are a helpful AI assistant. When the user's request is ambiguous, "
                        + "use the AskUserQuestionTool to clarify their preferences before answering.";

        AskUserQuestionTool askUserTool = AskUserQuestionTool.builder()
                .questionHandler(new WebAutoQuestionHandler())
                .answersValidation(false)
                .build();

        ChatClient client = ChatClient.builder(model)
                .defaultSystem(prompt)
                .defaultTools(
                        askUserTool,
                        SkillsTool.builder().addSkillsResources(List.of(agentSkillsDirs)).build(),
                        ShellTools.builder().build(),
                        FileSystemTools.builder().build(),
                        SmartWebFetchTool.builder(ChatClient.builder(model).build()).build())
                .defaultAdvisors(
                        MyLoggingAdvisor.builder()
                                .showAvailableTools(true)
                                .showSystemMessage(true)
                                .build())
                .build();

        log.info("AskUserQuestion 演示请求: {}", message);
        return client.prompt()
                .user(message)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
    }

    /**
     * 扫描配置的 skills 目录，返回实际存在的技能列表。
     * 使用 classpath*: 模式直接扫描，避免 ClassPathResource 对目录 exists() 判断失败的问题。
     */
    public List<Map<String, String>> listSkills() {
        List<Map<String, String>> skillsList = new ArrayList<>();

        for (Resource dir : agentSkillsDirs) {
            try {
                // 统一转为 classpath*: 模式扫描，避免 ClassPathResource 目录 exists() 不可靠
                String location;
                if (dir instanceof org.springframework.core.io.ClassPathResource cpr) {
                    String path = cpr.getPath();
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    location = "classpath*:/" + path + "/*/SKILL.md";
                } else {
                    String uri = dir.getURI().toString();
                    location = uri + (uri.endsWith("/") ? "" : "/") + "*/SKILL.md";
                }

                Resource[] skillFiles;
                try {
                    skillFiles = resourcePatternResolver.getResources(location);
                } catch (Exception e) {
                    log.debug("Failed to scan skills at pattern: {}", location, e);
                    continue;
                }

                for (Resource skillFile : skillFiles) {
                    String skillName = extractParentDirName(skillFile);
                    String description = parseSkillDescription(skillFile);

                    skillsList.add(Map.of(
                            "name", skillName,
                            "description", description != null ? description : "",
                            "path", skillFile.getURI().toString()));
                }
            } catch (Exception e) {
                log.debug("Failed to scan skills dir: {}", dir, e);
            }
        }

        return skillsList;
    }

    /**
     * 获取配置的 skills 目录数量
     */
    public int getSkillsDirsCount() {
        return agentSkillsDirs.length;
    }

    /**
     * 获取实际用于 Skills 的 ChatModel 描述
     */
    public String getChatModelDescription() {
        ChatModel model = resolveSkillsChatModel();
        return model != null ? model.toString() : "no ChatModel available";
    }

    /**
     * 从 SKILL.md 文件路径中提取父目录名作为 skill 名称
     */
    private String extractParentDirName(Resource skillFile) {
        try {
            String uri = skillFile.getURI().toString();
            // classpath:/skills/ai-tutor/SKILL.md → ai-tutor
            String[] parts = uri.replace('\\', '/').split("/");
            if (parts.length >= 2) {
                return parts[parts.length - 2];
            }
        } catch (Exception e) {
            log.debug("Failed to extract dir name from: {}", skillFile, e);
        }
        return "unknown";
    }

    /**
     * 解析 SKILL.md 的 YAML frontmatter 中 description 字段
     */
    private String parseSkillDescription(Resource skillFile) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(skillFile.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder yamlBlock = new StringBuilder();
            String line;
            boolean inYaml = false;
            int yamlDelimCount = 0;

            while ((line = reader.readLine()) != null && yamlDelimCount < 2) {
                if (line.trim().equals("---")) {
                    yamlDelimCount++;
                    if (yamlDelimCount == 1) {
                        inYaml = true;
                        continue;
                    } else {
                        break;
                    }
                }
                if (inYaml) {
                    yamlBlock.append(line).append('\n');
                }
            }

            String yaml = yamlBlock.toString();
            Matcher descMatcher = YAML_DESC.matcher(yaml);
            if (descMatcher.find()) {
                return descMatcher.group(1).trim();
            }

            Matcher nameMatcher = YAML_NAME.matcher(yaml);
            if (nameMatcher.find()) {
                return nameMatcher.group(1).trim();
            }
        } catch (Exception e) {
            log.debug("Failed to parse SKILL.md: {}", skillFile, e);
        }
        return null;
    }
}
