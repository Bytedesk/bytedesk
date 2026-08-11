package com.bytedesk.ai.springai.controller;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.ai.springai.service.SkillTestService;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import lombok.extern.slf4j.Slf4j;

/**
 * Skills 测试控制器 — 仅负责 HTTP 层，核心逻辑委托给 {@link SkillTestService}
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai/api/v1/skills")
@ConditionalOnBean(ChatModel.class)
public class SkillTestController {

    private final SkillTestService skillTestService;

    private final BytedeskProperties bytedeskProperties;

    public SkillTestController(SkillTestService skillTestService, BytedeskProperties bytedeskProperties) {
        this.skillTestService = skillTestService;
        this.bytedeskProperties = bytedeskProperties;
    }

    /**
     * Skills 聊天接口
     * GET http://127.0.0.1:9003/spring/ai/api/v1/skills/chat?message=Hello
     */
    @GetMapping("/chat")
    public ResponseEntity<JsonResult<?>> chat(
            @RequestParam(defaultValue = "Hello, what can you do?") String message) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available in production mode"));
        }

        try {
            log.info("Skills chat request: {}", message);
            String responseText = skillTestService.chat(message);

            return ResponseEntity.ok(JsonResult.success(Map.of(
                    "message", message,
                    "response", responseText,
                    "model", skillTestService.getChatModelDescription(),
                    "timestamp", System.currentTimeMillis())));

        } catch (Exception e) {
            log.error("Error in skills chat: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Skills chat failed: " + e.getMessage()));
        }
    }

    /**
     * 带系统提示的 Skills 聊天接口
     * GET http://127.0.0.1:9003/spring/ai/api/v1/skills/chat-with-system?message=Hello&systemPrompt=You%20are%20a%20helpful%20assistant
     */
    @GetMapping("/chat-with-system")
    public ResponseEntity<JsonResult<?>> chatWithSystem(
            @RequestParam(defaultValue = "Hello, what can you do?") String message,
            @RequestParam(defaultValue = "You are a helpful AI assistant with access to various tools and skills.") String systemPrompt) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available in production mode"));
        }

        try {
            log.info("Skills chat with system request: {}", message);
            String responseText = skillTestService.chatWithSystem(message, systemPrompt);

            return ResponseEntity.ok(JsonResult.success(Map.of(
                    "message", message,
                    "response", responseText,
                    "systemPrompt", systemPrompt,
                    "timestamp", System.currentTimeMillis())));

        } catch (Exception e) {
            log.error("Error in skills chat with system: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Skills chat failed: " + e.getMessage()));
        }
    }

    /**
     * AskUserQuestionTool 演示接口
     * <p>
     * 注册 {@code AskUserQuestionTool}，当 AI 觉得请求模糊时会主动提出澄清问题；
     * 由于 HTTP 场景没有交互式控制台，问题将由 {@code WebAutoQuestionHandler} 自动采纳推荐项。
     * <p>
     * GET http://127.0.0.1:9003/spring/ai/api/v1/skills/ask-user?message=Help%20me%20choose%20a%20database
     *
     * @param message     用户消息，建议故意模糊以触发 AI 反问
     * @param systemPrompt 可选系统提示，留空则使用鼓励提问的默认提示
     */
    @GetMapping("/ask-user")
    public ResponseEntity<JsonResult<?>> askUser(
            @RequestParam(defaultValue = "Help me set up a new web application, what do I need?") String message,
            @RequestParam(defaultValue = "") String systemPrompt) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available in production mode"));
        }

        try {
            log.info("AskUserQuestion demo request: {}", message);
            String responseText = skillTestService.chatWithAskUser(message, systemPrompt);

            return ResponseEntity.ok(JsonResult.success(Map.of(
                    "message", message,
                    "response", responseText,
                    "tool", "AskUserQuestionTool",
                    "note", "问题由 WebAutoQuestionHandler 自动采纳推荐项（见服务日志）",
                    "model", skillTestService.getChatModelDescription(),
                    "timestamp", System.currentTimeMillis())));

        } catch (Exception e) {
            log.error("Error in AskUserQuestion demo: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("AskUserQuestion demo failed: " + e.getMessage()));
        }
    }

    /**
     * 列出当前配置 skills 目录中实际存在的所有技能
     * GET http://127.0.0.1:9003/spring/ai/api/v1/skills/list
     */
    @GetMapping("/list")
    public ResponseEntity<JsonResult<?>> listSkills() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available in production mode"));
        }

        try {
            List<Map<String, String>> skillsList = skillTestService.listSkills();

            return ResponseEntity.ok(JsonResult.success(Map.of(
                    "skills", skillsList,
                    "count", skillsList.size(),
                    "dirsConfigured", skillTestService.getSkillsDirsCount(),
                    "timestamp", System.currentTimeMillis())));

        } catch (Exception e) {
            log.error("Error listing skills: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Failed to list skills: " + e.getMessage()));
        }
    }

    /**
     * 查看当前可用的 Skills 工具列表（静态描述）
     * GET http://127.0.0.1:9003/spring/ai/api/v1/skills/tools
     */
    @GetMapping("/tools")
    public ResponseEntity<JsonResult<?>> tools() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available in production mode"));
        }

        return ResponseEntity.ok(JsonResult.success(Map.of(
                "tools", List.of(
                        Map.of("name", "SkillsTool", "description", "加载并执行 SKILL.md 技能文件"),
                        Map.of("name", "ShellTools", "description", "执行 Shell 命令"),
                        Map.of("name", "FileSystemTools", "description", "文件系统读写操作"),
                        Map.of("name", "SmartWebFetchTool", "description", "智能网页内容抓取"),
                        Map.of("name", "AskUserQuestionTool", "description", "向用户提出澄清问题（/ask-user 接口演示）")),
                "skillsDirs", skillTestService.getSkillsDirsCount(),
                "timestamp", System.currentTimeMillis())));
    }
}
